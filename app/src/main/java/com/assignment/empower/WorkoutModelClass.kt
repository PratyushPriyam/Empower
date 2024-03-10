package com.assignment.empower

data class WorkoutModelClass(val name: String, val sets: Int, val reps: Int) {
    // Add an empty constructor
    constructor() : this("", 0, 0) {}
}
