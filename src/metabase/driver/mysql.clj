(ns metabase.driver.mysql
  (:require [korma.db :as kdb]
            (metabase.driver [generic-sql :as generic-sql, :refer [GenericSQLIDriverMixin GenericSQLISyncDriverTableFKsMixin
                                                                   GenericSQLISyncDriverFieldAvgLengthMixin GenericSQLISyncDriverFieldPercentUrlsMixin]]
                             [interface :refer [IDriver ISyncDriverTableFKs ISyncDriverFieldAvgLength ISyncDriverFieldPercentUrls
                                                ISyncDriverSpecificSyncField driver-specific-sync-field!]])
            (metabase.driver.generic-sql [interface :refer :all])))

(def ^:private ^:const column->base-type
  {:bigint     :BigIntegerField
   :binary     :UnknownField
   :bit        :UnknownField
   :blob       :UnknownField
   :char       :CharField
   :date       :DateField
   :datetime   :DateTimeField
   :decimal    :DecimalField
   :double     :FloatField
   :enum       :UnknownField
   :float      :FloatField
   :int        :IntegerField
   :integer    :IntegerField
   :longblob   :UnknownField
   :longtext   :TextField
   :mediumblob :UnknownField
   :mediumint  :IntegerField
   :mediumtext :TextField
   :numeric    :DecimalField
   :real       :FloatField
   :set        :UnknownField
   :text       :TextField
   :time       :TimeField
   :timestamp  :DateTimeField
   :tinyblob   :UnknownField
   :tinyint    :IntegerField
   :tinytext   :TextField
   :varbinary  :UnknownField
   :varchar    :CharField
   :year       :IntegerField})

(defrecord MySQLDriver []
  ISqlDriverDatabaseSpecific
  (connection-details->connection-spec [_ details]
    (kdb/mysql details))

  (database->connection-details [_ {:keys [details]}]
    details)

  (cast-timestamp-to-date [_ table-name field-name seconds-or-milliseconds]
    ;; TODO
    )

  (timezone->set-timezone-sql [_ timezone]
    ;; see http://stackoverflow.com/questions/930900/how-to-set-time-zone-of-mysql
    (format "SET @@session.time_zone = '%s';" timezone)))

(extend MySQLDriver
  IDriver                     GenericSQLIDriverMixin
  ISyncDriverTableFKs         GenericSQLISyncDriverTableFKsMixin
  ISyncDriverFieldAvgLength   GenericSQLISyncDriverFieldAvgLengthMixin
  ISyncDriverFieldPercentUrls GenericSQLISyncDriverFieldPercentUrlsMixin)

(def ^:const driver
  (map->MySQLDriver {:column->base-type    column->base-type
                     :features             (conj generic-sql/features :set-timezone)
                     :sql-string-length-fn :CHAR_LENGTH}))
