(ns flexql.server
  (:require [clojure.data.json :as json]
            [com.walmartlabs.lacinia :as lacinia]
            [flexql.config :as cfg]
            [reitit.ring :as ring]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.content-type :as content-type]
            [ring.middleware.not-modified :as not-modified]
            [ring.middleware.resource :as resource]
            [ring.util.request :as request]))

(defn- handler [schema db-connection request]
  (let [{:keys [query variables]} (json/read-str (request/body-string request)
                                                 :key-fn keyword)
        result                    (lacinia/execute schema
                                                   query
                                                   variables
                                                   {:dbconn db-connection})]
    {:status 200
     :body (json/write-str result)
     :headers {"Content-Type" "application/json"}}))

(defn- app [schema db-connection]
  (let [handler-fn (partial handler schema db-connection)]
    (-> (ring/ring-handler (ring/router ["/graphql" {:post handler-fn}]))
        (resource/wrap-resource "static")
        content-type/wrap-content-type
        not-modified/wrap-not-modified)))

(defn start [schema db-connection]
  (jetty/run-jetty (app schema db-connection)
                   {:join? false :port (-> cfg/config :http :port)}))