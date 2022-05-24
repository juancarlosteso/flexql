(ns flexql.graphql.resolvers
  (:require [flexql.graphql.resolvers.board-game :as game]
            [flexql.graphql.resolvers.designer :as designer]
            [flexql.graphql.resolvers.member :as member]))

(def resolvers-map
  {:games/all game/index
   :designers/all designer/index
   :members/all member/index})