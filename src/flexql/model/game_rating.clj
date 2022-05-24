(ns flexql.model.game-rating
  (:require [flexql.db.core :as db]
            [flexql.db.utils :refer [->UUID now]]
            [honey.sql.helpers :as sqlh]))

(defn ratings-for [dbconn game-id]
  (let [sql (-> (sqlh/select :gr.rating :m.*)
                (sqlh/from [:game-rating :gr])
                (sqlh/join [:member :m] [:= :m.id :gr.member_id])
                (sqlh/where [:= :gr.game_id (->UUID game-id)]))]
    (db/execute! dbconn sql)))

(defn avg-rating-for [dbconn game-id]
  (let [sql (-> (sqlh/select [[:avg :rating] :average])
                (sqlh/from :game-rating)
                (sqlh/where [:= :game-id (->UUID game-id)]))]
    (->> sql
         (db/execute! dbconn)
         first
         :average)))

(defn- rating-selector [member-id game-id]
  [:and
   [:= :game-id (->UUID game-id)]
   [:= :member-id (->UUID member-id)]])

(defn rating-for-by [dbconn game-id member-id]
  (let [sql (-> (sqlh/select :*)
                (sqlh/from :game-rating)
                (sqlh/where (rating-selector member-id game-id)))]
    (->> sql
         (db/execute! dbconn)
         first)))

(defn change-rating [dbconn game-id member-id rating]
  (let [timestamp (now)
        sql (-> (sqlh/update :game-rating)
                (sqlh/set {:rating rating :updated-at timestamp})
                (sqlh/where (rating-selector member-id game-id)))]
    (db/execute! dbconn sql)))

(defn create-rating [dbconn game-id member-id rating]
  (let [timestamp (now)
        new-rating {:game_id (->UUID game-id)
                    :member_id (->UUID member-id)
                    :rating rating
                    :created-at timestamp
                    :updated-at timestamp}
        sql (-> (sqlh/insert-into :game-rating)
                (sqlh/values [new-rating]))]
    (db/execute! dbconn sql)))

(defn rated-by [dbconn member-id]
  (let [sql (-> (sqlh/select :gr.rating :bg.*)
                (sqlh/from [:game-rating :gr])
                (sqlh/join [:board-game :bg] [:= :gr.game-id :bg.id])
                (sqlh/where [:= :gr.member-id (->UUID member-id)]))]
    (db/execute! dbconn sql)))