(ns chestnut1.core
  (:require [clojure.string :as string]
            [json-html.core :refer [edn->hiccup]]
            [reagent.core :as r]
            [reagent-forms.core :as forms]))


(defn row [label input]
  [:div.row
   [:div.col-xs-2 [:label label]]
   [:div.col-xs-10 input]])

(defn radio [label id name value]
  [:div.radio
   [:label
    [:input {:field :radio :id id :name name :value value}]
    label]])

(defn input [label type id]
  (row label [:input.form-control {:field type :id id}]))

(defn bad-email? [s]
  ;; Derived from http://www.dotnet-tricks.com/Tutorial/javascript/UNDS040712-JavaScript-Email-Address-validation-using-Regular-Expression.html
  (not (.exec (js/RegExp. "^\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$") s)))

(defn errmsg-bar [id event message]
  [:div.row
   [:div.col-xs-2]
   [:div.col-xs-10
    [:div.alert.alert-danger
     {:field :alert :id id :event event}
     message]]])

(defn errchecked-input [label type id & err-handlers]
  [:div
   (input label type id)
   (doall (map #(apply errmsg-bar id %) (partition 2 err-handlers)))])


(defn header-dom [doc]
  [:h1 "My web page"])

(defn menu-dom [doc]
   [:div.btn-group {:field :single-select :id :menu-page}
    [:button.btn.btn-default {:key :app} "App"]
    [:button.btn.btn-default {:key :user} "User"]
    [:button.btn.btn-default {:key :guts} "Guts"]])


(defn app-dom [doc]
  [:div
   [:h1 "I am the app; I am the walrus"][:hr]
   [:hr]
   (input "kg" :numeric :weight-kg)
   (input "lb" :numeric :weight-lb)
   [:hr]
   [:h3 "BMI Calculator"]
   (input "height" :numeric :height)
   (input "weight" :numeric :weight)
   (row "BMI"
        [:input.form-control
         {:field :numeric :fmt "%.2f" :id :bmi :disabled true}])
   ])


(defn user-dom [doc]
  [:div
   (errchecked-input "first name" :text :person.first-name
                     empty? "First name is empty"
                     #(< (js/parseInt %) 18) "You must be over 18"
                     #(= % "John") "No johns allowed here")

   (errchecked-input "last name" :text :person.last-name
                     empty?  "Last name is empty!")

   (errchecked-input "age" :numeric :person.age
                     #(empty? (str %)) "Please supply your age"
                     #(< % 18) "You must be over 18"
                     #(>= % 100) "Sorry, too old to play")

   (errchecked-input "email" :email :person.email
                     bad-email? "Invalid email address")])

(defn guts-dom [doc]
  [:div
   [:hr]
   [:h1 "Document State"]
   [edn->hiccup @doc]])

(defn page-dom [doc menu-key]
  [:div {:key (str "page-" (menu-key @doc))}
   (case (menu-key @doc)
     :app (app-dom doc)
     :user (user-dom doc)
     :guts (guts-dom doc))])

(defn page []
  (let [doc (r/atom {:person {:first-name "John"
                              :age 35
                              :email "foo@bar.baz"}
                     :weight 100
                     :height 200
                     :bmi 0.5
                     :menu-page :guts})]
    (fn []
      (js/console.log "DIV: "
                      (str [:div
                            (header-dom doc)
                            (menu-dom doc)
                            [page-dom doc :menu-page]]))
      [:div.container-fluid
       [:div.row-fluid
        [:div.span6
         [forms/bind-fields
          [:div
           (header-dom doc)
           (menu-dom doc)
           [page-dom doc :menu-page]]
          doc
          (fn [[id] value document]
            (cond
             (= id :weight-lb)
             (assoc document :weight-kg (/ value 2.2046))
             (= id :weight-kg)
             (assoc document :weight-lb (* value 2.2046))
             (= id :menu-page)
             (assoc document :menu-page value)
             :else nil))
          (fn [[id] value {:keys [height weight] :as document}]
            (when (and (some #{id} [:height :weight]) weight height)
              (assoc document :bmi (/ weight (* height height)))))]]]])))

(defn main []
  (r/render-component [page] (.getElementById js/document "app")))
