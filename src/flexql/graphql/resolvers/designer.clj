(ns flexql.graphql.resolvers.designer
  (:require [flexql.db.core :as db]
            [honey.sql.helpers :as sqlh]))

(defn index [{:keys [dbconn]} _ _]
  (db/execute! dbconn (-> (sqlh/select :*) (sqlh/from :designer))))