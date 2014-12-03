(ns chestnut1.core
  (:require [clojure.string :as string]
            [json-html.core :refer [edn->hiccup]]
            [reagent.core :as r]
            [reagent-forms.core :refer [bind-fields init-field value-of]]))


;; (def state (r/atom {:doc {} :saved? false}))

;; (defn set-value! [id value]
;;   (swap! state assoc :saved? false)
;;   (swap! state assoc-in [:doc id] value))

;; (defn get-value [id]
;;   (get-in @state [:doc id]))

;; (defn list-item [id k v selections]
;;   (letfn [(handle-click! []
;;             (swap! selections update-in [k] not)
;;             (set-value! id (->> @selections
;;                                 (filter second)
;;                                 (map first))))]
;;     [:li {:class (str "list-group-item"
;;                       (if (k @selections) " active"))
;;           :on-click handle-click!}
;;      v]))

;; (defn selection-list [id label & items]
;;   (let [selections (->> items (map (fn [[k]] [k false])) (into {}) r/atom)]    
;;     (fn []
;;       [:div.row
;;        [:div.col-md-2 [:span label]]
;;        [:div.col-md-5
;;         [:div.row
;;          (for [[k v] items]
;;            [list-item id k v selections])]]])))

;; (defn row [label & body]
;;   [:div.row
;;    [:div.col-md-2 [:span label]]
;;    [:div.col-md-3 body]])

;; (defn text-input [id label]
;;   [row label
;;    [:input
;;      {:type "text"
;;        :class "form-control"
;;        :value (get-value id)
;;       :on-change #(set-value! id (-> % .-target .-value))}]])

;; (defn source [text]
;;   (filter #(some #{text} %) ["Alice" "Alan" "Bob" "Beth"]))


;; (defn home []
;;   [:div
;;    [:div.page-header [:h1 "Reagent Form"]]

;;    [text-input :first-name "First name:"]
;;    [text-input :last-name "Last name:"]

;;    [selection-list :favorite-drinks "Favorite drinks:"
;;     [:coffee "Coffee"]
;;     [:beer "Beer"]
;;     [:crab-juice "Crab juice"]]
;;    (form)
;;    [:button {:type "submit"
;;              :class "btn btn-default"
;;              :onClick #(.log js/console (clj->js @state))}
;;     "Submit"]])

;; (def page-form
;;   [:div
;;    (input "first name" :text :person.first-name)
;;    [:div.row
;;     [:div.col-md-2]
;;     [:div.col-md-5
;;      [:div.alert.alert-danger
;;       {:field :alert :id :errors.first-name}]]]
;;    ])

;; (defn main []
;;   (r/render-component [home]
;;                       (.getElementById js/document "app")))

(defn row [label input]
  [:div.row
   [:div.col-md-2 [:label label]]
   [:div.col-md-5 input]])

(defn radio [label id name value]
  [:div.radio
   [:label
    [:input {:field :radio :id id :name name :value value}]
    label]])

(defn input [label type id]
  (row label [:input.form-control {:field type :id id}]))

(def form-template
  [:div
   (input "first name" :text :person.first-name)
   [:div.row
    [:div.col-md-2]
    [:div.col-md-5
     [:div.alert.alert-danger
      {:field :alert :id :errors.first-name}]]]

   (input "last name" :text :person.last-name)
   [:div.row
    [:div.col-md-2]
    [:div.col-md-5
     [:div.alert.alert-success
      {:field :alert :id :person.last-name :event empty?}
      "last name is empty!"]]]

   (input "age" :numeric :person.age)
   (input "email" :email :person.email)
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

       [bind-fields
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
