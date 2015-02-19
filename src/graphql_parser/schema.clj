(ns graphql-parser.schema)

(def Country
  {:name [:country/name String]})

(def Artist
  {:gid [:artist/gid String]
   :name [:artist/name String]
   :sortName [:artist/sortName String]
   :type [:artist/type String]
   :gender [:artist/gender String]
   :country [:artist/country Country]
   :startYear [:artist/startYear Long]
   :startMonth [:artist/startMonth Long]
   :startDay [:artist/startDay Long]
   :endYear [:artist/endYear Long]
   :endMonth [:artist/endMonth Long]
   :endDay [:artist/endDay Long]})

(def AbstractRelease
  {:gid [:abstractRelease/gid String]
   :name [:abstractRelease/name String]
   :type [:abstractRelease/type String]
   :artists [:abstractRelease/artists Artist]
   :artistCredit [:abstractRelease/artistCredit String]})

(def Track
  {:artists [:track/artists Artist]
   :position [:track/position Long]
   :name [:track/name String]
   :duration [:track/duration Long]})

(def Media
  {:tracks [:medium/tracks Track]
   :format [:medium/format String]
   :position [:medium/position Long]
   :name [:medium/name String]
   :trackCount [:medium/trackCount Long]})

(def Release
  {:gid [:release/gid String]
   :country [:release/country Country]
   :barcode [:release/barcode String]
   :name [:release/name String]
   :media [:release/media Media]
   :packaging [:release/packaging String]
   :year [:release/year Long]
   :month [:release/month Long]
   :day [:release/day Long]
   :artistCredit [:release/artistCredit String]
   :artists [:release/artists Artist]
   :abstractRelease [:release/abstractRelease AbstractRelease]
   :status [:release/status String]})