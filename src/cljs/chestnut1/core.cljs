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
                      :clicker 0
                      :page-header "My web page"
                      :menu-page :app}))


(defn counting-component [doc]
  [:div
   "The atom " [:code "clicker"] " has value: "
   (:clicker @doc) ". "
   [:input {:type "button" :value "Click me!"
            :on-click #(swap! doc update-in [:clicker] inc)}]])

(defn header-dom [doc]
  [:div
   [:h1 (:page-header @doc)]
   [:div.btn-group {:field :single-select :id :menu-page}
    [:button.btn.btn-default {:key :app} "App"]
    [:button.btn.btn-default {:key :user} "User"]
    [:button.btn.btn-default {:key :guts} "Guts"]]])

(defn app-page-dom [doc]
  [:div
   [:h1 "APP "]
   [counting-component the-doc]
   (errchecked-input "last name" :text :user.last-name
                     empty?  "Last name is empty!")])

(defn user-page-dom [doc]
  [:div
   [:h1 "USER"]
   [counting-component the-doc]
   (errchecked-input "last name" :text :user.last-name
                     empty?  "Last name is empty!")])

(defn one-page-dom [doc]
  [:div
   [:div {:class (when (and true (not= :app (:menu-page @doc))) "hidden")}
    (app-page-dom doc)]
   [:div {:class (when (and true (not= :user (:menu-page @doc))) "hidden")}
    ;; Playing around... embedding one page as a vector, one as a form, and one
    ;; inline. All seem to behave the same.
    [user-page-dom doc]]
   [:div {:class (when (and true (not= :guts (:menu-page @doc))) "hidden")}
    [:h1 "GUTS"]
    [counting-component the-doc]
    (errchecked-input "last name" :text :user.last-name
                      empty?  "Last name is empty!")]])


(defn page []
  (fn []
    [forms/bind-fields
     [:div
      (header-dom the-doc)
      ;; Two input boxes, sharing state. This works
      (errchecked-input "last name" :text :user.last-name
                        empty?  "Last name is empty!")
      
      (errchecked-input "last name" :text :user.last-name
                        empty?  "Last name is empty!")

;;;  MAIN QUESTION IS OVER HERE
      ;; This line, with one-page-dom in a vector, changes on menu clicks,
      ;; but does not share the user.last-name state
      [one-page-dom the-doc]
      ;; This line, with it in a form, shares the state, but does not change
      ;; when the menu is clicked.
      ;; I am confused.
      (one-page-dom the-doc)

      ]
     the-doc
     (fn [[id] value document]
       (cond
        (= id :menu-page)
        (assoc document :menu-page value)))]))


(defn main []
  (r/render-component [page] (.getElementById js/document "app")))
