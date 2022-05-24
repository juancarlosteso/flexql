(ns flexql.db.builder
  (:require [flexql.config :as cfg]
            [flexql.db.core :as db]
            [flexql.db.schema.designer :as designer]
            [flexql.db.schema.board-game :as game]
            [flexql.db.schema.game-designer :as game-designer]
            [flexql.db.schema.game-rating :as game-rating]
            [flexql.db.schema.member :as member]))

(defn- down! [connection]
  (print "Dropping tables... ")
  (db/execute! connection game-designer/destroy)
  (db/execute! connection game-rating/destroy)
  (db/execute! connection member/destroy)
  (db/execute! connection game/destroy)
  (db/execute! connection designer/destroy)
  (println "tables dropped.")
  connection)


(defn- up! [connection]
  (print "Creating tables:")
  (print " Members")
  (db/execute! connection member/create)
  (print ", Board games")
  (db/execute! connection game/create)
  (print ", Designers")
  (db/execute! connection designer/create)
  (print ", Game ratings")
  (db/execute! connection game-rating/create)
  (print ", Game designers")
  (db/execute! connection game-designer/create)
  (println ".\nTables created.")
  connection)

(defn -main []
  (let [{:keys [db]} cfg/config]
    (-> (db/connection db)
        down!
        up!)))