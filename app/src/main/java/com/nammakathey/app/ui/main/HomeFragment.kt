package com.nammakathey.app.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.nammakathey.app.R
import com.nammakathey.app.databinding.FragmentHomeBinding
import com.nammakathey.app.utils.SessionManager

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        sessionManager = SessionManager(requireContext())

        setupUI()
    }

    private fun setupUI() {
        // Score and Badges
        binding.tvScore.text = sessionManager.getScore().toString()
        binding.tvBadgesCount.text = sessionManager.getUnlockedBadgesCount().toString()

        // Load Karnataka Banner Image
        Glide.with(this)
            .load(R.drawable.karnataka)
            .centerCrop()
            .into(binding.ivHeaderReal)
    }

    override fun onResume() {
        super.onResume()
        // Refresh score and badges
        binding.tvScore.text = sessionManager.getScore().toString()
        binding.tvBadgesCount.text = sessionManager.getUnlockedBadgesCount().toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
