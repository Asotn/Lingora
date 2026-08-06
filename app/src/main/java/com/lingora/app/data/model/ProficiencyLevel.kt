package com.lingora.app.data.model

/** The learner's self-reported starting level, gathered during onboarding. */
enum class ProficiencyLevel(val displayName: String, val description: String) {
    BEGINNER("Beginner", "I'm just getting started"),
    INTERMEDIATE("Intermediate", "I know the basics and want to grow"),
    ADVANCED("Advanced", "I'm comfortable in most conversations"),
    FLUENT("Fluent", "I'm refining a language I already speak well")
}
