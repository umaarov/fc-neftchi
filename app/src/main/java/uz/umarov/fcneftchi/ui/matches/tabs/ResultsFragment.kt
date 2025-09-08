package uz.umarov.fcneftchi.ui.matches.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import uz.umarov.fcneftchi.databinding.FragmentResultsBinding
import uz.umarov.fcneftchi.ui.matches.MatchesFragmentDirections
import uz.umarov.fcneftchi.ui.matches.adapter.ResultAdapter // <-- Use the new ResultAdapter

@AndroidEntryPoint
class ResultsFragment : Fragment() {
    private var _binding: FragmentResultsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ResultsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val resultAdapter = ResultAdapter { match ->
            val action =
                MatchesFragmentDirections.actionMatchesFragmentToMatchDetailFragment(match.id.toInt())
            findNavController().navigate(action)
        }
        binding.resultsRecyclerView.apply {
            adapter = resultAdapter
            layoutManager = LinearLayoutManager(context)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect {
                binding.progressBar.isVisible = it.isLoading
                resultAdapter.submitList(it.items)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}