import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.team_on.connection.Retrofit

class DatabaseWalk private constructor(context: Context) : SQLiteOpenHelper(context.applicationContext, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "walk_database.db"
        private const val TABLE_NAME = "walk_info"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_TIME = "time"
        private const val COLUMN_DIS = "distance"
        private const val COLUMN_SPEED = "speed"
        private const val COLUMN_IMG = "img"

        @Volatile
        private var instance: DatabaseWalk?= null

        fun getInstance(context: Context)=
            instance ?: synchronized(DatabaseWalk::class.java){
                instance ?: DatabaseWalk(context).also{
                    instance =it
                }
            }
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME ($COLUMN_DATE TEXT PRIMARY KEY, $COLUMN_TIME TEXT, $COLUMN_DIS TEXT, $COLUMN_SPEED TEXT, $COLUMN_IMG BLOB)"
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // 데이터베이스 업그레이드 처리
    }

    fun insertData(date: String, time: String, distance: String, speed: String, img: ByteArray) {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply{
            put(COLUMN_DATE, date)
            put(COLUMN_TIME, time)
            put(COLUMN_DIS, distance)
            put(COLUMN_SPEED, speed)
            put(COLUMN_IMG, img)
        }
        db.insert(TABLE_NAME, null, contentValues)
        db.close()
    }

    @SuppressLint("Recycle")
    fun updateData(date: String, time: String, distance: String, speed: String, img: ByteArray){
        val db = this.writableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_DATE = ?", arrayOf(date))
        var newTime = 0
        var newDistance = 0
        var newSpeed = 0
        if (cursor.moveToFirst()) {
            newTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME)).toInt() + time.toInt()
            newDistance = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DIS)).toInt() + distance.toInt()
            newSpeed = newDistance/newTime
        }

        val contentValues = ContentValues().apply{
            put(COLUMN_DATE, date)
            put(COLUMN_TIME, newTime)
            put(COLUMN_DIS, newDistance)
            put(COLUMN_SPEED, newSpeed)
            put(COLUMN_IMG, img)
        }

        db.update(TABLE_NAME, contentValues, "$COLUMN_DATE = ?", arrayOf(date))

        cursor.close()
        db.close()
    }

    fun getOneData(date: String): Retrofit.WalkData? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_DATE = ?", arrayOf(date))

        var walkData: Retrofit.WalkData? = null
        if (cursor.moveToFirst()) {
            val time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME))
            val distance = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DIS))
            val speed = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SPEED))
            val img = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_IMG))

            walkData = Retrofit.WalkData(time, distance, speed, img)
        }

        cursor.close()
        db.close()
        return walkData
    }

    @SuppressLint("Recycle")
    fun getData(date: String): Array<Retrofit.WalkData>{
        val startDate = date.split(" ")[0]
        val endDate = date.split(" ")[1]

        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_DATE BETWEEN ? AND ?", arrayOf(startDate, endDate))

        val dataList = mutableListOf<Retrofit.WalkData>()
        if (cursor.moveToFirst()) {
            do {
                val time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME))
                val distance = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DIS))
                val speed = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SPEED))

                dataList.add(Retrofit.WalkData(time, distance, speed, null))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return dataList.toTypedArray()
    }
}