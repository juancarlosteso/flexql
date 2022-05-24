(ns flexql.model.designer
  (:require [flexql.db.core :as db]
            [flexql.db.utils :refer [->UUID now]]
            [honey.sql.helpers :as sqlh]))

(def ^:private base-select (-> (sqlh/select :*) (sqlh/from :designer)))

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

(defn of [dbconn game-id]
  (let [sql (-> (sqlh/select :d.*)
                (sqlh/from [:game-designer :gd])
                (sqlh/join [:designer :d] [:= :gd.designer-id :d.id])
                (sqlh/where [:= :gd.game-id (->UUID game-id)]))]
    (db/execute! dbconn sql)))

(defn ->designer [name uri]
  (let [timestamp (now)]
    {:id (random-uuid)
     :name name
     :uri uri
     :created-at timestamp
     :updated-at timestamp}))

(defn create [dbconn name uri]
  (let [new-designer (->designer name uri)
        sql (-> (sqlh/insert-into :designer)
                (sqlh/values [new-designer]))]
    (db/execute! dbconn sql)
    (update new-designer :id str)))