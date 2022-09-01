package io.github.mrfastwind.hikecompanion.utils

import io.github.mrfastwind.hikecompanion.courses.Stage

object CourseUtilities {
    fun courseLength(stages: List<Stage>):Float{
        var sum = 0F
        stages.forEachIndexed { index, stage ->
            if(index!= stages.size-1){
                sum+=stage.location.distanceInMeters(stages[index+1].location)
            }
        }
        return sum
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

