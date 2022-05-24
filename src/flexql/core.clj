(ns flexql.core
  (:require [clojure.pprint :as pprint]
            [flexql.config :as cfg]
            [flexql.db.core :as db]))

(defn -main
  [& _args]
  (let [db-connection (-> cfg/config :db db/connection)]
    (pprint/pprint (db/execute! db-connection {:select [:*] :from [:board_game]}))))