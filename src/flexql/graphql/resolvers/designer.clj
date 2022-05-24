(ns flexql.graphql.resolvers.designer
  (:require [com.walmartlabs.lacinia.resolve :refer [resolve-as]]
            [flexql.model.board-game :as game]
            [flexql.model.designer :as designer]))

(defn index [{:keys [dbconn]} _ _]
  (designer/get-all dbconn))

(defn find-by-id [{:keys [dbconn]} {:keys [id name]} _]
  (cond
    id    (designer/with-id dbconn id)
    name  (designer/with-name dbconn name)
    :else (resolve-as nil {:message "You need to provide an id or a name."
                           :status 400})))

(defn games [{:keys [dbconn]} _ {:keys [id]}]
  (game/designed-by dbconn id))

(defn create [{:keys [dbconn]} {:keys [name uri]} _]
  (designer/create dbconn name uri))