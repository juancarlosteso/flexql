(ns flexql.db.seeder
  (:require [flexql.config :as cfg]
            [flexql.db.core :as db]
            [flexql.db.seeds.board-game :as game]
            [flexql.db.seeds.designer :as designer]
            [flexql.db.seeds.game-designer :as game-designer]
            [flexql.db.seeds.game-rating :as game-rating]
            [flexql.db.seeds.member :as member]))

(defn- clear! [connection]
  (print "Clearing data... ")
  (db/execute! connection game-designer/clear)
  (db/execute! connection game-rating/clear)
  (db/execute! connection member/clear)
  (db/execute! connection game/clear)
  (db/execute! connection designer/clear)
  (println "data cleared.")
  connection)

(defn- seed! [connection]
  (print "Creating initial data for:")
  (print " Board games")
  (db/execute! connection game/feed)
  (print ", Members")
  (db/execute! connection member/feed)
  (print ", Designers")
  (db/execute! connection designer/feed)
  (print ", Game designers")
  (db/execute! connection game-designer/feed)
  (print ", Game ratings")
  (db/execute! connection game-rating/feed)
  (println ". Initial data created.")
  connection)

(defn -main []
  (-> cfg/config
      :db
      db/connection
      clear!
      seed!))