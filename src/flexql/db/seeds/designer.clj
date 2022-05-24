(ns flexql.db.seeds.designer
  (:require [flexql.db.utils :refer [now]]
   [honey.sql.helpers :refer [insert-into truncate values]]))

(def clear
  (truncate :designer))

(defn- new-designer [name uri]
  (let [timestamp (now)]
    {:id (random-uuid)
     :name name
     :uri uri
     :created-at timestamp
     :updated-at timestamp}))

(def seeds
  {:burm    (new-designer "Kris Burm" "http://www.gipf.com/project_gipf/burm/burm.html")
   :bauza   (new-designer "Antoine Bauza" "http://www.antoinebauza.fr")
   :cathala (new-designer "Bruno Cathala" "http://www.brunocathala.com")
   :almes   (new-designer "Scott Almes" nil)
   :donald  (new-designer "Donald X. Vaccarino" nil)})

(def feed
  (-> (insert-into :designer)
      (values (vals seeds))))
