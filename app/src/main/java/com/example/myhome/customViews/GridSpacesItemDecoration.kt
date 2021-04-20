package com.example.myhome.customViews

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridSpacesItemDecoration(private val space: Int, private val spanCount: Int) :
    RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val pos = parent.getChildLayoutPosition(view)
        if (pos >= spanCount) {
            outRect.top = space
        }
        if(pos%spanCount!=spanCount-1){
            outRect.right = space
        }
    }
}