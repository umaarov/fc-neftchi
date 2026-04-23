package uz.umarov.fcneftchi.ui.bookmarks

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
import uz.umarov.fcneftchi.R
import uz.umarov.fcneftchi.databinding.FragmentBookmarkListBinding
import uz.umarov.fcneftchi.ui.bookmarks.adapter.BookmarkedPlayerAdapter

@AndroidEntryPoint
class BookmarkedPlayersFragment : Fragment() {

    private var _binding: FragmentBookmarkListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BookmarkedPlayersViewModel by viewModels()
    private lateinit var adapter: BookmarkedPlayerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookmarkListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = BookmarkedPlayerAdapter { player ->
            val action = BookmarksFragmentDirections
                .actionBookmarksFragmentToPlayerProfileFragment(player.id)
            findNavController().navigate(action)
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@BookmarkedPlayersFragment.adapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.players.collect { players ->
                if (players.isEmpty()) {
                    binding.recyclerView.isVisible = false
                    binding.stateView.showEmpty(messageRes = R.string.state_empty_bookmark_players)
                } else {
                    binding.stateView.hide()
                    binding.recyclerView.isVisible = true
                    adapter.submitList(players)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerView.adapter = null
        _binding = null
    }
}
