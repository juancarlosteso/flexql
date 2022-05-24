(ns flexql.graphql.resolvers.member
  (:require [com.walmartlabs.lacinia.resolve :refer [resolve-as]]
            [flexql.model.member :as member]
            [flexql.model.game-rating :as ratings]))

(defn index [{:keys [dbconn]} _ _]
  (member/get-all dbconn))

(defn find-by-id [{:keys [dbconn]} {:keys [id name]} _]
  (cond
    id    (member/with-id dbconn id)
    name  (member/with-name dbconn name)
    :else (resolve-as nil {:message "You need to provide an id or a name"
                           :status 400})))

(defn ratings [{:keys [dbconn]} _ {:keys [id] :as member}]
  (letfn [(->rating [{:keys [rating] :as r}]
                    {:game (dissoc r :rating)
                     :member member
                     :rating rating})]
    (map ->rating (ratings/rated-by dbconn id))))

(defn create [{:keys [dbconn]} {:keys [name]} _]
  (member/create dbconn name))