(ns flexql.db.schema.member
  (:require [honey.sql.helpers :refer [create-table drop-table with-columns]]))

(def destroy
  (drop-table :if-exists :member))

(def create
  (create-table
   :member
   (with-columns
     [:id :uuid [:not nil]]
     [:name :text [:not nil]]
     [:created-at :timestamp]
     [:updated-at :timestamp])))