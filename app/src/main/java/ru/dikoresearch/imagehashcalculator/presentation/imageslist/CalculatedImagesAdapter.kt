package ru.dikoresearch.imagehashcalculator.presentation.imageslist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.dikoresearch.imagehashcalculator.R
import ru.dikoresearch.imagehashcalculator.databinding.CalculatedImageItemBinding
import ru.dikoresearch.imagehashcalculator.domain.entities.CalculatedImage

class CalculatedImagesAdapter(
    private val onImageSelectClicked: (CalculatedImage) -> Unit,
    private val onImageDeleteClicked: (CalculatedImage) -> Unit
): RecyclerView.Adapter<CalculatedImagesAdapter.CalculatedImageViewHolder>(), View.OnClickListener {

    var images: List<CalculatedImage> = emptyList()
        set(value){
            val diffUtilCallback = ImagesDiffUtilCallback(field, value)
            val diffResult = DiffUtil.calculateDiff(diffUtilCallback)
            field = value
            diffResult.dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalculatedImageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CalculatedImageItemBinding.inflate(inflater, parent, false)

        binding.root.setOnClickListener(this)
        binding.imageDeleteButton.setOnClickListener(this)

        return CalculatedImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CalculatedImageViewHolder, position: Int) {
        val image = images[position]
        holder.setContent(image)
    }

    override fun getItemCount(): Int = images.size

    override fun onClick(v: View) {
        val image = v.tag as CalculatedImage
        when(v.id){
            R.id.image_delete_button -> {
                onImageDeleteClicked(image)
            }
            else -> {
                onImageSelectClicked(image)
            }
        }
    }

    class CalculatedImageViewHolder(
        private val binding: CalculatedImageItemBinding
    ): RecyclerView.ViewHolder(binding.root){

        fun setContent(image: CalculatedImage){
            itemView.tag = image
            binding.imageDeleteButton.tag = image

            binding.calculatedHashView.text = image.imageHash
            Glide.with(binding.calculatedImageThumbnail.context)
                .load(image.imageUri)
                .circleCrop()
                .into(binding.calculatedImageThumbnail)
        }
    }
}