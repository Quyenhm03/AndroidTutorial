package com.eco.musicplayer.audioplayer.music.activityandfragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.eco.musicplayer.audioplayer.music.databinding.FragmentNameBinding

class NameFragment : Fragment() {

    interface OnNameListener {
        fun onNameEntered(name: String)
    }

    private var listener: OnNameListener? = null
    private var useCallback: Boolean = false
    private val nameViewModel: NameViewModel by activityViewModels()

    private lateinit var binding: FragmentNameBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        arguments?.let {
            useCallback = it.getBoolean("userCallback", false)
        }
        if (useCallback && context is OnNameListener) {
            listener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!useCallback) {
            nameViewModel.msgFromActivity.observe(viewLifecycleOwner) { msg ->
                binding.txtResult.text = "Fragment received: $msg"
            }
        }

        binding.btnSubmit.setOnClickListener {
            val name = binding.edtName.text.toString().trim()
            if (name.isNotEmpty()) {
                if (useCallback) {
                    listener?.onNameEntered(name)
                } else {
                    nameViewModel.setName(name)
                }
            }
        }
    }

    companion object {
        fun newInstance(useCallback: Boolean) : NameFragment {
            val fragment = NameFragment()
            fragment.arguments = Bundle().apply {
                putBoolean("useCallback", useCallback)
            }
            return fragment
        }
    }
}