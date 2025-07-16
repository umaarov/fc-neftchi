package uz.umarov.fcneftchi.ui.topplayers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import uz.umarov.fcneftchi.databinding.FragmentTopPlayersBinding
import uz.umarov.fcneftchi.ui.topplayers.adapter.TopPlayerAdapter

@AndroidEntryPoint
class TopPlayersFragment : Fragment() {

    private var _binding: FragmentTopPlayersBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TopPlayersViewModel by viewModels()
    private lateinit var topScorersAdapter: TopPlayerAdapter
    private lateinit var topAssistersAdapter: TopPlayerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTopPlayersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViews()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                binding.progressBar.isVisible = state.isLoading
                binding.contentGroup.isVisible = !state.isLoading

                topScorersAdapter.submitList(state.topScorers)
                topAssistersAdapter.submitList(state.topAssisters)
            }
        }
    }

    private fun setupRecyclerViews() {
        topScorersAdapter = TopPlayerAdapter(TopPlayerAdapter.StatType.GOALS)
        binding.topScorersRecyclerView.apply {
            adapter = topScorersAdapter
            layoutManager = LinearLayoutManager(context)
        }

        topAssistersAdapter = TopPlayerAdapter(TopPlayerAdapter.StatType.ASSISTS)
        binding.topAssistersRecyclerView.apply {
            adapter = topAssistersAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}