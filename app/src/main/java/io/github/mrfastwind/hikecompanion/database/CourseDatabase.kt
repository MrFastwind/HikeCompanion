package io.github.mrfastwind.hikecompanion.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import io.github.mrfastwind.hikecompanion.courses.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Database(entities = [Course::class,Stage::class,Picture::class], version = 1)
abstract class CourseDatabase : RoomDatabase() {
    abstract fun courseDAO(): CourseDAO
    abstract fun pictureDAO(): PictureDAO
    abstract fun stageDAO(): StageDAO

    companion object {
        @Volatile
        private var INSTANCE: CourseDatabase? = null
        val executor: ExecutorService = Executors.newFixedThreadPool(4)
        fun getDatabase(context: Context): CourseDatabase? {
            if (INSTANCE == null) {
                //The synchronized is to prevent multiple instances being created.
                synchronized(CourseDatabase::class.java) {
                    //If the db has not yet been created, the builder creates it.
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            CourseDatabase::class.java, "hike_database"
                        ).build()
                    }
                }
            }
            return INSTANCE
        }
    }
}

@Dao
interface PictureDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addPicture(picture: Picture)


    fun getPictureByDistance(location: Location, distance: Int):LiveData<List<Picture>>{
        return getPictureByDistance(location.latitude,location.longitude,distance)
    }

    @Transaction
    @Query("SELECT * FROM PICTURE P " +
            "WHERE latitude BETWEEN :latitude-:distance AND :latitude+:distance " +
            "AND longitude BETWEEN :longitude-:distance AND :longitude+:distance")
    fun getPictureByDistance(latitude: Double, longitude: Double, distance: Int): LiveData<List<Picture>>
}


@Dao
interface CourseDAO {
    //The selected OnConflictStrategy ignores a new CardItem if it's already in the list
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addCourse(course: Course)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addCourseWithStages(course:Course, stages:List<Stage>)

    @get:Transaction
    @get:Query("SELECT * FROM COURSE")
    val getCoursesWithStages: LiveData<List<CourseStages>>

    // @Transaction: anything inside the method runs in a single transaction.
    @get:Transaction
    @get:Query("SELECT * FROM COURSE")
    val courses: LiveData<List<Course>>

    @Transaction
    @Query("SELECT * FROM COURSE WHERE published = 1")
    fun getPublicCourseStages():LiveData<List<CourseStages>>

    @Update
    fun updateCourse(course: Course)

    @Transaction
    @Query("SELECT * FROM COURSE WHERE id = :uuid")
    fun getCourse(uuid: String):CourseStages?
}

@Dao
interface StageDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addStage(stage: Stage)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addStage(stage: List<Stage>)
}

