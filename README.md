# flexql

A GraphQL example to be used in one of Flexiana Clojure Guild workshops.

## Requirements
This workshop requires you to have installed:
* Clojure (both `clj` *and* `clojure`)
* Leiningen v2 or above
* Docker & Docker Compose

## Usage

1. Clone this repository.

1. Start the database:
    ```
    docker-compose up -d
    ```
1. Create the database schema:
    ```
    lein db.rebuild
    ```
1. Populate the database tables with sample data:
    ```
    lein db.reseed
    ```

1. Check everything is running ok:
    ```
    lein run
    ```
    you should see an output similar to this one:
    ```edn
    [{:id "80033147-a976-463e-b29b-0b2ee78bb722",
      :name "Zertz",
      :summary "Two player abstract with forced moves and shrinking board",
      :min_players 2,
      :max_players 2,
      :created_at #inst "2022-05-23T08:15:19.728348000-00:00",
      :updated_at #inst "2022-05-23T08:15:19.728348000-00:00"}
     {:id "ef71f7f2-4c58-46c4-b74e-30a13dea1385",
      :name "Dominion",
      :summary "Created the deck-building genre; zillions of expansions",
      :min_players 2,
      :max_players nil,
      :created_at #inst "2022-05-23T08:15:19.728408000-00:00",
      :updated_at #inst "2022-05-23T08:15:19.728408000-00:00"}
     {:id "b1087012-2449-4eeb-8b71-5e0b5fe0ce59",
      :name "Tiny Epic Galaxies",
      :summary "Fast dice-based sci-fi space game with a bit of chaos",
      :min_players 1,
      :max_players 4,
      :created_at #inst "2022-05-23T08:15:19.728413000-00:00",
      :updated_at #inst "2022-05-23T08:15:19.728413000-00:00"}
     {:id "b2105903-043e-47a2-8902-0ddeca2e6b32",
      :name "7 Wonders: Duel",
      :summary "Tense, quick card game of developing civilizations",
      :min_players 2,
      :max_players 2,
      :created_at #inst "2022-05-23T08:15:19.728417000-00:00",
      :updated_at #inst "2022-05-23T08:15:19.728417000-00:00"}]
    ```
## The workshop

### Step 1: Retrieve the list of games
1. Create a file named `resources/graphql/schema.edn` and type this code inside it:

    ```edn
    {:objects
     {:BoardGame
      {:description "A physical or virtual board game."
       :fields
       {:id          {:type (non-null ID)}
        :name        {:type (non-null String)}
        :summary     {:type String
                      :description "A one-line summary of the game."}
        :min_players {:type Int
                      :description "The minimum number of players the game supports."}
        :max_players {:type Int
                      :description "The maximum number of players the game supports."}}}}}
    ```
    We just described the information that can be queried for any `BoardGame`.

1. Extend the schema by defining the query to recover all board games:

    ```edn
    {:objects
     {:BoardGame
      {:description "A physical or virtual board game."
       :fields
       {:id          {:type (non-null ID)}
        :name        {:type (non-null String)}
        :summary     {:type String
                      :description "A one-line summary of the game."}
        :min_players {:type Int
                      :description "The minimum number of players the game supports."}
        :max_players {:type Int
                      :description "The maximum number of players the game supports."}}}}
     :queries
     {:all_games
      {:type (list :BoardGame)
       :description "All of the board games."
       :resolve :games/all}}}
    ```
    We declared an `all_games` query which will return a `list` of `BoardGame` objects, and delegated the gathering of the results to a *resolver* function identified with the key `:games/all`.

1. Create a file named `src/flexql/graphql/resolvers.clj` and type this code inside it:

    ```clj
    (ns flexql.graphql.resolvers
      (:require [flexql.config :as cfg]
                [flexql.db.core :as db]
                [honey.sql.helpers :as sqlh]))

    (defn- game-index [_ _ _]
      (let [connection (-> cfg/config :db db/connection)]
        (db/execute! connection (-> (sqlh/select :*) (sqlh/from :board-game)))))

    (def resolvers-map
      {:games/all game-index})
    ```
    We created the *resolver* function we have previously associated to the `all_games` query. Resolver function always get three parameters that will be explained in detail in future steps. For now, we only need to remember that the resolver return value **must** match the schema definition.

1. Create a file named `src/flexql/graphql/schema.clj` and type this code inside it:

    ```clj
    (ns flexql.graphql.schema
      (:require [clojure.edn :as edn]
                [clojure.java.io :as io]
                [com.walmartlabs.lacinia.util :as u]
                [com.walmartlabs.lacinia.schema :as sch]
                [flexql.graphql.resolvers :refer [resolvers-map]]))

    (defn initialize []
      (-> "graphql/schema.edn"
        io/resource
        slurp
        edn/read-string
        (u/attach-resolvers resolvers-map)
        sch/compile))
    ```
    We made Lacinia compile our GraphQL schema and attach the resolver functions referenced by the map, so now we are ready to tell Lacinia to parse and execute queries.

