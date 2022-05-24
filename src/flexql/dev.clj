(ns flexql.dev
  (:require [com.walmartlabs.lacinia :as lacinia]
            [flexql.graphql.schema :as schema]))

(defn run [query]
  (lacinia/execute (schema/initialize) query nil nil))