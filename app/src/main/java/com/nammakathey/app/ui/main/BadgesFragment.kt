package com.nammakathey.app.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.nammakathey.app.R
import com.nammakathey.app.data.model.Badge
import com.nammakathey.app.databinding.FragmentBadgesBinding
import com.nammakathey.app.ui.adapters.BadgeAdapter
import com.nammakathey.app.utils.SessionManager

class BadgesFragment : Fragment() {

    private var _binding: FragmentBadgesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBadgesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateBadges()
    }

    private fun updateBadges() {
        val userScore = SessionManager(requireContext()).getScore()
        
        val badgesList = listOf(
            Badge(getString(R.string.badge_1_name), getString(R.string.badge_1_desc), 20),
            Badge(getString(R.string.badge_2_name), getString(R.string.badge_2_desc), 40),
            Badge(getString(R.string.badge_3_name), getString(R.string.badge_3_desc), 60),
            Badge(getString(R.string.badge_4_name), getString(R.string.badge_4_desc), 80),
            Badge(getString(R.string.badge_5_name), getString(R.string.badge_5_desc), 100)
        )
        
        val adapter = BadgeAdapter(badgesList, userScore)
        binding.rvBadges.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        updateBadges()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
