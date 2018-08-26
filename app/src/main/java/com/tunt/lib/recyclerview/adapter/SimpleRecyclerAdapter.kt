package com.tunt.lib.recyclerview.adapter

import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by TuNT on 8/26/18.
 * tunt.program.04098@gmail.com
 */
class SimpleRecyclerAdapter<Item>(@LayoutRes val itemLayout: Int,
                                  hasHeader: Boolean = false,
                                  hasFooter: Boolean = false,
                                  allowDeselectItem: Boolean = false) : RecyclerAdapter<Item>(hasHeader, hasFooter, allowDeselectItem) {

    override fun onCreateItemView(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): View = inflater.inflate(itemLayout, parent, false)
}