(ns flexql.db.schema.designer
  (:require [honey.sql.helpers :refer [create-table drop-table with-columns]]))

(def destroy
  (drop-table :if-exists :designer))

(def create
  (create-table
   :designer
   (with-columns
     [:id :uuid [:not nil] [:primary-key]]
     [:name :text [:not nil]]
     [:uri :text]
     [:created-at :timestamp]
     [:updated-at :timestamp])))
