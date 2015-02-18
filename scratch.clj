(use '[instaparse.core :as insta])
(use '[clojure.core.match :as m])
(use 'clojure.zip)

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

(def graphql "
       User (234234) {
           one {
               sub1,
               sub2
           },
           two,
           three,
           four
       }
   ")

(def tree (parse-graphql graphql))

(def zp (zipper vector? seq (fn [_ c] c) tree))

(defn is-field [node]
  (and (vector? node) (keyword? (first node)) (= (first node) :field)))

(defn create-datalog [loc]
  (let [curr-node (node loc)
        field-parent (-> loc up up up node)
        is-root (= field-parent (root loc))
        field-name (-> curr-node second second)
        base-class (-> field-parent second second)]
    (println curr-node "PARENT" field-parent)
    (if is-root
      field-name
      [base-class field-name])))

(defn walk-zip [z]
  (loop [loc z
         results []]
    (println "RESULTS" results)
    (if (end? loc)
      results
      (if (is-field (node loc))
        (recur (next loc) (conj results (create-datalog loc)))
        (recur (next loc) results)))))

(walk-zip zp)