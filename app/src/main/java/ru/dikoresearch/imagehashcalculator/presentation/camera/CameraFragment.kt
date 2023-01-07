package ru.dikoresearch.imagehashcalculator.presentation.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import ru.dikoresearch.imagehashcalculator.R
import ru.dikoresearch.imagehashcalculator.databinding.CameraUiContainerBinding
import ru.dikoresearch.imagehashcalculator.databinding.FragmentCameraBinding
import ru.dikoresearch.imagehashcalculator.domain.camera.CameraXHelper
import ru.dikoresearch.imagehashcalculator.presentation.imagepreview.ImagePreviewFragment
import ru.dikoresearch.imagehashcalculator.presentation.utils.NavigationDestination
import ru.dikoresearch.imagehashcalculator.presentation.utils.navigator
import java.io.File


private val PERMISSIONS_REQUIRED = arrayOf(Manifest.permission.CAMERA)

class CameraFragment: Fragment(R.layout.fragment_camera) {

    private lateinit var fragmentBinding: FragmentCameraBinding
    private lateinit var cameraUiBinding: CameraUiContainerBinding

    private var cameraXHelper: CameraXHelper? = null

    private val permissionsRequestLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all {
                it.value
            }
            if (granted) {
                cameraXHelper?.start()
            }
            else {
                navigator().goBack()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentBinding = FragmentCameraBinding.inflate(inflater, container, false)
        cameraUiBinding = CameraUiContainerBinding.inflate(
            LayoutInflater.from(requireContext()),
            fragmentBinding.root,
            true
        )
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraXHelper = CameraXHelper(
            caller = this,
            previewView = fragmentBinding.cameraPreviewView,
            filesDirectory = File(
                requireActivity().applicationContext.filesDir,
                "images"
            ),
            onPictureTaken = {uri ->
                val bundle = bundleOf(ImagePreviewFragment.IMAGE_URI to uri!!.toString())
                navigator().navigate(NavigationDestination.IMAGE_PREVIEW_SCREEN, bundle)
            },
            onError = { error ->
                Log.e("","ERROR: ${error.message}")
                navigator().toast("Got error: ${error.message}")
            }
        )

        cameraUiBinding.cameraCaptureButton.setOnClickListener {
            cameraXHelper?.takePicture()
        }

        if (!hasPermissions(requireActivity().applicationContext)){
            permissionsRequestLauncher.launch(
                PERMISSIONS_REQUIRED
            )
        }
        else {
            cameraXHelper?.start()
        }
    }


    override fun onDestroyView() {
        cameraXHelper?.stop()
        super.onDestroyView()

    }

    companion object {
        fun hasPermissions(context: Context) = PERMISSIONS_REQUIRED.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }
}