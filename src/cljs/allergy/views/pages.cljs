(ns allergy.views.pages
  (:require [allergy.views.home-page :refer [home-page]]
            [allergy.views.about-page :refer [about-page]]
            [allergy.views.new-page :refer [new-page]]
            [allergy.user-page :refer [page-dom]]))

(def pages {:home-page home-page
            :about-page about-page
            :new-page new-page
            :user-page page-dom})
