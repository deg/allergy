(ns chestnut1.core
  (:require [clojure.string :as string]
            [json-html.core :refer [edn->hiccup]]
            [reagent.core :as r]
            [reagent-forms.core :as forms]))


(defn row [label input]
  [:div.row
   [:div.col-xs-2 [:label label]]
   [:div.col-xs-10 input]])

(defn input [label type id]
  (row label [:input.form-control {:field type :id id}]))

(defn errmsg-bar [id event message]
  [:div.row
   [:div.alert.alert-danger
    {:field :alert :id id :event event}
    message]])

(defn errchecked-input [label type id & err-handlers]
  [:div
   (input label type id)
   (doall (map #(apply errmsg-bar id %) (partition 2 err-handlers)))])


(def the-doc (r/atom {:user {:first-name "John"
                             :last-name "Smith"
                             :email "JSmith@zmail.com"
                             :user-id ""
                             }
                      :clicker1 0
                      :clicker2 0
                      :page-header "My web page"
                      :menu-page :app}))


(defn counting-component [doc]
  [:div
   [:p
    [:code "clicker1"] " has value: " (:clicker1 @doc) ". "
    [:input {:type "button" :value "Click me!"
             :on-click #(swap! doc update-in [:clicker1] inc)}]]
   [:p
    [:code "clicker2"] " has value: " (:clicker2 @doc) ". "
    [:input {:type "button" :value "Click me!"
             :on-click #(swap! doc update-in [:clicker2] inc)}]]
   [:p "Advanced programming techniques reveal that their product is: "
    (* (:clicker1 @doc) (:clicker2 @doc))]])

(defn header-dom [doc]
  [forms/bind-fields
   [:div
    [:h1 {:field :label :id :page-header} (:page-header @doc)]
    [:code "clicker1"] " has value: " (:clicker1 @doc) ". "
    [:p (:page-header @doc)]

    [:div.btn-group {:field :single-select :id :menu-page}
     [:button.btn.btn-default {:key :app} "App"]
     [:button.btn.btn-default {:key :user} "User"]
     [:button.btn.btn-default {:key :guts} "Guts"]]]
   doc])

(defn app-page-dom [doc]
  [forms/bind-fields
   [:div
    [:h1 "The application "]
    [counting-component the-doc]
    (errchecked-input "last name" :text :user.last-name
                      empty?  "Last name is empty!")]
   doc])

(defn user-page-dom [doc]
  [forms/bind-fields
   [:div
    [:h1 "User info"]
    (errchecked-input "last name" :text :user.last-name
                      empty?  "Last name is empty!")]
   doc])

(defn guts-page-dom [doc]
  [forms/bind-fields
   [:div
    [:h1 "State dump"]
    [edn->hiccup @doc]]
   doc])

(defn one-page-dom [doc]
  (case (:menu-page @doc)
    :app
    [app-page-dom doc]

    :user
    [user-page-dom doc]

    :guts
    [guts-page-dom doc]

    [:div
     [:h3 "Button error?"]
     [:p "Missing :menu-page when clicked on current page: "]
     [:p "State is: " [:code (str @doc)]]]))


(defn page []
  [:div
   [header-dom the-doc]
   [one-page-dom the-doc]])


(defn main []
  (r/render-component [page] (.getElementById js/document "app")))
