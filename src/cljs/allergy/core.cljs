(ns allergy.core
  (:require [clojure.string :as string]
            [json-html.core :refer [edn->hiccup]]
            [reagent.core :as r]
            [reagent-forms.core :as forms]
            [allergy.session :as session :refer [global-state]]
            [allergy.routes :as routes]
            [allergy.views.common :as common]
            [allergy.views.app-page :as app]
            [allergy.views.user-page :as user]))

;;; This template began as my playground for learning what was
;;; available. It contains lots of example code that you probably
;;; don't want. So, the best way to use this for a new project is to
;;; first "lein new allergy-template" your project, and then remove
;;; all the bits you don't want.




(defn page-dom []
  [:div.container
   [common/header]
   [(global-state :current-page)]])

(defn page-component []
  (r/create-class {:component-will-mount routes/app-routes
                   :render page-dom}))


;;; Main entry point. Render the current page.
(defn main []
  (r/render-component [page-component] (.getElementById js/document "app")))
