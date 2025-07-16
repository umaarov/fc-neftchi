package uz.umarov.fcneftchi.ui.stadium

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import uz.umarov.fcneftchi.databinding.FragmentStadiumBinding

class StadiumFragment : Fragment() {

    private var _binding: FragmentStadiumBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStadiumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // binding.fabOpenMap.setOnClickListener { ... }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}