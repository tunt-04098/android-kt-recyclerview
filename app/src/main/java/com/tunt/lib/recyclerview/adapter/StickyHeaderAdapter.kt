package com.tunt.lib.recyclerview.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration
import com.tunt.lib.recyclerview.ViewModelUtils
import com.tunt.lib.recyclerview.viewholder.SimpleViewHolder

/**
 * Created by TuNT on 8/26/18.
 * tunt.program.04098@gmail.com
 */
abstract class StickyHeaderAdapter<Item>(hasHeader: Boolean = false,
                                         hasFooter: Boolean = false,
                                         allowDeselectItem: Boolean = false) : RecyclerAdapter<Item>(hasHeader, hasFooter, allowDeselectItem), StickyHeaderAwareAdapter<Item> {

    private lateinit var itemDecorator: StickyRecyclerHeadersDecoration

    override fun getHeaderItemDecoration(): StickyRecyclerHeadersDecoration {
        if (itemDecorator == null) {
            itemDecorator = StickyRecyclerHeadersDecoration(this)
            registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                override fun onChanged() {
                    itemDecorator.invalidateHeaders()
                    ViewModelUtils.resetStickyHeaderData(itemDecorator)
                }
            })
        }
        return itemDecorator
    }

    override fun getHeaderId(position: Int): Long = when {
        (hasHeader && position == 0) || (hasFooter && position == itemCount - 1) -> -1
        else -> {
            val realPos = position - (if (hasHeader) 1 else 0)
            getHeaderId(realPos, getItem(realPos))
        }
    }

    override fun onCreateHeaderViewHolder(parent: ViewGroup?): SimpleViewHolder<Item> = SimpleViewHolder(onCreateStickyHeaderView(LayoutInflater.from(parent?.context), parent))

    override fun onBindHeaderViewHolder(holder: SimpleViewHolder<Item>?, position: Int) {
        when {
            (hasHeader && position == 0) || (hasFooter && position == itemCount - 1) -> return
            else -> {
                val realPos = position - (if (hasHeader) 1 else 0)
                onBindHeaderView(holder?.itemView, realPos, getItem(realPos))
            }
        }
    }

    abstract fun getHeaderId(position: Int, item: Item): Long

    abstract fun onCreateStickyHeaderView(inflater: LayoutInflater, parent: ViewGroup?): View

    abstract fun onBindHeaderView(headerView: View?, position: Int, item: Item)
}