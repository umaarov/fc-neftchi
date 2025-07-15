package uz.umarov.fcneftchi.ui.matches

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import uz.umarov.fcneftchi.databinding.FragmentMatchesBinding
import uz.umarov.fcneftchi.ui.matches.adapter.MatchAdapter

@AndroidEntryPoint
class MatchesFragment : Fragment() {

    private var _binding: FragmentMatchesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MatchesViewModel by viewModels()
    private lateinit var matchAdapter: MatchAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMatchesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupTabs()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                binding.progressBar.isVisible = state.isLoading
                binding.matchesRecyclerView.isVisible = !state.isLoading
                if (binding.tabLayout.selectedTabPosition == 0) {
                    matchAdapter.submitList(state.fixtures)
                } else {
                    matchAdapter.submitList(state.results)
                }
            }
        }
    }

    private fun setupRecyclerView() {
        matchAdapter = MatchAdapter()
        binding.matchesRecyclerView.apply {
            adapter = matchAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val state = viewModel.uiState.value
                when (tab?.position) {
                    0 -> matchAdapter.submitList(state.fixtures)
                    1 -> matchAdapter.submitList(state.results)
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}