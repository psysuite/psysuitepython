package org.albaspazio.psysuite.adaptive

data class TaskADAParams(
    val min: Float,
    val max: Float,
    val ntrials: Int = -1,
    val offset: Long = 0,
)