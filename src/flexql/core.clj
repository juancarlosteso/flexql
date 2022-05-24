(ns flexql.core
  (:require [flexql.config :as cfg]
            [flexql.db.core :as db]
            [flexql.graphql.schema :as s]
            [flexql.server :as server]))

(defn -main []
  (server/start (s/initialize)
                (-> cfg/config :db db/connection)))