1. Create `src/flexql/dev.clj` and type this code inside it:

    ```clj
    (ns flexql.dev
      (:require [com.walmartlabs.lacinia :as lacinia]
                [flexql.graphql.schema :as schema]))

    (defn run [query]
      (lacinia/execute (schema/initialize) query nil nil))
    ```
    This code prepares the compiled schema by calling `schema/initialize` and uses it to execute the query received by the `run` function.

1. Modify your `project.clj` to change your `repl-options` like this:
    ```edn
    :repl-options {:init-ns flexql.dev}
    ```

1. Check that everything works by running `lein repl` and making the following call to `run`:

    ```clj
    (clojure.pprint/pprint (run "{all_games {id name}}"))
    ```

    That query means "please tell me the id and name of all games", and it should print out a result similar to this one:

    ```edn
    {:data
     {:all_games
      [{:id "80033147-a976-463e-b29b-0b2ee78bb722", :name "Zertz"}
       {:id "ef71f7f2-4c58-46c4-b74e-30a13dea1385", :name "Dominion"}
       {:id "b1087012-2449-4eeb-8b71-5e0b5fe0ce59",
        :name "Tiny Epic Galaxies"}
       {:id "b2105903-043e-47a2-8902-0ddeca2e6b32",
        :name "7 Wonders: Duel"}]}}
    ```

1. Experiment with different queries. Ask for more fields. Ask for fields that do not exist, and see what happens.

### Step 2: Retrieve also the lists of Designers and Members
1. Modify `resources/graphql/schema.edn` to make it look like this:

    ```edn
    {:objects
     {:BoardGame
      {:description "A physical or virtual board game."
       :fields
       {:id          {:type (non-null ID)}
        :name        {:type (non-null String)}
        :summary     {:type String
                      :description "A one-line summary of the game."}
        :min_players {:type Int
                      :description "The minimum number of players the game supports."}
        :max_players {:type Int
                      :description "The maximum number of players the game supports."}}}
      :Designer
      {:description "A person who may have contributed to a board game design."
       :fields
       {:id          {:type (non-null ID)}
        :name        {:type (non-null String)}
        :uri         {:type String
                      :description "Home page URL, if known."}}}
      :Member
      {:description "A registered user."
       :fields
       {:id          {:type (non-null ID)}
        :name        {:type (non-null String)}}}}
     :queries
     {:all_games
      {:type (list :BoardGame)
       :description "All of the board games."
       :resolve :games/all}
      :all_designers
      {:type (list :Designer)
       :description "All of the designers."
       :resolve :designers/all}
      :all_members
      {:type (list :Member)
       :description "All of the members."
       :resolve :members/all}}}
    ```
    We have now defined our 3 main entities and queries to get a list of each.

1. Modify `src/flexql/graphql/resolvers.clj` and add this resolver functions:

    ```clj
    (defn- member-index [_ _ _]
      (let [connection (-> cfg/config :db db/connection)]
        (db/execute! connection (-> (sqlh/select :*) (sqlh/from :member)))))

    (defn- designer-index [_ _ _]
      (let [connection (-> cfg/config :db db/connection)]
        (db/execute! connection (-> (sqlh/select :*) (sqlh/from :designer)))))
    ```

    Also, add the entries to the resolvers map, to make it look like this:

    ```clj
    (def resolvers-map
      {:games/all game-index
       :designers/all designer-index
       :members/all member-index})
    ```
1. Try your new queries to ensure they work as intended.

### Step 2.5: Using application context & cleaning up code
1. Modify `src/flexql/dev.clj` to make it look like this:

    ```clj
    (ns flexql.dev
      (:require [com.walmartlabs.lacinia :as lacinia]
                [flexql.config :as cfg]
                [flexql.db.core :as db]
                [flexql.graphql.schema :as schema]))
    
    (defn run [query]
      (let [connection (-> cfg/config :db db/connection)]
        (lacinia/execute (schema/initialize)
                         query
                         nil
                         {:dbconn connection})))
    ```
    This way, we inject the database connection dependency via the last parameter of `lacinia/execute` which is the *application context.*

1. Create separate `src/flexql/graphql/resolvers/<entity>.clj` files, one for each of our entities. We are going to create new resolvers soon, and having all of them on the same namespace would be quite of a clutter.

