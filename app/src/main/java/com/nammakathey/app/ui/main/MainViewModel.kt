package com.nammakathey.app.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nammakathey.app.data.datasource.HeroDataSource
import com.nammakathey.app.data.firebase.FirebaseManager
import com.nammakathey.app.data.model.District
import com.nammakathey.app.data.model.Hero
import com.nammakathey.app.data.repository.DataRepository
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = DataRepository(application)
    private val firebaseManager = FirebaseManager()

    private val _popularDistricts = MutableLiveData<List<District>>()
    val popularDistricts: LiveData<List<District>> = _popularDistricts

    private val _districts = MutableLiveData<List<District>>()
    val districts: LiveData<List<District>> = _districts

    private val _heroes = MutableLiveData<List<Hero>>()
    val heroes: LiveData<List<Hero>> = _heroes

    private val _heroDetail = MutableLiveData<Hero?>()
    val heroDetail: LiveData<Hero?> = _heroDetail

    fun loadHomeData() {
        _popularDistricts.value = repository.getPopularDistricts()
        
        // Update Firestore with the new simplified categories
        viewModelScope.launch { firebaseManager.uploadInitialHeroes(HeroDataSource.heroes) }
    }

    fun loadAllDistricts() {
        _districts.value = repository.getAllDistricts()
    }

    fun getDistrictById(id: String): District? {
        return repository.getDistrictById(id)
    }
    
    fun fetchHeroesForDistrict(districtId: String) {
        viewModelScope.launch {
            val heroList = firebaseManager.getHeroesForDistrict(districtId)
            if (heroList.isNotEmpty()) {
                _heroes.value = heroList
            } else {
                // Fallback to local
                _heroes.value = repository.getHeroesForDistrict(districtId)
            }
        }
    }
    
    fun fetchHeroById(heroId: String) {
        viewModelScope.launch {
            val hero = firebaseManager.getHeroById(heroId)
            if (hero != null) {
                _heroDetail.value = hero
            } else {
                _heroDetail.value = repository.getHeroById(heroId)
            }
        }
    }

    fun saveQuizScore(heroId: String, score: Int, totalMarks: Int, badgeName: String) {
        firebaseManager.saveQuizScore(heroId, score, totalMarks, badgeName)
    }

    fun logout() {
        firebaseManager.logout()
    }

    fun seedDatabase() {
        viewModelScope.launch {
            firebaseManager.uploadInitialHeroes(HeroDataSource.heroes)
        }
    }
}
