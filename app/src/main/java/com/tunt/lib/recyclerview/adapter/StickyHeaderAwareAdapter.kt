package com.tunt.lib.recyclerview.adapter

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration
import com.tunt.lib.recyclerview.viewholder.SimpleViewHolder

/**
 * Created by TuNT on 8/26/18.
 * tunt.program.04098@gmail.com
 */
interface StickyHeaderAwareAdapter<Item> : ItemAdapter<Item>, StickyRecyclerHeadersAdapter<SimpleViewHolder<Item>> {

    fun getHeaderItemDecoration(): StickyRecyclerHeadersDecoration
}