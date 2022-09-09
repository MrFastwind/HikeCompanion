package io.github.mrfastwind.hikecompanion.remote

import android.util.Log
import io.github.mrfastwind.hikecompanion.courses.Course
import io.github.mrfastwind.hikecompanion.courses.CourseStages
import io.github.mrfastwind.hikecompanion.courses.Location
import io.github.mrfastwind.hikecompanion.courses.Stage
import io.github.mrfastwind.hikecompanion.remote.CourseJSONConverter.COURSE_ID
import io.github.mrfastwind.hikecompanion.remote.CourseJSONConverter.toJson
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.IllegalArgumentException
import java.util.*

object CourseJSONConverter {
    private const val LONGITUDE: String = "longitude"
    private const val LATITUDE: String = "latitude"
    private const val TAG: String = "CourseJSONConverter"
    private const val STAGES: String = "course_stages"
    private const val COURSE_ID: String = "course_id"
    private const val COURSE_NAME: String="course_name"
    private const val COURSE_DATE: String ="course_date"
    private const val COURSE_DESCRIPTION: String = "course_description"
    private const val COURSE_PUBLIC: String = "course_public"

    fun toCourseStages(jsonObject: JSONObject): CourseStages? {
        return try{
            val course=courseOf(jsonObject)
            val stages=stagesOf(jsonObject)
            CourseStages(course,stages)
        }catch (e: JSONException){
            Log.e(TAG,e.stackTraceToString())
            null
        }catch (e: IllegalArgumentException){
            Log.e(TAG,e.stackTraceToString())
            null
        }
    }

    @Throws(IllegalArgumentException::class)
    private fun stagesOf(jsonObject: JSONObject): List<Stage>{
        val uuid = UUID.fromString(jsonObject.getString(COURSE_ID))
        val list= mutableListOf<Stage>()
        val array =jsonObject.getJSONArray(STAGES)

        for (i in 0 until array.length()){
            val stage = array.getJSONObject(i)
            list.add(Stage(
                uuid,
                i,
                Location(
                    latitude = stage.getDouble(LATITUDE),
                    longitude = stage.getDouble(LONGITUDE)
                )
            ))
        }
        return list
    }

    @Throws(IllegalArgumentException::class)
    private fun courseOf(jsonObject: JSONObject): Course {
        return Course(
            UUID.fromString(jsonObject.getString(COURSE_ID)),
            jsonObject.getString(COURSE_NAME),
            jsonObject.getString(COURSE_DATE),
            jsonObject.getString(COURSE_DESCRIPTION),
            jsonObject.getBoolean(COURSE_PUBLIC)
        )
    }

    fun toCourses(jsonObject: JSONObject): List<CourseStages> {
        return try{
            val list:MutableList<CourseStages> = mutableListOf()
            val array = jsonObject.getJSONArray("COURSES")

            for (i in 0 until array.length()){
                val course = array.getJSONObject(i)
                toCourseStages(course)?.let { list.add(it)}
            }
            list
        }catch (e: JSONException){
            Log.e(TAG,e.stackTraceToString())
            listOf()
        }catch (e: IllegalArgumentException){
            Log.e(TAG,e.stackTraceToString())
            listOf()
        }
    }

    fun getJson(course: CourseStages): JSONObject {
        return course.toJson()
    }

    fun getJson(course: Course): JSONObject {
        return course.toJson()
    }

    private fun CourseStages.toJson():JSONObject{
        val jsonObject = this.course.toJson()
        val array= JSONArray()
        jsonObject.put(STAGES,stages.map { array.put(it.order,it.toJson()) })
        return jsonObject
    }

    private fun Stage.toJson(): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put(LATITUDE,location.latitude)
        jsonObject.put(LONGITUDE,location.longitude)
        return jsonObject
    }

    private fun Course.toJson(): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put(COURSE_ID,id)
        jsonObject.put(COURSE_NAME,name)
        jsonObject.put(COURSE_DATE,date)
        jsonObject.put(COURSE_DESCRIPTION,description)
        jsonObject.put(COURSE_PUBLIC,published)
        return jsonObject
    }




}

