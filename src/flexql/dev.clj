(ns flexql.dev
  (:require [com.walmartlabs.lacinia :as lacinia]
            [flexql.config :as cfg]
            [flexql.db.core :as db]
            [flexql.graphql.schema :as schema]))

(defn run [query]
  (let [connection (-> cfg/config :db db/connection)]
    (lacinia/execute (schema/initialize)
                     query
                     nil
                     {:dbconn connection})))