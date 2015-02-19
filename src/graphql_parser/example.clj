(ns graphql-parser.example
  (:require [datomic.api :as d]
            [graphql-parser.core :as core]
            [graphql-parser.schema]))

(def uri "datomic:sql://mbrainz?jdbc:postgresql://localhost:5432/datomic?user=datomic")

(defn resolve-schema-class [class]
  (var-get (ns-resolve 'graphql-parser.schema (symbol class))))

(defn ql->pull [root attributes]
  (let [metadata (resolve-schema-class root)
        schema (select-keys metadata (map keyword attributes))
        attrs (map #(-> % val first) schema)]
    attrs))


(def graphql "
       Artist (17592186050305) {
           country {
              name
           },
           name,
           sortName,
           gender,
           gid,
           type
       }
   ")

(defn insert-relations [pull-clause metadata]
  (if (:relations metadata)
    (let [relation (first (:relations metadata))
          base-attr (keyword (first relation))
          attr-meta (get (resolve-schema-class (:base-class metadata)) base-attr)
          relation-attr (keyword (second relation))
          relation-name (first attr-meta)
          relation-class (second attr-meta)
          relation-addition (first (get relation-class relation-attr))
          old-pull (into [] pull-clause)
          index (.indexOf old-pull relation-name)
          new-pull (assoc old-pull index {relation-name [(keyword relation-addition)]})]
      (println "PULL" new-pull)
      new-pull)
    pull-clause))

(defn create-pull [graphql]
  (let [metadata (core/graphql->datalog graphql)
        pull-clause (ql->pull (:base-class metadata) (:attributes metadata))
        pull-with-rel (insert-relations pull-clause metadata)]
    (println metadata)
    [(:id metadata) pull-with-rel]))

(defn graphql-query [graphql]
  (let [pull-clause (create-pull graphql)
        db (-> uri d/connect d/db)]
    (println pull-clause)
    (d/pull db (second pull-clause) (read-string (first pull-clause)))))