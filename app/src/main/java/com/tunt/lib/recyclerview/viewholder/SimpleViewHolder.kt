package com.tunt.lib.recyclerview.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by TuNT on 8/23/18.
 * tunt.program.04098@gmail.com
 */
abstract class SimpleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    inline fun <reified V : View> getItemView(): V {
        if (this.itemView is V) {
            return this.itemView
        } else {
            throw IllegalArgumentException("Cannot cast itemView to your wanted View")
        }
    }
}