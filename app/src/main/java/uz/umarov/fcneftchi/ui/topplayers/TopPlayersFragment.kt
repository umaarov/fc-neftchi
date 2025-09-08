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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTopPlayersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val topPlayerAdapter = TopPlayerAdapter()
        binding.topPlayersRecyclerView.apply {
            adapter = topPlayerAdapter
            layoutManager = LinearLayoutManager(context)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                binding.progressBar.isVisible = state.isLoading
                binding.topPlayersRecyclerView.isVisible = !state.isLoading
                topPlayerAdapter.submitList(state.items)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}