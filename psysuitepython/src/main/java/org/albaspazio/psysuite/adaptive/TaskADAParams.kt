package org.albaspazio.psysuite.adaptive

data class TaskADAParams(
    val range: Float,
    val ntrials: Int = -1,
    val min: Float = 0.1F,
    val offset: Long = 0,
    val exclusion_width: Long = 40
)