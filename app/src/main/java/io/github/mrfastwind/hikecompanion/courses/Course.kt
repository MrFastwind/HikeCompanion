package io.github.mrfastwind.hikecompanion.courses

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "COURSE")
data class Course(
    @PrimaryKey val id:UUID = UUID.randomUUID(),
    var name: String,
    val date: String,
    var description: String,
    var published: Boolean = false
)