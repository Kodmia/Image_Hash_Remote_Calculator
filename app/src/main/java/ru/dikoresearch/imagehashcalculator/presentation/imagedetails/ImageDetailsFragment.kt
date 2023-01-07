package ru.dikoresearch.imagehashcalculator.presentation.imagedetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import ru.dikoresearch.imagehashcalculator.R
import ru.dikoresearch.imagehashcalculator.databinding.FragmentImageDetailsBinding
import ru.dikoresearch.imagehashcalculator.presentation.utils.Navigate
import ru.dikoresearch.imagehashcalculator.presentation.utils.ShowToast
import ru.dikoresearch.imagehashcalculator.presentation.utils.navigator
import ru.dikoresearch.imagehashcalculator.presentation.utils.viewModelFactory

class ImageDetailsFragment: Fragment(R.layout.fragment_image_details) {

    private lateinit var binding: FragmentImageDetailsBinding

    private val viewModel: ImageDetailsViewModel by viewModels { viewModelFactory() }

    private val imageId: Int by lazy {
        arguments?.getInt(IMAGE_ID) ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentImageDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.calculatedImage.observe(viewLifecycleOwner){image ->
            Glide.with(binding.imageDetailsView.context)
                .load(image.imageUri)
                .into(binding.imageDetailsView)

            binding.imageHashView.text = image.imageHash

            binding.numOfClicksView.text = "Clicked ${image.counter} times"
        }

        viewModel.actionEvent.observe(viewLifecycleOwner){event ->
            when(event){
                is Navigate -> {

                }
                is ShowToast -> {
                    navigator().toast(event.msg)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadImageInfo(imageId)
    }

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle) =
            ImageDetailsFragment().apply {
                arguments = bundle
            }
        const val IMAGE_ID = "IMAGE_ID"
    }

}