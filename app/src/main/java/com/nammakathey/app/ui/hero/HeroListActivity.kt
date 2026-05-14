package com.nammakathey.app.ui.hero

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.nammakathey.app.R
import com.nammakathey.app.data.model.District
import com.nammakathey.app.databinding.ActivityHeroListBinding
import com.nammakathey.app.ui.adapters.HeroAdapter
import com.nammakathey.app.ui.base.BaseActivity
import com.nammakathey.app.ui.main.MainViewModel
import com.nammakathey.app.utils.SessionManager

class HeroListActivity : BaseActivity() {

    private lateinit var binding: ActivityHeroListBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHeroListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val districtId = intent.getStringExtra("districtId") ?: ""
        val district = viewModel.getDistrictById(districtId)
        val isKannada = SessionManager(this).getLanguage() == "kn"
        
        val districtName = if (district != null) {
            if (isKannada) district.nameKn else district.nameEn
        } else {
            intent.getStringExtra("districtName") ?: getString(R.string.nav_districts)
        }

        setDistrictInfo(district, districtName)

        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.progressBar.visibility = View.VISIBLE
        viewModel.fetchHeroesForDistrict(districtId)
        
        viewModel.heroes.observe(this) { heroes ->
            binding.progressBar.visibility = View.GONE
            if (heroes.isEmpty()) {
                binding.tvEmpty.text = getString(R.string.no_heroes_found)
                binding.tvEmpty.visibility = View.VISIBLE
                binding.rvHeroes.visibility = View.GONE
            } else {
                binding.tvEmpty.visibility = View.GONE
                binding.rvHeroes.visibility = View.VISIBLE
                val adapter = HeroAdapter(heroes) { hero ->
                    val intent = Intent(this, HeroDetailActivity::class.java)
                    intent.putExtra("hero", hero)
                    startActivity(intent)
                }
                binding.rvHeroes.adapter = adapter
            }
        }
    }

    private fun setDistrictInfo(district: District?, name: String) {
        binding.tvDistrictName.text = name ?: ""
        
        val id = district?.id ?: ""
        val stringResName = "desc_$id"
        val resId = if (id.isNotEmpty()) resources.getIdentifier(stringResName, "string", packageName) else 0
        
        binding.tvDistrictDesc.text = if (resId != 0) {
            getString(resId)
        } else {
            getString(R.string.desc_default, name ?: "")
        }

        val imageRes = district?.imageRes ?: 0
        
        Glide.with(this)
            .load(if (imageRes != 0) imageRes else R.drawable.search_bg)
            .centerCrop()
            .into(binding.ivDistrictHeader)
    }
}
