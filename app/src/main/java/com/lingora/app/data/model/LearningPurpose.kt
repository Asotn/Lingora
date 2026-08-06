package com.lingora.app.data.model

/** Why the learner opened Lingora in the first place. Chosen once during
 *  onboarding and editable later from Settings. */
enum class LearningPurpose(val displayName: String, val description: String) {
    TRAVEL("Travel", "Get around comfortably on your next trip"),
    CAREER("Career", "Grow your professional opportunities"),
    EDUCATION("Education", "Support school, university, or a course"),
    CULTURE("Culture & Media", "Enjoy films, music, and books in the original language"),
    FAMILY("Family & Friends", "Talk with the people closest to you"),
    EXAMS("Exams & Certification", "Prepare for an official language test"),
    PERSONAL("Just for Me", "Learn simply because you enjoy it")
}
