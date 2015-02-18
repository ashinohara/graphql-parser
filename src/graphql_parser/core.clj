(ns graphql-parser.core
  (:require [instaparse.core :as insta]
            [clojure.zip :as z]))

(def parse-graphql
  (insta/parser
    "root = <WS> object_name <WS> <OPEN_ROUND> <WS> object_id <WS> <CLOSE_ROUND> <WS> <WS> object <WS>
     object = <OPEN_CURLY> <WS> field_list <WS> <CLOSE_CURLY>
     WS =           #'\\s*'
     OPEN_CURLY =   '{'
     CLOSE_CURLY =  '}'
     OPEN_ROUND =   '('
     CLOSE_ROUND =   ')'
     COMMA = ','
     object_name =  #'\\w*'
     object_id =    #'\\w*'
     name =         #'\\w*'
     field_name =   #'\\w*'
     field_list = field <WS> (<COMMA> <WS> field)*
     field = field_name <WS> object?"))

(defn is-field [node]
  (and (vector? node) (keyword? (first node)) (= (first node) :field)))

(defn create-datalog [loc]
  (let [curr-node (z/node loc)
        field-parent (-> loc z/up z/up z/up z/node)
        is-root (= field-parent (z/root loc))
        field-name (-> curr-node second second)
        base-class (-> field-parent second second)]
    (println "PARENT" curr-node "1" field-name "2" base-class "3" field-parent)
    (if is-root
      field-name
      [base-class field-name])))

(defn walk-zip [zipped]
  (loop [loc zipped
         results []]
    (println "RESULTS" results)
    (if (z/end? loc)
      results
      (if (is-field (z/node loc))
        (recur (z/next loc) (conj results (create-datalog loc)))
        (recur (z/next loc) results)))))

(defn object-name [zipped]
  (second (z/node (-> zipped z/down z/right))))

(defn object-id [zipped]
  (second (z/node (-> zipped z/down z/right z/right))))

(defn get-attributes [zipped]
  (println (walk-zip zipped))
  (get (group-by vector? (walk-zip zipped)) false))

(defn get-objects [zipped]
  (println (walk-zip zipped))
  (get (group-by vector? (walk-zip zipped)) true))

(defn get-metadata [zipped]
  {:id (object-id zipped)
   :base-class (object-name zipped)
   :attributes (get-attributes zipped)
   :relations (get-objects zipped)})

(defn graphql->datalog [graphql]
  (let [tree (parse-graphql graphql)
        zipped (z/zipper vector? seq (fn [_ c] c) tree)]
    (get-metadata zipped)))