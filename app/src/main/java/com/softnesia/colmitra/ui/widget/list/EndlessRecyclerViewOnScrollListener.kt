package com.softnesia.colmitra.ui.widget.list

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/*
 * https://github.com/JoaquimLey/avenging/blob/development/mobile/src/main/java/com/joaquimley/avenging/ui/list/EndlessRecyclerViewOnScrollListener.java

 * Copyright (c) Joaquim Ley 2016. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ abstract class EndlessRecyclerViewOnScrollListener : RecyclerView.OnScrollListener {
    private var mCurrentPage = 0
    private var mPreviousTotalItemCount = 0
    private var mLoading = true
    private var mLayoutManager: RecyclerView.LayoutManager

    constructor(layoutManager: RecyclerView.LayoutManager) {
        mLayoutManager = layoutManager

        if (layoutManager is GridLayoutManager) {
            sVisibleThreshold *= layoutManager.spanCount
        } else if (layoutManager is StaggeredGridLayoutManager) {
            sVisibleThreshold *= layoutManager.spanCount
        }
    }

    constructor(layoutManager: LinearLayoutManager) {
        mLayoutManager = layoutManager
    }

    constructor(layoutManager: GridLayoutManager) {
        mLayoutManager = layoutManager
        sVisibleThreshold *= layoutManager.spanCount
    }

    constructor(layoutManager: StaggeredGridLayoutManager) {
        mLayoutManager = layoutManager
        sVisibleThreshold *= layoutManager.spanCount
    }

    fun getLastVisibleItem(lastVisibleItemPositions: IntArray): Int {
        var maxSize = 0

        for (i in lastVisibleItemPositions.indices) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i]
            } else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i]
            }
        }

        return maxSize
    }

    override fun onScrolled(view: RecyclerView, dx: Int, dy: Int) {
        var lastVisibleItemPosition = 0
        val totalItemCount = mLayoutManager.itemCount

        if (mLayoutManager is StaggeredGridLayoutManager) {
            val lastVisibleItemPositions =
                (mLayoutManager as StaggeredGridLayoutManager).findLastVisibleItemPositions(null)
            lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions)
        } else if (mLayoutManager is GridLayoutManager) {
            lastVisibleItemPosition =
                (mLayoutManager as GridLayoutManager).findLastVisibleItemPosition()
        } else if (mLayoutManager is LinearLayoutManager) {
            lastVisibleItemPosition =
                (mLayoutManager as LinearLayoutManager).findLastVisibleItemPosition()
        }
        if (totalItemCount < mPreviousTotalItemCount) { // List was cleared
            mCurrentPage = STARTING_PAGE_INDEX
            mPreviousTotalItemCount = 0
            if (totalItemCount == 0) {
                mLoading = true
            }
        }
        /**
         * If it’s still loading, we check to see if the DataSet count has
         * changed, if so we conclude it has finished loading and update the current page
         * number and total item count (+ 1 due to loading view being added).
         */
        if (mLoading && totalItemCount > mPreviousTotalItemCount + 1) {
            mLoading = false
            mPreviousTotalItemCount = totalItemCount
        }
        /**
         * If it isn’t currently loading, we check to see if we have breached
         * + the visibleThreshold and need to reload more data.
         */
        if (!mLoading && lastVisibleItemPosition + sVisibleThreshold > totalItemCount) {
            mCurrentPage++
            onLoadMore(mCurrentPage, totalItemCount)
            mLoading = true
        }
    }

    abstract fun onLoadMore(page: Int, totalItemsCount: Int)

    companion object {
        private const val STARTING_PAGE_INDEX = 0

        /**
         * Low threshold to show the onLoad()/Spinner functionality.
         * If you are going to use this for a production app set a higher value
         * for better UX
         */
        private var sVisibleThreshold = 2
    }
}