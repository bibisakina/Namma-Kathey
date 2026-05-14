package com.nammakathey.app.ui.storybook

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.nammakathey.app.R
import com.nammakathey.app.data.model.Hero
import com.nammakathey.app.databinding.ActivityStorybookBinding
import com.nammakathey.app.ui.adapters.StoryPagerAdapter
import com.nammakathey.app.ui.base.BaseActivity
import com.nammakathey.app.ui.main.MainViewModel
import com.nammakathey.app.ui.quiz.QuizActivity
import com.nammakathey.app.utils.SessionManager
import java.util.*

class StorybookActivity : BaseActivity(), TextToSpeech.OnInitListener {

    private lateinit var binding: ActivityStorybookBinding
    private val viewModel: MainViewModel by viewModels()
    private var tts: TextToSpeech? = null
    private var storyPages: List<String> = emptyList()
    private var heroId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStorybookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        heroId = intent.getStringExtra("heroId") ?: ""
        val hero = intent.getSerializableExtra("hero") as? Hero

        if (hero == null) {
            Toast.makeText(this, getString(R.string.info_coming_soon), Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val isKannada = SessionManager(this).getLanguage() == "kn"
        binding.toolbar.title = getString(R.string.story_title_format, if (isKannada) hero.nameKn else hero.nameEn)
        binding.toolbar.setNavigationOnClickListener { finish() }

        storyPages = if (isKannada){
            listOf(hero.bioKn)
        }else{
            listOf(hero.bioEn)
        }
        val adapter = StoryPagerAdapter(storyPages)
        binding.viewPager.adapter = adapter

        tts = TextToSpeech(this, this)

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tts?.stop()
                if ((position == (storyPages.size - 1)) && hero.quizzes.isNotEmpty()) {
                    binding.btnStartQuiz.visibility = View.VISIBLE
                } else {
                    binding.btnStartQuiz.visibility = View.GONE
                }
            }
        })

        binding.btnPrev.setOnClickListener {
            val current = binding.viewPager.currentItem
            if (current > 0) {
                binding.viewPager.currentItem = current - 1
            }
        }

        binding.btnNext.setOnClickListener {
            val current = binding.viewPager.currentItem
            if (current < storyPages.size - 1) {
                binding.viewPager.currentItem = current + 1
            }
        }

        binding.btnTts.setOnClickListener {
            val current = binding.viewPager.currentItem
            if (current < storyPages.size) {
                speakOut(storyPages[current])
            }
        }
        
        binding.btnStartQuiz.setOnClickListener {
            val intent = Intent(this, QuizActivity::class.java)
            intent.putExtra("hero", hero)
            startActivity(intent)
            finish()
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            if ((result == TextToSpeech.LANG_MISSING_DATA) || (result == TextToSpeech.LANG_NOT_SUPPORTED)) {
                Toast.makeText(this, getString(R.string.error_tts_not_supported), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun speakOut(text: String) {
        val isKannada = SessionManager(this).getLanguage() == "kn"
        if (isKannada) {
            tts?.language = Locale("kn", "IN")
        } else {
            tts?.language = Locale.US
        }
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    override fun onDestroy() {
        if (tts != null) {
            tts?.stop()
            tts?.shutdown()
        }
        super.onDestroy()
    }
}
