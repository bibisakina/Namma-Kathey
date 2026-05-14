package com.nammakathey.app.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AppData(
    @SerializedName("featured_districts") val featuredDistrictIds: List<String> = emptyList(),
    @SerializedName("popular_districts") val popularDistrictIds: List<String> = emptyList(),
    val districts: List<District> = emptyList(),
    val heroes: List<Hero> = emptyList(),
)

data class District(
    val id: String,
    val nameEn: String,
    val nameKn: String,
    val imageRes: Int
) : Serializable

data class Hero(
    val id: String = "",
    val districtId: String = "",
    val nameEn: String = "",
    val nameKn: String = "",
    val categoryEn: String = "",
    val categoryKn: String = "",
    val photoRes: String = "",
    val bioEn: String = "",
    val bioKn: String = "",
    val familyInfoEn: String = "",
    val familyInfoKn: String = "",
    val achievementsEn: String = "",
    val achievementsKn: String = "",
    val contributionEn: String = "",
    val contributionKn: String = "",
    val quoteEn: String = "",
    val quoteKn: String = "",
    val factEn: String = "",
    val factKn: String = "",
    val quizzes: List<QuizQuestion> = emptyList()
) : Serializable {
    fun getImageResId(context: android.content.Context): Int {
        return context.resources.getIdentifier(photoRes, "drawable", context.packageName)
    }
}

data class QuizQuestion(
    val question: String = "",
    val questionKn: String = "",
    val options: List<String> = emptyList(),
    val optionsKn: List<String> = emptyList(),
    val answerIndex: Int = 0
) : Serializable

data class Badge(
    val name: String,
    val description: String,
    val requiredScore: Int
)
