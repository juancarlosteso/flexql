(ns flexql.db.seeds.designer
  (:require [flexql.model.designer :refer [->designer]]
            [honey.sql.helpers :refer [insert-into truncate values]]))

(def clear
  (truncate :designer))

(def seeds
  {:burm    (->designer "Kris Burm" "http://www.gipf.com/project_gipf/burm/burm.html")
   :bauza   (->designer "Antoine Bauza" "http://www.antoinebauza.fr")
   :cathala (->designer "Bruno Cathala" "http://www.brunocathala.com")
   :almes   (->designer "Scott Almes" nil)
   :donald  (->designer "Donald X. Vaccarino" nil)})

(def feed
  (-> (insert-into :designer)
      (values (vals seeds))))
