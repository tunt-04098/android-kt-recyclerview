package com.tunt.lib.recyclerview.viewholder

import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.View
import com.jakewharton.rxbinding2.view.RxView
import com.tunt.lib.recyclerview.BR
import com.tunt.lib.recyclerview.CollectionPosition
import com.tunt.lib.recyclerview.listener.OnItemClickListener
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

/**
 * Created by TuNT on 8/23/18.
 * tunt.program.04098@gmail.com
 */
open class SimpleViewHolder<Item>(itemView: View) : RecyclerView.ViewHolder(itemView) {

    init {
        RxView.clicks(itemView)
                .throttleFirst(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .subscribe { onClick() }
    }

    protected var item: Item? = null

    var onItemClickListener: OnItemClickListener<Item>? = null

    inline fun <reified V : View> getItemView(): V {
        if (this.itemView is V) {
            return this.itemView
        } else {
            throw IllegalArgumentException("Cannot cast itemView to your wanted View")
        }
    }

    open fun bind(item: Item) {
        this.item = item
    }

    fun getData(): Item? = item

    open fun onClick() {
        onItemClickListener?.onItemClick(itemView, getData(), adapterPosition)
    }
}