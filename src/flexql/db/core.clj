(ns flexql.db.core
  (:require [honey.sql :as sql]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs])
  (:import (java.sql ResultSetMetaData)))

(extend-protocol rs/ReadableColumn
  java.util.UUID
  (read-column-by-label [v _] (str v))
  (read-column-by-index [v ^ResultSetMetaData _ _] (str v)))

(defn- go! [connection sentence]
  (jdbc/execute! connection sentence {:builder-fn rs/as-unqualified-lower-maps}))

(defn execute! [connection sentence]
  (->> sentence
       sql/format
       (go! connection)))

(defn connection [config]
  (-> config
      jdbc/get-datasource
      jdbc/get-connection))