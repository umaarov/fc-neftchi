package uz.umarov.fcneftchi.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import uz.umarov.fcneftchi.R
import uz.umarov.fcneftchi.databinding.FragmentClubHistoryBinding
import uz.umarov.fcneftchi.ui.MainActivity

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

        val historyAdapter = ClubHistoryAdapter(getHistoryData())
        binding.historyRecyclerView.adapter = historyAdapter
    }

    private fun getHistoryData(): List<HistoryListItem> {
        return listOf(
            HistoryListItem.Header("Neftchi'ning shonli tarixi"),
            HistoryListItem.SubHeader("Dastlabki sovet davri"),
            HistoryListItem.Paragraph(
                "\"Neftchi\" futbol klubiga <b>1962-yilda</b> Farg'ona shahrida \"Neftyanik\" nomi bilan asos solingan. 1962-yildan 1991-yilgacha jamoa Sovet Ittifoqining Ikkinchi ligasida (O'rta Osiyo divizioni) ishtirok etgan. 1990-yilda klub Ikkinchi liganing \"Sharq\" konferensiyasida g'olib chiqib, Sovet Birinchi ligasiga yo'l oladi. 1991-yilgi mavsumda Birinchi ligada 7-o'rinni egallashi klubning sovet futboli tarixidagi eng yuqori yutug'i bo'lib qoldi."
            ),
            HistoryListItem.SubHeader("Mustaqillik davri (Oltin davr)"),
            HistoryListItem.Paragraph(
                "<b>1992-yildan</b> boshlab klub O'zbekiston Oliy Ligasida ishtirok etib kelmoqda. \"Paxtakor\" va \"Navbahor\" bilan bir qatorda, \"Neftchi\" Oliy Liganing barcha mavsumlarida tanaffussiz qatnashgan uch klubdan biridir. 1992-yildan 2001-yilgacha bo'lgan davr klub tarixidagi \"oltin davr\" hisoblanadi. Yuriy Sarkisyan boshchiligidagi jamoa bu yillarda mamlakatning eng kuchli klubiga aylanib, ketma-ket chempionliklarni qo'lga kiritdi."
            ),
            HistoryListItem.Header("Asosiy sovrinlar"),
//            HistoryListItem.HistoryImage(R.drawable.img_trophies),
            HistoryListItem.Trophy("O'zbekiston Superligasi Chempioni (5 marta): 1992, 1993, 1994, 1995, 2001"),
            HistoryListItem.Trophy("O'zbekiston Kubogi sohibi (2 marta): 1994, 1996"),
            HistoryListItem.Trophy("MDH Hamdo'stlik Kubogi finalchisi: 1994"),
            HistoryListItem.Header("Stadion"),
            HistoryListItem.Paragraph(
                "\"Neftchi\" dastlab o'z uy uchrashuvlarini 1932-yilda qurilgan \"Farg'ona\" stadionida o'tkazgan. 2012-yilda yangi arena qurilishi boshlandi va 2015-yilda <b>20,000</b> tomoshabinga mo'ljallangan zamonaviy \"Istiqlol\" stadioni ochildi. Stadionning ochilish o'yini 2015-yil 3-aprel kuni O'zbekiston U-20 va Yangi Zelandiya U-20 jamoalari o'rtasida bo'lib o'tgan."
            ),
            HistoryListItem.Header("Raqobatlar"),
            HistoryListItem.SubHeader("O'zbek Klassikosi"),
            HistoryListItem.Paragraph(
                "O'zbek futbolida an'anaviy raqobat \"Neftchi\" va poytaxtning \"Paxtakor\" klubi o'rtasida kechadi. Bu qarama-qarshilik Ispaniyadagi \"El Klasiko\"ga qiyosan \"O'zbek Klassikosi\" nomini olgan."
            ),
            HistoryListItem.SubHeader("Vodiy derbisi"),
            HistoryListItem.Paragraph(
                "\"Neftchi\"ning mahalliy va eng ashaddiy raqiblaridan biri Namanganning \"Navbahor\" klubidir. Shuningdek, \"Andijon\" klubi bilan o'yinlar ham \"Vodiy derbisi\" maqomiga ega bo'lib, muxlislar uchun doimo katta qiziqish uyg'otadi."
            )
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