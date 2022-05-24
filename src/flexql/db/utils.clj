(ns flexql.db.utils
  (:import (java.sql Timestamp)
           (java.time Instant)
           (java.util UUID)))

(defn now [] (Timestamp/from (Instant/now)))

(defn ->UUID
  [uuid]
  (if (string? uuid)
    (UUID/fromString uuid)
    uuid))