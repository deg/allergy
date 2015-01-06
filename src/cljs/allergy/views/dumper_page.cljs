(ns allergy.views.dumper-page
  (:require [reagent.core :as r]
            [reagent-forms.core :as forms]
            [json-html.core :refer [edn->hiccup]]
            allergy.session
            allergy.views.user-page
            allergy.views.app-page))


(defn dump-dom []
  [:div
   [:h2 "Wrapper"]
   (edn->hiccup @allergy.session/app-state)])

(defn page-dom []
  [:div
   [:h1 "State dump"]
   [dump-dom]
   [allergy.views.app-page/dump-dom]
   (allergy.views.user-page/dump-dom)])

