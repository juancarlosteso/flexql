(ns flexql.graphql.resolvers.board-game
  (:require [com.walmartlabs.lacinia.resolve :refer [resolve-as]]
            [flexql.model.board-game :as game]
            [flexql.model.designer :as designer]
            [flexql.model.game-rating :as ratings]))

(defn index [{:keys [dbconn]} _ _]
  (game/get-all dbconn))

(defn find-by-id [{:keys [dbconn]} {:keys [id name]} _]
  (cond
    id    (game/with-id dbconn id)
    name  (game/with-name dbconn name)
    :else (resolve-as nil {:message "You need to provide an id or a name"
                           :status 400})))

(defn avg-rating-for [{:keys [dbconn]} _ {:keys [id]}]
  (ratings/avg-rating-for dbconn id))

(defn ratings [{:keys [dbconn]} _ {:keys [id] :as game}]
  (letfn [(->rating [{:keys [rating] :as r}]
            {:game game
             :rating rating
             :member (dissoc r :rating)})]
    (map ->rating (ratings/ratings-for dbconn id))))

(defn rate [{:keys [dbconn]} {:keys [game_id member_id rating]} _]
  (if (ratings/rating-for-by dbconn game_id member_id)
    (ratings/change-rating dbconn game_id member_id rating)
    (ratings/create-rating dbconn game_id member_id rating))
  (game/with-id dbconn game_id))

(defn designers [{:keys [dbconn]} _ {:keys [id]}]
  (designer/of dbconn id))

(defn create [{:keys [dbconn]} {:keys [name summary min_players max_players]} _]
  (game/create dbconn name summary min_players max_players))

(defn add-designer [{:keys [dbconn]} {:keys [game_id designer_id]} _]
  (game/add-designer dbconn game_id designer_id)
  (game/with-id dbconn game_id))