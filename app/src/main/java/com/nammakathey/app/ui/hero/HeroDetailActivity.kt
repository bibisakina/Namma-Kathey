package com.nammakathey.app.ui.hero

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.nammakathey.app.R
import com.nammakathey.app.data.model.Hero
import com.nammakathey.app.databinding.ActivityHeroDetailBinding
import com.nammakathey.app.ui.base.BaseActivity
import com.nammakathey.app.ui.main.MainViewModel
import com.nammakathey.app.ui.quiz.QuizActivity
import com.nammakathey.app.utils.SessionManager
import java.util.Locale

class HeroDetailActivity : BaseActivity(), TextToSpeech.OnInitListener {

    private lateinit var binding: ActivityHeroDetailBinding
    private val viewModel: MainViewModel by viewModels()
    private var tts: TextToSpeech? = null
    private var isSpeaking = false
    private var isTtsReady = false
    private var hero: Hero? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHeroDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hero = intent.getSerializableExtra("hero") as? Hero
        if (hero != null) {
            setupToolbar(hero!!)
            displayHeroDetails(hero!!)
        } else {
            val heroId = intent.getStringExtra("heroId") ?: ""
            viewModel.fetchHeroById(heroId)
        }

        viewModel.heroDetail.observe(this) { fetchedHero ->
            if (fetchedHero != null) {
                hero = fetchedHero
                setupToolbar(fetchedHero)
                displayHeroDetails(fetchedHero)
            }
        }

        // Initialize TextToSpeech
        binding.fabSpeaker.isEnabled = false
        tts = TextToSpeech(this, this)

        binding.fabSpeaker.setOnClickListener {
            toggleSpeech()
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val isKannada = SessionManager(this).getLanguage() == "kn"
            val locale = if (isKannada) Locale("kn", "IN") else Locale.US
            val result = tts?.setLanguage(locale)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, getString(R.string.error_tts_not_supported), Toast.LENGTH_SHORT).show()
            } else {
                isTtsReady = true
                runOnUiThread {
                    binding.fabSpeaker.isEnabled = true
                }
            }

            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}
                override fun onDone(utteranceId: String?) {
                    runOnUiThread {
                        isSpeaking = false
                        binding.fabSpeaker.setImageResource(android.R.drawable.ic_lock_silent_mode_off)
                    }
                }
                override fun onError(utteranceId: String?) {
                    runOnUiThread {
                        isSpeaking = false
                        binding.fabSpeaker.setImageResource(android.R.drawable.ic_lock_silent_mode_off)
                    }
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?, errorCode: Int) {
                    onError(utteranceId)
                }
            })
        } else {
            Toast.makeText(this, getString(R.string.tts_error), Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleSpeech() {
        if (isSpeaking) {
            stopSpeech()
        } else {
            startSpeech()
        }
    }

    private fun startSpeech() {
        if (!isTtsReady || tts == null) {
            Toast.makeText(this, "Voice is loading...", Toast.LENGTH_SHORT).show()
            return
        }

        val currentHero = hero ?: return
        val isKannada = SessionManager(this).getLanguage() == "kn"

        val textToRead = if (isKannada) {
            "${currentHero.nameKn}. ${currentHero.bioKn}. ${currentHero.familyInfoKn}. ${currentHero.achievementsKn}. ${currentHero.quoteKn}"
        } else {
            "${currentHero.nameEn}. ${currentHero.bioEn}. ${currentHero.familyInfoEn}. ${currentHero.achievementsEn}. ${currentHero.quoteEn}"
        }

        val result = tts?.speak(textToRead, TextToSpeech.QUEUE_FLUSH, null, "HeroDetailTTS")
        if (result == TextToSpeech.SUCCESS) {
            isSpeaking = true
            binding.fabSpeaker.setImageResource(android.R.drawable.ic_media_pause)
            Toast.makeText(this, "Speaking...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopSpeech() {
        tts?.stop()
        isSpeaking = false
        binding.fabSpeaker.setImageResource(android.R.drawable.ic_lock_silent_mode_off)
        Toast.makeText(this, getString(R.string.tts_stopped), Toast.LENGTH_SHORT).show()
    }

    override fun onPause() {
        super.onPause()
        if (isSpeaking) {
            stopSpeech()
        }
    }

    override fun onDestroy() {
        tts?.let {
            it.stop()
            it.shutdown()
        }
        tts = null
        super.onDestroy()
    }

    private fun setupToolbar(hero: Hero) {
        val isKannada = SessionManager(this).getLanguage() == "kn"
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "" // Using custom views or just name
        
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun displayHeroDetails(hero: Hero) {
        val isKannada = SessionManager(this).getLanguage() == "kn"
        val context = this

        // Basic Info
        binding.tvHeroName.text = if (isKannada) hero.nameKn else hero.nameEn
        binding.tvCategory.text = if (isKannada) hero.categoryKn else hero.categoryEn
        
        val district = viewModel.getDistrictById(hero.districtId)
        val districtName = if (district != null) {
            if (isKannada) district.nameKn else district.nameEn
        } else {
            hero.districtId
        }
        binding.tvDistrictName.text = getString(R.string.label_district, districtName)

        // Detailed Content
        binding.tvBio.text = if (isKannada) hero.bioKn else hero.bioEn
        binding.tvFamily.text = if (isKannada) hero.familyInfoKn else hero.familyInfoEn
        binding.tvAchievements.text = if (isKannada) hero.achievementsKn else hero.achievementsEn
        binding.tvContribution.text = if (isKannada) hero.contributionKn else hero.contributionEn
        binding.tvQuote.text = if (isKannada) hero.quoteKn else hero.quoteEn
        binding.tvFact.text = if (isKannada) hero.factKn else hero.factEn

        // Image
        val resourceId = hero.getImageResId(this)
        Glide.with(this)
            .load(if (resourceId != 0) resourceId else R.drawable.search_bg)
            .centerCrop()
            .into(binding.ivHeroPhoto)

        // Quiz Button
        if (hero.quizzes.isNotEmpty()) {
            binding.btnStartQuiz.visibility = View.VISIBLE
            binding.btnStartQuiz.setOnClickListener {
                val intent = Intent(this, QuizActivity::class.java)
                intent.putExtra("hero", hero)
                startActivity(intent)
            }
        } else {
            binding.btnStartQuiz.visibility = View.GONE
        }
    }
}
