package uz.umarov.fcneftchi.ui.team

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import uz.umarov.fcneftchi.databinding.FragmentTeamBinding
import uz.umarov.fcneftchi.ui.MainActivity
import uz.umarov.fcneftchi.ui.team.adapter.PlayerAdapter

@AndroidEntryPoint
class TeamFragment : Fragment() {

    private var _binding: FragmentTeamBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TeamViewModel by viewModels()
    private lateinit var playerAdapter: PlayerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTeamBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                binding.progressBar.isVisible = state.isLoading
                binding.teamRecyclerView.isVisible = !state.isLoading
                playerAdapter.submitList(state.items)
            }
        }
    }

    private fun setupRecyclerView() {
        playerAdapter = PlayerAdapter()
        val gridLayoutManager = GridLayoutManager(context, 2)

        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (playerAdapter.getItemViewType(position)) {
                    0 -> 2
                    1 -> 1
                    else -> 1
                }
            }
        }

        binding.teamRecyclerView.apply {
            adapter = playerAdapter
            layoutManager = gridLayoutManager
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.hideToolbarOnly()
    }

    override fun onPause() {
        super.onPause()
        (activity as? MainActivity)?.showMainUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}