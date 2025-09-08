package uz.umarov.fcneftchi.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import uz.umarov.fcneftchi.databinding.FragmentHomeBinding
import uz.umarov.fcneftchi.ui.home.adapter.HomeAdapter

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

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

        val homeAdapter = HomeAdapter(
            onNavigate = { destinationId -> findNavController().navigate(destinationId) },
            onArticleClick = { article ->
                val action =
                    HomeFragmentDirections.actionHomeFragmentToNewsArticleFragment(article.url)
                findNavController().navigate(action)
            },
            onMatchClick = { match ->
                val action =
                    HomeFragmentDirections.actionHomeFragmentToMatchDetailFragment(match.id.toInt())
                findNavController().navigate(action)
            }
        )

        binding.homeRecyclerView.adapter = homeAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                binding.progressBar.isVisible = state.isLoading
                homeAdapter.submitList(state.items)
            }
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadHomeData()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (binding.homeRecyclerView.adapter as? HomeAdapter)?.release()
        _binding = null
    }
}