package com.nammakathey.app.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.nammakathey.app.databinding.FragmentDistrictsBinding
import com.nammakathey.app.ui.adapters.DistrictAdapter
import com.nammakathey.app.ui.hero.HeroListActivity
import com.nammakathey.app.utils.SessionManager

class DistrictsFragment : Fragment() {

    private var _binding: FragmentDistrictsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDistrictsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadAllDistricts()
        
        val isKannada = SessionManager(requireContext()).getLanguage() == "kn"

        viewModel.districts.observe(viewLifecycleOwner) { districts ->
            val adapter = DistrictAdapter(districts = districts) { district ->
                val intent = Intent(requireContext(), HeroListActivity::class.java)
                intent.putExtra("districtId", district.id)
                intent.putExtra("districtName", if (isKannada) district.nameKn else district.nameEn)
                startActivity(intent)
            }
            binding.rvDistricts.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
