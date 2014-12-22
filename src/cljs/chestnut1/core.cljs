(ns chestnut1.core
  (:require [clojure.string :as string]
            [json-html.core :refer [edn->hiccup]]
            [reagent.core :as r]
            [reagent-forms.core :as forms]
            [chestnut1.app-page :as app]
            [chestnut1.session :as session :refer [global-state]]
            [chestnut1.routes :as routes]
            [chestnut1.views.common :as common]
            [chestnut1.user-page :as user]))

(def the-page-wrapper (r/atom {:page-header "My web page"
                               :menu-page :app}))

(defn dump-dom []
  [:div
   [:h2 "Wrapper"]
   (edn->hiccup @the-page-wrapper)])


(defn dumper-page-dom []
  [:div
   [:h1 "State dump"]
   [dump-dom]
   [app/dump-dom]
   (user/dump-dom)])


(defn page []
  (fn []
    [:div.container-fluid
     [:div.row-fluid
      [:div.span9

       [forms/bind-fields
        [:div.row-fluid
         [:h1 {:field :label :id :page-header}]
         [:p [:em "Problem 1. Click on a button twice, and the page id becomes nil."]]
         [:div.btn-group {:field :single-select :id :menu-page}
          [:button.btn.btn-default {:key :app} "App"]
          [:button.btn.btn-default {:key :user} "User"]
          [:button.btn.btn-default {:key :dumper} "Dumper"]]]
        the-page-wrapper]

       [:div.row-fluid
        (case (:menu-page @the-page-wrapper)
          :app    [app/page-dom]
          :user   [user/page-dom]
          :dumper [dumper-page-dom]
          [:div
           [:h3 "Button error?"]
           [:p "Missing :menu-page when clicked on current page: "]
           [:p "State is: " [:code (str @the-page-wrapper)]]])]]]]))


(defn page-render []
  [:div.container
   [common/header]
   [(global-state :current-page)]])

(defn page-component []
  (r/create-class {:component-will-mount routes/app-routes
                         :render page-render}))

(defn main []
  (r/render-component [page-component] (.getElementById js/document "app")))
