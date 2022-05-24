(ns flexql.model.board-game
  (:require [flexql.db.core :as db]
            [flexql.db.utils :refer [->UUID now]]
            [honey.sql.helpers :as sqlh]))

(def ^:private base-select (-> (sqlh/select :*) (sqlh/from :board-game)))

(defn get-all [dbconn]
  (db/execute! dbconn base-select))

(defn with-id [dbconn id]
  (let [sql (-> base-select (sqlh/where [:= :id (->UUID id)]))]
    (->> sql
         (db/execute! dbconn)
         first)))

(defn with-name [dbconn text]
  (let [sql (-> base-select (sqlh/where [:= :name text]))]
    (->> sql
         (db/execute! dbconn)
         first)))

(defn designed-by [dbconn designer-id]
  (let [sql (-> (sqlh/select :bg.*)
                (sqlh/from [:game-designer :gd])
                (sqlh/join [:board-game :bg] [:= :gd.game-id :bg.id])
                (sqlh/where [:= :gd.designer-id (->UUID designer-id)]))]
    (db/execute! dbconn sql)))

(defn ->board-game [name summary min-players max-players]
  (let [timestamp (now)]
    {:id (random-uuid)
     :name name
     :summary summary
     :min-players min-players
     :max-players max-players
     :created-at timestamp
     :updated-at timestamp}))

(defn create [dbconn name summary min-players max-players]
  (let [new-game (->board-game name summary min-players max-players)
        sql (-> (sqlh/insert-into :board-game)
                (sqlh/values [new-game]))]
    (db/execute! dbconn sql)
    (update new-game :id str)))

(defn add-designer [dbconn game-id designer-id]
  (let [sql (-> (sqlh/insert-into :game-designer)
                (sqlh/values [{:game-id (->UUID game-id) :designer-id (->UUID designer-id)}]))]
    (db/execute! dbconn sql)))