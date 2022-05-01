package com.wanotube.wanotubeapp.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class MarginItemDecoration(
    private val leftSpaceSize: Int = 0,
    private val rightSpaceSize: Int = 0,
    private val topSpaceSize: Int = 0,
    private val bottomSpaceSize: Int = 0,
    ) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        with(outRect) {
            if (parent.getChildAdapterPosition(view) == 0) {
                top = topSpaceSize
            }
            left = leftSpaceSize
            right = rightSpaceSize
            bottom = bottomSpaceSize
        }
    }
}