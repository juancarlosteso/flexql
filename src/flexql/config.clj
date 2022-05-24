(ns flexql.config
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]))

(def config
  (-> "config.edn"
      io/resource
      io/file
      slurp
      edn/read-string))