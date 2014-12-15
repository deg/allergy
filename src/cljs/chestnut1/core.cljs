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
   [:div.col-xs-2]
   [:div.col-xs-10
    [:div.alert.alert-danger
     {:field :alert :id id :event event}
     message]]])

(defn bad-email? [s]
  ;; Derived from
  ;; http://www.dotnet-tricks.com/Tutorial/javascript/UNDS040712-JavaScript-Email-Address-validation-using-Regular-Expression.html
  (not (.exec (js/RegExp. "^\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$") s)))

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

(defn clicker-component [doc id]
   [:p
    [:code (str id)] " has value: " (id @doc) ". "
    [:input {:type "button" :value "Click me!"
             :on-click #(swap! doc update-in [id] inc)}]])

(defn counting-component [doc]
  [:div
   [clicker-component doc :clicker1]
   [clicker-component doc :clicker2]
   [:p "Advanced programming techniques reveal that their product is: "
    (* (:clicker1 @doc) (:clicker2 @doc))]])

(defn header-dom [doc]
  [forms/bind-fields
   [:div.row-fluid
    [:h1 {:field :label :id :page-header} (:page-header @doc)]
    [:div  "Location 1: clicker1 here is " (:clicker1 @doc) " of doc " (str @doc) ". "]
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
    [:div  "Location 3: clicker1 here is " (:clicker1 @doc) " of doc " (str @doc) ". "]
    [counting-component doc]
    (errchecked-input "last name" :text :user.last-name
                      empty?  "Last name is empty!")]
   doc])

(defn user-page-dom [doc]
  [forms/bind-fields
   [:div
    [:h1 "User info"]
    (errchecked-input "first name" :text :user.first-name
                     empty? "First name is empty"
                     #(< (js/parseInt %) 18) "You must be over 18"
                     #(= % "John") "No johns allowed here")
   (errchecked-input "last name" :text :user.last-name
                     empty?  "Last name is empty!")
   (errchecked-input "age" :numeric :user.age
                     #(empty? (str %)) "Please supply your age"
                     #(< % 18) "You must be over 18"
                     #(>= % 100) "Sorry, too old to play")
   (errchecked-input "email" :email :user.email
                     bad-email? "Invalid email address")]
   doc])

(defn guts-page-dom [doc]
  [forms/bind-fields
   [:div
    [:h1 "State dump"]
    [edn->hiccup @doc]]
   doc])

(defn one-page-dom [doc]
  [:div.row-fluid
    [:div  "Location 2: clicker1 here is " (:clicker1 @doc) " of doc " (str @doc) ". "]
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
      [:p "State is: " [:code (str @doc)]]])])


(defn page []
  (fn []
    [forms/bind-fields
     [:div.container-fluid
      [:div.row-fluid
       [:div.span9
        [header-dom the-doc]
        [one-page-dom the-doc]]]]
     the-doc]))


(defn main []
  (r/render-component [page] (.getElementById js/document "app")))
