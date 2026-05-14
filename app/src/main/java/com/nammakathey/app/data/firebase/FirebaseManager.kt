package com.nammakathey.app.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Transaction
import com.nammakathey.app.data.model.Hero
import kotlinx.coroutines.tasks.await

class FirebaseManager {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun getCurrentUser() = auth.currentUser

    fun logout() {
        auth.signOut()
    }

    suspend fun getHeroesForDistrict(districtId: String): List<Hero> {
        return try {
            val snapshot = db.collection("heroes")
                .whereEqualTo("districtId", districtId)
                .get()
                .await()
            snapshot.toObjects(Hero::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getHeroById(heroId: String): Hero? {
        return try {
            val snapshot = db.collection("heroes")
                .document(heroId)
                .get()
                .await()
            snapshot.toObject(Hero::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun saveQuizScore(
        heroId: String,
        score: Int,
        totalMarks: Int,
        badgeName: String
    ) {
        val user = auth.currentUser ?: return
        val scoreData = hashMapOf(
            "userId" to user.uid,
            "userName" to (user.displayName ?: "Unknown"),
            "heroId" to heroId,
            "score" to score,
            "totalMarks" to totalMarks,
            "badgeName" to badgeName,
            "timestamp" to System.currentTimeMillis()
        )
        db.collection("quiz_scores").add(scoreData)
        
        // Update badge progress
        updateBadgeProgress(score)
    }

    private fun updateBadgeProgress(newScore: Int) {
        val user = auth.currentUser ?: return
        val userRef = db.collection("badges").document(user.uid)
        
        db.runTransaction { transaction ->
            val snapshot = transaction.get(userRef)
            val currentTotalScore = (snapshot.getLong("totalScore") ?: 0L) + newScore
            val badgeName = when {
                currentTotalScore >= 100 -> "Karnataka Legend"
                currentTotalScore >= 80 -> "Heritage Star"
                currentTotalScore >= 60 -> "Explorer"
                currentTotalScore >= 40 -> "Learner"
                currentTotalScore >= 20 -> "Beginner"
                else -> "None"
            }
            
            val data = HashMap<String, Any>()
            data["userId"] = user.uid
            data["totalScore"] = currentTotalScore
            data["badgeName"] = badgeName
            data["lastUpdated"] = System.currentTimeMillis()
            
            transaction.set(userRef, data)
            null
        }
    }
    
    suspend fun uploadInitialHeroes(heroes: List<Hero>) {
        val collection = db.collection("heroes")
        for (hero in heroes) {
            collection.document(hero.id).set(hero).await()
        }
    }
}
