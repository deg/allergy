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

(def form-template
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
                     bad-email? "Invalid email address")
   (row
    "comments"
    [:textarea.form-control
     {:field :textarea :id :comments}])

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
   [:hr]

   (row "isn't data binding lovely?"
        [:input {:field :checkbox :id :databinding.lovely}])
   [:label
    {:field :label
     :preamble "The level of awesome: "
     :placeholder "N/A"
     :id :awesomeness}]

   [:input {:field :range :min 1 :max 10 :id :awesomeness}]

   [:h3 "option list"]
   [:div.form-group
    [:label "pick an option"]
    [:select.form-control {:field :list :id :many.options}
     [:option {:key :foo} "foo"]
     [:option {:key :bar} "bar"]
     [:option {:key :baz} "baz"]]]

   (radio
    "Option one is this and that—be sure to include why it's great"
    :radioselection :foo :a)
   (radio
    "Option two can be something else and selecting it will deselect option one"
    :radioselection :foo :b)

   [:h3 "multi-select buttons"]
   [:div.btn-group {:field :multi-select :id :every.position}
    [:button.btn.btn-default {:key :left} "Left"]
    [:button.btn.btn-default {:key :middle} "Middle"]
    [:button.btn.btn-default {:key :right} "Right"]]

   [:h3 "single-select buttons"]
   [:div.btn-group {:field :single-select :id :unique.position}
    [:button.btn.btn-default {:key :left} "Left"]
    [:button.btn.btn-default {:key :middle} "Middle"]
    [:button.btn.btn-default {:key :right} "Right"]]

   [:h3 "single-select list"]
   [:div.list-group {:field :single-select :id :pick-one}
    [:div.list-group-item {:key :foo} "foo"]
    [:div.list-group-item {:key :bar} "bar"]
    [:div.list-group-item {:key :baz} "baz"]]

   [:h3 "multi-select list"]
   [:ul.list-group {:field :multi-select :id :pick-a-few}
    [:li.list-group-item {:key :foo} "foo"]
    [:li.list-group-item {:key :bar} "bar"]
    [:li.list-group-item {:key :baz} "baz"]]])

(defn page []
  (let [doc (r/atom {:person {:first-name "John"
                              :age 35
                              :email "foo@bar.baz"}
                     :weight 100
                     :height 200
                     :bmi 0.5
                     :comments "some interesting comments\non this subject"
                     :radioselection :b
                     :position [:left :right]
                     :pick-one :bar
                     :unique {:position :middle}
                     :pick-a-few [:bar :baz]
                     :many {:options :bar}})]
    (fn []
      [:div
       [:div.page-header [:h1 "Sample Form"]]

       [forms/bind-fields
        form-template
        doc
        (fn [[id] value {:keys [weight-lb weight-kg] :as document}]
           (cond
            (= id :weight-lb)
            (assoc document :weight-kg (/ value 2.2046))
            (= id :weight-kg)
            (assoc document :weight-lb (* value 2.2046))
            :else nil))
        (fn [[id] value {:keys [height weight] :as document}]
          (when (and (some #{id} [:height :weight]) weight height)
            (assoc document :bmi (/ weight (* height height)))))]

       [:button.btn.btn-default
         {:on-click
          #(if (empty? (get-in @doc [:person :first-name]))
             (swap! doc assoc-in [:errors :first-name]"first name is empty"))}
         "save"]

       [:hr]
       [:h1 "Document State"]
       [edn->hiccup @doc]])))

(defn main []
  (r/render-component [page] (.getElementById js/document "app")))
