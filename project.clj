(defproject flexql "0.1.0-SNAPSHOT"
  :description "Flexiana GraphQL workshop"
  :min-lein-version "2.0.0"
  :plugins [[lein-tools-deps "0.4.5"]]
  :middleware [lein-tools-deps.plugin/resolve-dependencies-with-deps-edn]
  :lein-tools-deps/config {:config-files [:install :user :project]}
  :main flexql.core
  :repl-options {:init-ns flexql.dev}
  :aliases
  {"db.rebuild" ["run" "-m" "flexql.db.builder"]
   "db.reseed"  ["run" "-m" "flexql.db.seeder"]})
