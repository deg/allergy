(ns chestnut1.core
  (:require [clojure.string :as string]
            [reagent.core :as r]))

;; (defn main [])


(def state (r/atom {:doc {} :saved? false}))

(defn set-value! [id value]
  (swap! state assoc :saved? false)
  (swap! state assoc-in [:doc id] value))

(defn get-value [id]
  (get-in @state [:doc id]))

(defn list-item [id k v selections]
  (letfn [(handle-click! []
            (swap! selections update-in [k] not)
            (set-value! id (->> @selections
                                (filter second)
                                (map first))))]
    [:li {:class (str "list-group-item"
                      (if (k @selections) " active"))
          :on-click handle-click!}
     v]))

(defn selection-list [id label & items]
  (let [selections (->> items (map (fn [[k]] [k false])) (into {}) r/atom)]    
    (fn []
      [:div.row
       [:div.col-md-2 [:span label]]
       [:div.col-md-5
        [:div.row
         (for [[k v] items]
           [list-item id k v selections])]]])))

(defn row [label & body]
  [:div.row
   [:div.col-md-2 [:span label]]
   [:div.col-md-3 body]])

(defn text-input [id label]
  [row label
   [:input
     {:type "text"
       :class "form-control"
       :value (get-value id)
       :on-change #(set-value! id (-> % .-target .-value))}]])

(defn home []
  [:div
    [:div.page-header [:h1 "Reagent Form"]]

    [text-input :first-name "First name"]
    [text-input :last-name "First name"]

    [selection-list :favorite-drinks "Favorite drinks"
     [:coffee "Coffee"]
     [:beer "Beer"]
     [:crab-juice "Crab juice"]]

    [:button {:type "submit"
              :class "btn btn-default"
              :onClick #(.log js/console (clj->js @state))}
     "Submit"]])

(defn main []
  (r/render-component [home]
                      (.getElementById js/document "app")))
