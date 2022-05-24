(ns flexql.graphql.resolvers
  (:require [flexql.config :as cfg]
            [flexql.db.core :as db]
            [honey.sql.helpers :as sqlh]))

(defn- game-index [_ _ _]
  (let [connection (-> cfg/config :db db/connection)]
    (db/execute! connection (-> (sqlh/select :*) (sqlh/from :board-game)))))

(defn- member-index [_ _ _]
  (let [connection (-> cfg/config :db db/connection)]
    (db/execute! connection (-> (sqlh/select :*) (sqlh/from :member)))))

(defn- designer-index [_ _ _]
  (let [connection (-> cfg/config :db db/connection)]
    (db/execute! connection (-> (sqlh/select :*) (sqlh/from :designer)))))

(def resolvers-map
  {:games/all game-index
   :designers/all designer-index
   :members/all member-index})