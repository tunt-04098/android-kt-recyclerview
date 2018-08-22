package com.tunt.lib.recyclerview.listener

import android.view.View

/**
 * Created by TuNT on 8/22/18.
 * tunt.program.04098@gmail.com
 */
interface OnItemClickListener<T> {

    fun onItemClick(itemView: View, data: T?, position: Int)
}