package com.nammakathey.app.data.repository

import android.content.Context
import com.google.gson.Gson
import com.nammakathey.app.data.model.AppData
import com.nammakathey.app.data.model.District
import com.nammakathey.app.data.model.Hero
import java.io.InputStreamReader

class DataRepository(private val context: Context) {

    private var appData: AppData? = null

    init {
        loadData()
    }

    private fun loadData() {
        try {
            val inputStream = context.assets.open("data.json")
            val reader = InputStreamReader(inputStream)
            appData = Gson().fromJson(reader, AppData::class.java)
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getAllDistricts(): List<District> {
        return com.nammakathey.app.data.datasource.DistrictDataSource.getAllDistricts()
    }

    fun getDistrictById(id: String): District? {
        return com.nammakathey.app.data.datasource.DistrictDataSource.getDistrictById(id)
    }

    fun getFeaturedDistricts(): List<District> {
        return getAllDistricts().take(4) // Example logic, or keep as is if needed
    }

    fun getPopularDistricts(): List<District> {
        val popularIds = appData?.popularDistrictIds ?: emptyList()
        return getAllDistricts().filter { it.id in popularIds }
    }

    fun getHeroesForDistrict(districtId: String): List<Hero> {
        return com.nammakathey.app.data.datasource.HeroDataSource.getHeroesForDistrict(districtId)
    }

    fun getHeroById(heroId: String): Hero? {
        return com.nammakathey.app.data.datasource.HeroDataSource.getHeroById(heroId)
    }

    fun getHeroOfTheDay(): Hero? {
        return com.nammakathey.app.data.datasource.HeroDataSource.heroes.randomOrNull()
    }

    fun search(query: String): List<Any> {
        val results = mutableListOf<Any>()
        val lowerQuery = query.lowercase()
        
        com.nammakathey.app.data.datasource.DistrictDataSource.districts
            .filter { it.nameEn.lowercase().contains(lowerQuery) || it.nameKn.contains(lowerQuery) }
            .let { results.addAll(it) }
            
        com.nammakathey.app.data.datasource.HeroDataSource.heroes
            .filter { it.nameEn.lowercase().contains(lowerQuery) || it.nameKn.contains(lowerQuery) }
            .let { results.addAll(it) }
        
        return results
    }
}
