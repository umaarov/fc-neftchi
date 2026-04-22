package uz.umarov.fcneftchi.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import uz.umarov.fcneftchi.R
import uz.umarov.fcneftchi.databinding.FragmentClubHistoryBinding
import uz.umarov.fcneftchi.ui.MainActivity
import uz.umarov.fcneftchi.util.applySystemBarPadding

class ClubHistoryFragment : Fragment() {

    private var _binding: FragmentClubHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClubHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbarLayout.root.applySystemBarPadding(top = true)
        setupToolbar()
        val historyAdapter = ClubHistoryAdapter(getHistoryData())
        binding.historyRecyclerView.adapter = historyAdapter
    }

    private fun setupToolbar() {
        binding.toolbarLayout.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }


    private fun getHistoryData(): List<HistoryListItem> {
        return listOf(
            HistoryListItem.Header(R.string.history_section_overview),
            HistoryListItem.SubHeader(R.string.history_subsection_soviet_era),
            HistoryListItem.Paragraph(R.string.history_paragraph_soviet_era),
            HistoryListItem.SubHeader(R.string.history_subsection_independence_era),
            HistoryListItem.Paragraph(R.string.history_paragraph_independence_era),
            HistoryListItem.Header(R.string.history_section_trophies),
            HistoryListItem.Trophy(R.string.history_trophy_super_league),
            HistoryListItem.Trophy(R.string.history_trophy_cup),
            HistoryListItem.Trophy(R.string.history_trophy_cis_cup),
            HistoryListItem.Header(R.string.history_section_stadium),
            HistoryListItem.Paragraph(R.string.history_paragraph_stadium),
            HistoryListItem.Header(R.string.history_section_rivalries),
            HistoryListItem.SubHeader(R.string.history_subsection_classico),
            HistoryListItem.Paragraph(R.string.history_paragraph_classico),
            HistoryListItem.SubHeader(R.string.history_subsection_valley_derby),
            HistoryListItem.Paragraph(R.string.history_paragraph_valley_derby)
        )
    }


    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.hideMainUI()
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