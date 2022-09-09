package io.github.mrfastwind.hikecompanion.remote

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.Request.Method
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import io.github.mrfastwind.hikecompanion.courses.Course
import io.github.mrfastwind.hikecompanion.courses.CourseStages
import org.json.JSONException
import org.json.JSONObject
import java.util.UUID

class RegistryClient(private var applicationContext: Context,private val online: Boolean =false) {

    private val SUCCESS: String="Ok"
    private val STATUS: String="status"
    private val requestQueue = Volley.newRequestQueue(applicationContext)

    //--Get Course

    private fun getCourse(liveData: MutableLiveData<CourseStages>, response: JSONObject) {
        liveData.value = CourseJSONConverter.toCourseStages(response)
    }

    private fun getError(volleyError: VolleyError?) {
        volleyError
    }

    fun getCourse(uuid: UUID):LiveData<CourseStages>{
        val livedata: MutableLiveData<CourseStages> = MutableLiveData()
        if(online){
            val request = JsonObjectRequest(
                Method.GET,
                ServerResourses.getCourseUrl(uuid).toString(),
                null,
                {getCourse(livedata,it)},
                ::getError)
        }
        return livedata
    }


    private fun getCourses(liveData: MutableLiveData<List<CourseStages>>,response: JSONObject){
        liveData.value = CourseJSONConverter.toCourses(response)
    }


    fun getCourses():LiveData<List<CourseStages>>{
        val livedata: MutableLiveData<List<CourseStages>> = MutableLiveData()
        if(online){
            val request = JsonObjectRequest(
                Method.GET,
                ServerResourses.getCoursesUrl().toString(),
                null,
                {getCourses(livedata,it)},
                ::getError)
        }
        return livedata
    }

    //--Push Course

    fun pushCourse(course: CourseStages):LiveData<Boolean>{
        val livedata: MutableLiveData<Boolean> = MutableLiveData()
        val jsonObject = CourseJSONConverter.getJson(course)
        if(online){
            val request = JsonObjectRequest(
                Method.POST,
                ServerResourses.getCourseUrl(course.course.id).toString(),
                jsonObject,
                {pushCourse(livedata,it)},
                ::pushError)
        }
        return livedata
    }

    private fun pushCourse(livedata: MutableLiveData<Boolean>, jsonObject: JSONObject) {
        val livedata = MutableLiveData<Boolean>()
        try {
            jsonObject.getString(STATUS)==SUCCESS

            livedata.value=true
        }catch (e: JSONException){
            livedata.value=false
        }
    }

    private fun pushError(volleyError: VolleyError?) {
        volleyError
    }

    //--Delete Course

    private fun deleteCourse(response: String) {
        response.contains("OK")
    }

    private fun deleteError(error: VolleyError){
        error
    }

    private fun deleteCourse(course: Course) {
        if(online){
            val request = StringRequest(
                Request.Method.DELETE,
                ServerResourses.getCourseUrl(course.id).toString(),
                ::deleteCourse,
                ::deleteError
            )
            requestQueue.add(request)
        }
    }

    fun deleteCourse(course: CourseStages) {
        this.deleteCourse(course.course)
    }

    //--Update Course

    fun updateCourse(course: Course):LiveData<Boolean>{
        val livedata: MutableLiveData<Boolean> = MutableLiveData()
        val jsonObject = CourseJSONConverter.getJson(course)
        if(online){
            val request = JsonObjectRequest(
                Method.POST,
                ServerResourses.getCourseUrl(course.id).toString(),
                jsonObject,
                {updateCourse(livedata,it)},
                ::updateError)
        }
        return livedata
    }

    private fun updateCourse(livedata: MutableLiveData<Boolean>, jsonObject: JSONObject) {
        val livedata = MutableLiveData<Boolean>()
        try {
            jsonObject.getString(STATUS)==SUCCESS

            livedata.value=true
        }catch (e: JSONException){
            livedata.value=false
        }
    }

    private fun updateError(volleyError: VolleyError?) {
        volleyError
    }
}