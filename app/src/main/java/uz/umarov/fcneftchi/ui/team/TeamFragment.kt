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
import uz.umarov.fcneftchi.ui.team.adapter.PlayerAdapter
import uz.umarov.fcneftchi.util.applySystemBarPadding

@AndroidEntryPoint
class TeamFragment : Fragment() {

    private var _binding: FragmentTeamBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TeamViewModel by viewModels()
    private lateinit var playerAdapter: PlayerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
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
                playerAdapter.submitList(state.players)
            }
        }
    }

    private fun setupRecyclerView() {
        playerAdapter = PlayerAdapter()
        binding.teamRecyclerView.apply {
            adapter = playerAdapter
            layoutManager = GridLayoutManager(context, 3)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}