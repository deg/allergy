(ns allergy.views.pages
  (:require allergy.views.home-page
            allergy.views.about-page
            allergy.views.new-page
            allergy.views.user-page
            allergy.views.app-page
            allergy.views.dumper-page))

(def pages {:home-page   allergy.views.home-page/page-dom
            :about-page  allergy.views.about-page/page-dom
            :new-page    allergy.views.new-page/page-dom
            :user-page   allergy.views.user-page/page-dom
            :app-page    allergy.views.app-page/page-dom
            :dumper-page allergy.views.dumper-page/page-dom})
