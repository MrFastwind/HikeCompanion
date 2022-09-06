package io.github.mrfastwind.hikecompanion.utils

import android.content.Context
import android.text.format.DateFormat
import io.github.mrfastwind.hikecompanion.courses.CourseStages

import io.github.mrfastwind.hikecompanion.courses.Stage
import java.util.*

object CourseUtilities {

    fun Date.format(context: Context): String {
        return DateFormat.getMediumDateFormat(context).format(this)
    }

    fun courseLength(stages: List<Stage>):Float{
        var sum = 0F
        stages.forEachIndexed { index, stage ->
            if(index!= stages.size-1){
                sum+=stage.location.distanceInMeters(stages[index+1].location)
            }
        }
        return sum
    }

    fun courseLength(stages: CourseStages):Float{
        return courseLength(stages.getOrderedStages())
    }

    fun courseLengthAsString(stages: List<Stage>):String{
        var sum = 0F
        stages.forEachIndexed { index, stage ->
            if(index!= stages.size-1){
                sum+=stage.location.distanceInMeters(stages[index+1].location)
            }
        }
        return String.format("%.2f",sum)
    }

}

