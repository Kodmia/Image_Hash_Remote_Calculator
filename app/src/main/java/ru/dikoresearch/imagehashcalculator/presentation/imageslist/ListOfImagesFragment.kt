package ru.dikoresearch.imagehashcalculator.presentation.imageslist

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ru.dikoresearch.imagehashcalculator.R
import ru.dikoresearch.imagehashcalculator.databinding.FragmentImagesListBinding
import ru.dikoresearch.imagehashcalculator.presentation.imagepreview.ImagePreviewFragment
import ru.dikoresearch.imagehashcalculator.presentation.utils.*

class ListOfImagesFragment: Fragment(R.layout.fragment_images_list) {

    private lateinit var binding: FragmentImagesListBinding

    private val adapter by lazy {
        CalculatedImagesAdapter(
            onImageSelectClicked = { image ->
                viewModel.updateCounterAndNavigate(image)
            },
            onImageDeleteClicked = { image ->
                Log.e("", "Image delete clicked")
                viewModel.deleteImageFromRepository(image)
            }
        )
    }

    private val viewModel: ListOfImagesViewModel by viewModels { viewModelFactory() }

    private val activityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK){
            val data = result.data

            if (data != null && data.data != null){
                val selectedImageUri: Uri = data.data!!

                val bundle = Bundle()
                bundle.putString(ImagePreviewFragment.IMAGE_URI, selectedImageUri.toString())
                bundle.putBoolean(ImagePreviewFragment.IMAGE_FROM_GALLERY, true)

                navigator().navigate(NavigationDestination.IMAGE_PREVIEW_SCREEN, bundle)
            }
        }
        else {
            Log.e("", "Error on getting image ${result.resultCode}")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentImagesListBinding.inflate(inflater, container, false)

        binding.addImageButton.setOnClickListener{
            val popupMenu = preparePopupMenu(it)
            popupMenu.show()
        }

        val recyclerViewLayoutManager = LinearLayoutManager(requireContext())
        binding.calculatedImagesRecyclerView.layoutManager = recyclerViewLayoutManager
        binding.calculatedImagesRecyclerView.adapter = adapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.actionEvent.observe(viewLifecycleOwner){event ->
            when(event){
                is Navigate -> {
                    navigator().navigate(event.destination, event.bundle)
                }
                is ShowToast -> {
                    Toast.makeText(requireActivity(), event.msg, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.showProgressIndicator.observe(viewLifecycleOwner){
            binding.recyclerViewProgressIndicator.visibility = if(it) View.VISIBLE else View.GONE
        }

        viewModel.imagesList.observe(viewLifecycleOwner){
            adapter.images = it
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.getAllImages()
    }

    private fun chooseImage(){
        val i = Intent()
        i.type = "image/*"
        i.action = Intent.ACTION_OPEN_DOCUMENT
        activityLauncher.launch(i)
    }

    private fun preparePopupMenu(view: View): PopupMenu{
        val popupMenu = PopupMenu(view.context, view)

        popupMenu.menu.add(0, 0, Menu.NONE, "Local")
        popupMenu.menu.add(0, 1, Menu.NONE, "Camera")
        popupMenu.setOnMenuItemClickListener {
            when(it.itemId){
                0 -> {
                    //viewModel.sendBase64()
                    chooseImage()
                }
                else -> {
                    navigator().navigate(NavigationDestination.CAMERA_SCREEN, null)
                }
            }
            return@setOnMenuItemClickListener true
        }

        return popupMenu
    }
}