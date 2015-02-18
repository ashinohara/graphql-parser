(ns graphql-parser.core
  (:require [instaparse.core :as insta]))

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
     field = field_name <WS> optional_object
     optional_object = object?"))