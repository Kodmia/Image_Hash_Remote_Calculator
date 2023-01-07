package ru.dikoresearch.imagehashcalculator.presentation.imagepreview

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import ru.dikoresearch.imagehashcalculator.R
import ru.dikoresearch.imagehashcalculator.databinding.FragmentImagePreviewBinding
import ru.dikoresearch.imagehashcalculator.presentation.utils.Navigate
import ru.dikoresearch.imagehashcalculator.presentation.utils.ShowToast
import ru.dikoresearch.imagehashcalculator.presentation.utils.navigator
import ru.dikoresearch.imagehashcalculator.presentation.utils.viewModelFactory
import java.io.File
import java.io.FileOutputStream

class ImagePreviewFragment: Fragment(R.layout.fragment_image_preview) {

    private lateinit var binding: FragmentImagePreviewBinding
    private val viewModel: ImagePreviewViewModel by viewModels { viewModelFactory() }

    private val imageUri: String by lazy {
        arguments?.let {
            it.getString(IMAGE_URI, "") ?: ""
        } ?: ""
    }

    private val imageFromGallery: Boolean by lazy {
        arguments?.getBoolean(IMAGE_FROM_GALLERY) ?: false
    }

    private val bitmap: Bitmap by lazy {
        createBitmap(imageUri)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentImagePreviewBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Glide.with(binding.imagePreviewView.context)
            .load(imageUri)
            .into(binding.imagePreviewView)


        binding.proceedButton.setOnClickListener{
            val filePath: String = if (imageFromGallery){
                saveTempProvidedFile()
            }
            else {
                Uri.parse(imageUri).path!!
            }

            viewModel.processImage(bitmap, filePath)

        }

        viewModel.showProgressIndicator.observe(viewLifecycleOwner){
            binding.imageProcessingProgressIndicator.visibility = if (it) View.VISIBLE else View.GONE
            binding.proceedButton.isEnabled = !it
        }

        viewModel.actionEvent.observe(viewLifecycleOwner){ event ->
            when(event){
                is Navigate -> {
                    navigator().navigate(event.destination, event.bundle)
                }
                is ShowToast -> {
                    navigator().toast(event.msg)
                }
            }
        }
    }

    private fun saveTempProvidedFile(): String{
        val photoDirectory = File(
            requireActivity().applicationContext.filesDir,
            "images"
        )
        if (!photoDirectory.exists()){
            photoDirectory.mkdirs()
        }
        val imageFile = File(photoDirectory, imageUri.split("/").last())

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(imageFile))

        return imageFile.absolutePath
    }

    private fun createBitmap(uri: String): Bitmap{
        val source = ImageDecoder.createSource(requireActivity().contentResolver, Uri.parse(uri))
        return ImageDecoder.decodeBitmap(source)
    }

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle) =
            ImagePreviewFragment().apply {
                arguments = bundle
            }

        const val IMAGE_URI = "IMAGE_URI"
        const val IMAGE_FROM_GALLERY = "IMAGE_FROM_GALLERY"
    }
}