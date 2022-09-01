package io.github.mrfastwind.hikecompanion.courses

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "PICTURE",)
class Picture(
    @PrimaryKey val id : UUID,
    val date:String,
    @Embedded val location:Location
    )