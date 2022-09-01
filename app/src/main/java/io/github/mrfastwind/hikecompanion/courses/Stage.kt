package io.github.mrfastwind.hikecompanion.courses

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import java.util.*

@Entity(tableName = "STAGE", primaryKeys = ["course","order"])
class Stage (
    val course: UUID,
    val order: Int,
    @Embedded val location: Location
    )
