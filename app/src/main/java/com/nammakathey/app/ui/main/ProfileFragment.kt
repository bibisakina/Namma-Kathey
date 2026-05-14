package com.nammakathey.app.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.nammakathey.app.databinding.FragmentProfileBinding
import com.nammakathey.app.ui.auth.LoginActivity
import com.nammakathey.app.utils.SessionManager

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager
    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())

        updateUI()

        val currentLang = sessionManager.getLanguage()
        if (currentLang == "kn") {
            binding.rbKannada.isChecked = true
        } else {
            binding.rbEnglish.isChecked = true
        }

        binding.rgLanguage.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == binding.rbKannada.id) {
                sessionManager.setLanguage("kn")
            } else {
                sessionManager.setLanguage("en")
            }
            requireActivity().recreate()
        }

        binding.btnLogout.setOnClickListener {
            viewModel.logout()
            sessionManager.logout()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun updateUI() {
        val score = sessionManager.getScore()
        binding.tvUserName.text = sessionManager.getUserName()
        binding.tvEmail.text = sessionManager.getUserEmail()
        binding.tvScore.text = score.toString()
        binding.tvBadgesCount.text = sessionManager.getUnlockedBadgesCount().toString()
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
