package com.nammakathey.app.ui.quiz

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.nammakathey.app.R
import com.nammakathey.app.data.model.Hero
import com.nammakathey.app.data.model.QuizQuestion
import com.nammakathey.app.databinding.ActivityQuizBinding
import com.nammakathey.app.ui.base.BaseActivity
import com.nammakathey.app.ui.main.MainViewModel
import com.nammakathey.app.utils.SessionManager

class QuizActivity : BaseActivity() {

    private lateinit var binding: ActivityQuizBinding
    private val viewModel: MainViewModel by viewModels()
    private var quizzes: List<QuizQuestion> = emptyList()
    private var currentQuestionIndex = 0
    private var correctCount = 0
    private var wrongCount = 0
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        binding.toolbar.setNavigationOnClickListener { finish() }

        val hero = intent.getSerializableExtra("hero") as? Hero

        if (hero == null || hero.quizzes.isEmpty()) {
            Toast.makeText(this, getString(R.string.no_quiz_available), Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        quizzes = hero.quizzes.take(5)
        loadQuestion()

        binding.btnNext.setOnClickListener {
            checkAnswer()
        }

        binding.btnRetry.setOnClickListener {
            restartQuiz()
        }

        binding.btnFinish.setOnClickListener {
            finish()
        }
    }

    private fun restartQuiz() {
        currentQuestionIndex = 0
        correctCount = 0
        wrongCount = 0
        binding.layoutResult.visibility = View.GONE
        loadQuestion()
    }

    private fun loadQuestion() {
        val isKannada = sessionManager.getLanguage() == "kn"
        if (currentQuestionIndex < quizzes.size) {
            binding.rgOptions.clearCheck()
            val quiz = quizzes[currentQuestionIndex]
            binding.tvProgress.text = getString(R.string.quiz_progress_format, currentQuestionIndex + 1, quizzes.size)
            binding.tvQuestion.text = if (isKannada) quiz.questionKn else quiz.question

            val options = if (isKannada) quiz.optionsKn else quiz.options
            binding.rbOption1.text = options.getOrNull(0) ?: ""
            binding.rbOption2.text = options.getOrNull(1) ?: ""
            binding.rbOption3.text = options.getOrNull(2) ?: ""
            binding.rbOption4.text = options.getOrNull(3) ?: ""
            
            if (currentQuestionIndex == quizzes.size - 1) {
                binding.btnNext.text = getString(R.string.btn_finish_quiz)
            } else {
                binding.btnNext.text = getString(R.string.btn_next)
            }
        } else {
            showResult()
        }
    }

    private fun checkAnswer() {
        val selectedId = binding.rgOptions.checkedRadioButtonId
        if (selectedId == -1) {
            Toast.makeText(this, getString(R.string.select_answer_toast), Toast.LENGTH_SHORT).show()
            return
        }

        val selectedIndex = when (selectedId) {
            R.id.rbOption1 -> 0
            R.id.rbOption2 -> 1
            R.id.rbOption3 -> 2
            R.id.rbOption4 -> 3
            else -> -1
        }

        if (selectedIndex == quizzes[currentQuestionIndex].answerIndex) {
            correctCount++
        } else {
            wrongCount++
        }

        currentQuestionIndex++
        
        if (currentQuestionIndex < quizzes.size) {
            loadQuestion()
        } else {
            showResult()
        }
    }

    private fun showResult() {
        val quizScore = correctCount * 2 // 2 marks per correct answer
        val totalPossible = quizzes.size * 2
        
        val hero = intent.getSerializableExtra("hero") as? Hero
        val heroId = hero?.id ?: "unknown"

        // Calculate badge based on rules:
        // 20 marks = Beginner, 40 marks = Learner, 60 marks = Explorer, 
        // 80 marks = Heritage Star, 100 marks = Karnataka Legend
        // This is usually cumulative, but here we can save the badge earned for this quiz or just update total.
        // The requirement says: "Store badge progress in Firestore and update automatically."
        
        val badgeName = when {
            quizScore >= 100 -> "Karnataka Legend"
            quizScore >= 80 -> "Heritage Star"
            quizScore >= 60 -> "Explorer"
            quizScore >= 40 -> "Learner"
            quizScore >= 20 -> "Beginner"
            else -> "None"
        }
        
        viewModel.saveQuizScore(heroId, quizScore, totalPossible, badgeName)
        sessionManager.addScore(quizScore)
        
        binding.layoutResult.visibility = View.VISIBLE
        binding.tvFinalScore.text = getString(R.string.quiz_score_out_of, quizScore)
        
        // Show correct and wrong count
        binding.tvStats.text = getString(R.string.stats_format, correctCount, wrongCount)
        
        // Handle result titles and buttons in Kannada if needed
        val isKannada = sessionManager.getLanguage() == "kn"
        if (isKannada) {
            binding.tvResultTitle.text = "ರಸಪ್ರಶ್ನೆ ಪೂರ್ಣಗೊಂಡಿದೆ!"
            binding.btnRetry.text = "ಮತ್ತೆ ಪ್ರಯತ್ನಿಸಿ"
            binding.btnFinish.text = "ಹಿಂದೆ"
        }
    }
}
