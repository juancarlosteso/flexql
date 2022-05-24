(ns flexql.graphql.resolvers.designer
  (:require [flexql.db.core :as db]
            [flexql.db.utils :refer [->UUID]]
            [honey.sql.helpers :as sqlh]))

(def ^:private base-select (-> (sqlh/select :*) (sqlh/from :designer)))

(defn index [{:keys [dbconn]} _ _]
  (db/execute! dbconn base-select))

(defn find-by-id [{:keys [dbconn]} {:keys [id]} _]
  (let [sql (-> base-select (sqlh/where [:= :id (->UUID id)]))]
    (->> sql
         (db/execute! dbconn)
         first)))