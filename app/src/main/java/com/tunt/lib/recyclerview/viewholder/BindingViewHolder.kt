package com.tunt.lib.recyclerview.viewholder

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.util.SparseArray
import android.view.View
import com.jakewharton.rxbinding2.view.RxView
import com.tunt.lib.recyclerview.BR
import com.tunt.lib.recyclerview.CollectionPosition
import com.tunt.lib.recyclerview.listener.OnItemClickListener
import com.tunt.lib.recyclerview.selector.Selector
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

/**
 * Created by TuNT on 8/23/18.
 * tunt.program.04098@gmail.com
 */
class BindingViewHolder<Item>(itemView: View, val selector: Selector) : SimpleViewHolder(itemView), Selector.OnSelectorChangeListener {

    private var binding: ViewDataBinding? = null

    init {
        // init selector
        selector.addOnSelectorChangeListener(this)
        RxView.clicks(itemView)
                .throttleFirst(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .subscribe { onClick() }
        binding = DataBindingUtil.bind(itemView)
    }

    private var item: Item? = null

    var onItemClickListener: OnItemClickListener<Item>? = null

    fun bind(item: Item, position: CollectionPosition, bindingMap: SparseArray<Object>) {
        this.item = item
        binding?.apply {
            this.setVariable(BR.item, item)
            this.setVariable(BR.itemPosition, position)
            for (i in 0 until bindingMap.size()) {
                this.setVariable(bindingMap.keyAt(i), bindingMap.valueAt(i))
            }
            this.executePendingBindings()
        }
        selector?.let {
            setSelected(it.isSelected(adapterPosition))
        }
    }

    fun getData(): Item? = item

    override fun onSelectorChanged() {
        setSelected(selector.isSelected(adapterPosition))
    }

    private fun onClick() {
        onItemClickListener?.onItemClick(itemView, getData(), adapterPosition)
        selector?.let {
            it.toggle(adapterPosition)
            setSelected(it.isSelected(adapterPosition))
        }
    }

    private fun setSelected(selected: Boolean) {
        itemView.isSelected = selected
    }
}