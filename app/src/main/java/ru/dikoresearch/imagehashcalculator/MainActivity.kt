package ru.dikoresearch.imagehashcalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import ru.dikoresearch.imagehashcalculator.presentation.camera.CameraFragment
import ru.dikoresearch.imagehashcalculator.presentation.imagedetails.ImageDetailsFragment
import ru.dikoresearch.imagehashcalculator.presentation.imagepreview.ImagePreviewFragment
import ru.dikoresearch.imagehashcalculator.presentation.imageslist.ListOfImagesFragment
import ru.dikoresearch.imagehashcalculator.presentation.utils.NavigationDestination

interface Navigator {
    fun navigate(destination: String, bundle: Bundle?)
    fun goBack()
    fun toast(message: String)
}


class MainActivity : AppCompatActivity(), Navigator {

    private val actions = mutableListOf<() -> Unit>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null){
            supportFragmentManager.commit {
                replace(R.id.fragment_container_view, ListOfImagesFragment())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        actions.forEach { it() }
        actions.clear()
    }

    override fun navigate(destination: String, bundle: Bundle?) {

        when(destination){
            NavigationDestination.IMAGE_PREVIEW_SCREEN -> {
                supportFragmentManager.commit {
                    setCustomAnimations(
                        R.anim.slide_in,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.slide_out
                    )
                    replace(R.id.fragment_container_view, ImagePreviewFragment.newInstance(bundle!!))
                    addToBackStack(null)
                }
            }
            NavigationDestination.IMAGE_DETAILS_SCREEN -> {
                clearBackStack()
                supportFragmentManager.commit {
                    setCustomAnimations(
                        R.anim.slide_in,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.slide_out
                    )
                    replace(R.id.fragment_container_view, ImageDetailsFragment.newInstance(bundle!!))
                    addToBackStack(null)
                }
            }
            NavigationDestination.CAMERA_SCREEN -> {
                supportFragmentManager.commit {
                    setCustomAnimations(
                        R.anim.fade_in,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.fade_out
                    )
                    replace(R.id.fragment_container_view, CameraFragment())
                    addToBackStack(null)
                }
            }
            else -> {

            }
        }
    }

    override fun goBack() {
        runWhenActive {
            onBackPressed()
        }
    }

    override fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun runWhenActive(action: () -> Unit){
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)){
            action()
        }
        else {
            actions += action
        }
    }

    private fun clearBackStack(){
        var backStackSize = supportFragmentManager.backStackEntryCount
        while(backStackSize > 0){
            supportFragmentManager.popBackStack()
            backStackSize--
        }
    }
}