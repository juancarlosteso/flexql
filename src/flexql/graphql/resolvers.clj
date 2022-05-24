(ns flexql.graphql.resolvers
  (:require [flexql.graphql.resolvers.board-game :as game]
            [flexql.graphql.resolvers.designer :as designer]
            [flexql.graphql.resolvers.member :as member]))

(def resolvers-map
  {:games/all game/index
   :designers/all designer/index
   :members/all member/index
   :games/get game/find-by-id
   :designers/get designer/find-by-id
   :members/get member/find-by-id
   :game/rating game/avg-rating-for
   :game/all-ratings game/ratings
   :game/rate game/rate
   :game/designers game/designers
   :designer/games designer/games
   :member/all-ratings member/ratings
   :games/new game/create
   :designers/new designer/create
   :members/new member/create
   :game/new-designer game/add-designer})