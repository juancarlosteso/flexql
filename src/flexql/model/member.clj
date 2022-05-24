(ns flexql.model.member
  (:require [flexql.db.core :as db]
            [flexql.db.utils :refer [->UUID now]]
            [honey.sql.helpers :as sqlh]))

(def ^:private base-select (-> (sqlh/select :*) (sqlh/from :member)))

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

(defn ->member [name]
  (let [timestamp (now)]
    {:id (random-uuid)
     :name name
     :created-at timestamp
     :updated-at timestamp}))

(defn create [dbconn name]
  (let [new-member (->member name)
        sql (-> (sqlh/insert-into :designer)
                (sqlh/values [new-member]))]
    (db/execute! dbconn sql)
    (update new-member :id str)))