package uz.umarov.fcneftchi.ui.bookmarks

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
import uz.umarov.fcneftchi.R
import uz.umarov.fcneftchi.databinding.FragmentBookmarkListBinding
import uz.umarov.fcneftchi.ui.videos.adapter.VideoAdapter

@AndroidEntryPoint
class BookmarkedVideosFragment : Fragment() {

    private var _binding: FragmentBookmarkListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BookmarkedVideosViewModel by viewModels()
    private lateinit var adapter: VideoAdapter

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
        adapter = VideoAdapter(
            lifecycle = viewLifecycleOwner.lifecycle,
            onBookmarkClick = viewModel::removeBookmark,
        )
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@BookmarkedVideosFragment.adapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.videos.collect { videos ->
                if (videos.isEmpty()) {
                    binding.recyclerView.isVisible = false
                    binding.stateView.showEmpty(messageRes = R.string.state_empty_bookmark_videos)
                } else {
                    binding.stateView.hide()
                    binding.recyclerView.isVisible = true
                    adapter.submitList(videos) {
                        adapter.submitBookmarkedIds(videos.map { it.id }.toSet())
                    }
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