1. Move the following code from `src/flexql/graphql/resolvers.clj` to `src/flexql/graphql/resolvers/board_game.clj`:
    ```clj
    (defn- game-index [_ _ _]
      (let [connection (-> cfg/config :db db/connection)]
        (db/execute! connection (-> (sqlh/select :*) (sqlh/from :board-game)))))
    ```
    (Remember to `:require` both `honey.sql.helpers` and `flexql.db.core`.)

    We make this function public and change it to retrieve the database connection from the context, like this:

    ```clj
    (defn index [{:keys [dbconn]} _ _]
      (db/execute! dbconn (-> (sqlh/select :*) (sqlh/from :board-game))))
    ```
    Resolvers receive the *application context* as their first parameter, so that's where we get the database connection we injected before. (Function name change is optional, of course.)

1. Modify `src/flexql/graphql/resolvers.clj` to refer the public resolver we just created:

    ```clj
    (def resolvers-map
      {:games/all game/index
       :designers/all designer-index
       :members/all member-index})
    ```
    (Remember to add `[flexql.graphql.resolvers.board-game :as game]` to the namespace's `:require`.)

1. Check that the query is still working.

1. Repeat for the rest of entities.

1. Clean-up `:require`s in `flexql.graphql.resolvers` namespace.

### Step 3: Resolvers with arguments
1. Add the following query definitions to your GraphQL schema:
    ```edn
    :game
    {:type :BoardGame
     :description "Retrieve a board game by its id."
     :args {:id {:type (non-null ID)}}
     :resolve :games/get}
    :designer
    {:type :Designer
     :description "Retrieve a designer by its id."
     :args {:id {:type (non-null ID)}}
     :resolve :designers/get}
    :member
    {:type :Member
     :description "Retrieve a member by its id."
     :args {:id {:type (non-null ID)}}
     :resolve :members/get}
    ```
    We can see there's an `args` key that allows to specify input values for the queries.

1. Create new resolvers (showing `board_game`'s as an example, the rest would be very similar):

    ```clj
    (def ^:private base-select (-> (sqlh/select :*) (sqlh/from :board-game)))

    (defn find-by-id [{:keys [dbconn]} {:keys [id]} _]
      (let [sql (-> base-select (sqlh/where [:= :id (->UUID id)]))]
        (->> sql
             (db/execute! dbconn)
             first)))
    ```
    (Note: we defined `base-select` as the SQL sentence we used for `index`, so it will be reused there also. Remember to require `->UUID` from `flexql.db.utils`.)

    Here we are using the *second* argument of a resolver function to retrieve the resolver's arguments.

1. Try your new queries and see that they work as expected.

### Step 4: Field resolvers
1. Add this field definition to your `BoardGame` entity:

    ```edn
    :avg_rating  {:type Float
                  :description "Average rating for the board game."
                  :resolve :game/rating}
    ```
    We added a `:resolve` key to the definition to refer an *explicit field resolver*. We use explicit field resolvers when we want the field value to be gathered/calculated *only if it is requested*.

    So far, we have been doing a `SELECT * FROM <table>` query to recover data because it doesn't add much cost on performance, but ratings are stored on a different table so we'll need to run an additional query to get the average rating; that's why it is better to do it only if the data has actually been requested.

1. Create the field resolver for `BoardGame`:

    ```clj
    (defn avg-rating-for [{:keys [dbconn]} _ {:keys [id]}]
      (let [sql (-> (sqlh/select [[:avg :rating] :average])
                    (sqlh/from :game-rating)
                    (sqlh/where [:= :game-id (->UUID id)]))]
        (->> sql
            (db/execute! dbconn)
            first
            :average)))
    ```
    Here we are using the *third* parameter of a resolver, the one that gets the current value. That would allow us to create calculated fields based on current info or, like in this case, trigger an additional database query to recover additional info.

1. Reference the new field resolver on the resolvers map.

1. Try your new resolver to verify it works as expected.

### Step 5: Cross-references

1. Add this field definition to your `BoardGame` entity:

    ```clj
    :ratings     {:type (list :Rating)
                  :description "List of ratings given by members."
                  :resolve :game/all-ratings}
    ```

1. Add this object definition to your GraphQL schema:

    ```clj
    :Rating
    {:description "Score given to a board game by a member."
     :fields
     {:game        {:type (non-null :BoardGame)
                    :description "Board game rated."}
      :member      {:type (non-null :Member)
                    :description "Member who rated the game."}
      :rating      {:type (non-null Int)}}}
    ```

1. Create the field resolver for `ratings`:

    ```clj
    (defn ratings [{:keys [dbconn]} _ {:keys [id] :as game}]
      (let [sql (-> (sqlh/select :gr.rating :m.*)
                    (sqlh/from [:game-rating :gr])
                    (sqlh/join [:member :m] [:= :m.id :gr.member_id])
                    (sqlh/where [:= :gr.game_id (->UUID id)]))
            results (db/execute! dbconn sql)
            ->rating (fn [{:keys [rating] :as r}]
                       {:game game
                        :rating rating
                        :member (dissoc r :rating)})]
        (map ->rating results)))
    ```
1. Add the new resolver to the resolver map

1. Try your new resolver and see you can recover both game (unnecessary, actually) and member information from within the ratings field.

### Step 6: Web service
1. Create the file `src/flexql/server.clj` and type this code in:

    ```clj
    (ns flexql.server
      (:require [clojure.data.json :as json]
                [com.walmartlabs.lacinia :as lacinia]
                [flexql.config :as cfg]
                [reitit.ring :as ring]
                [ring.adapter.jetty :as jetty]
                [ring.middleware.content-type :as content-type]
                [ring.middleware.not-modified :as not-modified]
                [ring.middleware.resource :as resource]
                [ring.util.request :as request]))

    (defn- handler [schema db-connection request]
      (let [{:keys [query variables]} (json/read-str (request/body-string request)
                                                     :key-fn keyword)
            result                    (lacinia/execute schema
                                                       query
                                                       variables
                                                       {:dbconn db-connection})]
        {:status 200
         :body (json/write-str result)
         :headers {"Content-Type" "application/json"}}))

    (defn- app [schema db-connection]
      (let [handler-fn (partial handler schema db-connection)]
        (-> (ring/ring-handler (ring/router ["/graphql" {:post handler-fn}]))
            (resource/wrap-resource "static")
            content-type/wrap-content-type
            not-modified/wrap-not-modified)))

    (defn start [schema db-connection]
      (jetty/run-jetty (app schema db-connection)
                       {:join? false :port (-> cfg/config :http :port)}))
    ```
1. Modify your `-main` function to start the web server:

    ```clj
    (ns flexql.core
      (:require [flexql.config :as cfg]
                [flexql.db.core :as db]
                [flexql.graphql.schema :as s]
                [flexql.server :as server]))

    (defn -main []
      (server/start (s/initialize)
                    (-> cfg/config :db db/connection)))
    ```
1. Start the server with `lein run`

1. Open a browser and navigate to `http://localhost:8080/playground.html`. Your API is documented and can be tried interactively!

### Step 7: Mutations
1. Add this mutation definition to your GraphQL schema:

    ```edn
    :mutations
    {:rate_game
     {:type :BoardGame
      :description "Allows a member to rate a board game, or change a previous rating."
      :args {:member_id {:type (non-null ID)}
             :game_id {:type (non-null ID)}
             :rating {:type (non-null Int)}}
      :resolve :game/rate}}
    ```

    Alternatively, you can create an *Input object* and use it for the argument:

    ```edn
    :input-objects
    {:SubmittedRating
     {:description "A board game rating being created or updated."
      :fields
      {:member_id {:type (non-null ID)}
       :game_id   {:type (non-null ID)}
       :rating    {:type (non-null Int)}}}}
    :mutations
    {:rate_game
     {:type :BoardGame
      :description "Allows a member to rate a board game, or change a previous rating."
      :args {:rating {:type :SubmittedRating}}
      :resolve :game/rate}}
    ```
  
1. Create the resolver for the mutation:

    ```clj
    (defn- rating-selector [member-id game-id]
      [:and
       [:= :game-id (->UUID game-id)]
       [:= :member-id (->UUID member-id)]])

    (defn- select-game-rating-sql [member-id game-id]
      (-> (sqlh/select :*)
          (sqlh/from :game-rating)
          (sqlh/where (rating-selector member-id game-id))))

    (defn- rated? [dbconn {:keys [member_id game_id]}]
      (-> (select-game-rating-sql member_id game_id)
          (db/execute! dbconn)
          count
          (= 1)))
  
    (defn- update-rating-sql [{:keys [member_id game_id rating]}]
      (let [timestamp (now)]
        (-> (sqlh/update :game-rating)
            (sqlh/set {:rating rating :updated-at timestamp})
            (sqlh/where (rating-selector member_id game_id)))))
    
    (defn- insert-rating-sql [new-rating]
      (let [timestamp (now)]
        (-> (sqlh/insert-into :game-rating)
            (sqlh/values [(-> new-rating
                              (assoc :created-at timestamp)
                              (assoc :updated-at timestamp)
                              (update :game_id ->UUID)
                              (update :member_id ->UUID))]))))
    
    (defn rate [{:keys [dbconn] :as context} {:keys [game_id] :as input} _]
      (db/execute! dbconn (if (rated? dbconn input)
                            (update-rating-sql input)
                            (insert-rating-sql input)))
      (find-by-id context {:id game_id} nil))
    ```
    (Remember to refer `now` from `flexql.db.utils`.)

1. Add the new resolver to the resolvers map.

1. Try your mutation! See what happens when a member rates a game for the first time. See what happens when the same member submits a second rating for the same game.