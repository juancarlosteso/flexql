(ns flexql.graphql.resolvers.board-game
  (:require [flexql.db.core :as db]
            [flexql.db.utils :refer [->UUID now]]
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

(defn- rating-selector [member-id game-id]
  [:and
   [:= :game-id (->UUID game-id)]
   [:= :member-id (->UUID member-id)]])

(defn- select-game-rating-sql [member-id game-id]
  (-> (sqlh/select :*)
      (sqlh/from :game-rating)
      (sqlh/where (rating-selector member-id game-id))))

(defn- rated? [dbconn {:keys [member_id game_id]}]
  (->> (select-game-rating-sql member_id game_id)
       (db/execute! dbconn)
       count
       (= 1)))

(defn- update-rating-sql [{:keys [member_id game_id rating]}]
  (let [timestamp (now)]
    (-> (sqlh/update :game-rating)
        (sqlh/set {:rating rating :updated-at timestamp})
        (sqlh/where (rating-selector member_id game_id)))))

(defn- insert-rating-sql [new-rating]
  (let [timestamp (now)]
    (-> (sqlh/insert-into :game-rating)
        (sqlh/values [(-> new-rating
                          (assoc :created-at timestamp)
                          (assoc :updated-at timestamp)
                          (update :game_id ->UUID)
                          (update :member_id ->UUID))]))))

(defn rate [{:keys [dbconn] :as context} {:keys [game_id] :as input} _]
  (db/execute! dbconn (if (rated? dbconn input)
                        (update-rating-sql input)
                        (insert-rating-sql input)))
  (find-by-id context {:id game_id} nil))