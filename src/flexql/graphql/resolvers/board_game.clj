(ns flexql.graphql.resolvers.board-game
  (:require [flexql.db.core :as db]
            [flexql.db.utils :refer [->UUID]]
            [honey.sql.helpers :as sqlh]))

(def ^:private base-select (-> (sqlh/select :*) (sqlh/from :board-game)))

(defn index [{:keys [dbconn]} _ _]
  (db/execute! dbconn base-select))

(defn find-by-id [{:keys [dbconn]} {:keys [id]} _]
  (let [sql (-> base-select (sqlh/where [:= :id (->UUID id)]))]
    (->> sql
         (db/execute! dbconn)
         first)))

(defn avg-rating-for [{:keys [dbconn]} _ {:keys [id]}]
  (let [sql (-> (sqlh/select [[:avg :rating] :average])
                (sqlh/from :game-rating)
                (sqlh/where [:= :game-id (->UUID id)]))]
    (->> sql
         (db/execute! dbconn)
         first
         :average)))

(defn ratings [{:keys [dbconn]} _ {:keys [id] :as game}]
  (let [sql (-> (sqlh/select :gr.rating :m.*)
                (sqlh/from [:game-rating :gr])
                (sqlh/join [:member :m] [:= :m.id :gr.member_id])
                (sqlh/where [:= :gr.game_id (->UUID id)]))
        results (db/execute! dbconn sql)
        ->rating (fn [{:keys [rating] :as r}]
                   {:game game
                    :rating rating
                    :member (dissoc r :rating)})]
    (map ->rating results)))