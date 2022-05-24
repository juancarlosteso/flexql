(ns flexql.graphql.schema
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [com.walmartlabs.lacinia.util :as u]
            [com.walmartlabs.lacinia.schema :as sch]
            [flexql.graphql.resolvers :refer [resolvers-map]]))

(defn initialize []
  (-> "graphql/schema.edn"
      io/resource
      slurp
      edn/read-string
      (u/attach-resolvers resolvers-map)
      sch/compile))