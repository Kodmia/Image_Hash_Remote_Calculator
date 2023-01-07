package ru.dikoresearch.imagehashcalculator.presentation.imageslist

import androidx.recyclerview.widget.DiffUtil
import ru.dikoresearch.imagehashcalculator.domain.entities.CalculatedImage

class ImagesDiffUtilCallback(
    val oldList: List<CalculatedImage>,
    val newList: List<CalculatedImage>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldImage = oldList[oldItemPosition]
        val newImage = newList[newItemPosition]

        return oldImage.uid == newImage.uid
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldImage = oldList[oldItemPosition]
        val newImage = newList[newItemPosition]

        return oldImage == newImage
    }
}