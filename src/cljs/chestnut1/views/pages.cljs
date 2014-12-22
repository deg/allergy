(ns chestnut1.views.pages
  (:require [chestnut1.views.home-page :refer [home-page]]
            [chestnut1.views.about-page :refer [about-page]]
            [chestnut1.views.new-page :refer [new-page]]
            [chestnut1.user-page :refer [page-dom]]))

(def pages {:home-page home-page
            :about-page about-page
            :new-page new-page
            :user-page page-dom})
