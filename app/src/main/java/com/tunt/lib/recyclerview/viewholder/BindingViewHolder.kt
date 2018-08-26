package com.tunt.lib.recyclerview.viewholder

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.util.SparseArray
import android.view.View
import com.tunt.lib.recyclerview.BR
import com.tunt.lib.recyclerview.CollectionPosition
import com.tunt.lib.recyclerview.selector.Selector

/**
 * Created by TuNT on 8/23/18.
 * tunt.program.04098@gmail.com
 */
class BindingViewHolder<Item>(itemView: View, val selector: Selector) : SimpleViewHolder<Item>(itemView), Selector.OnSelectorChangeListener {

    private var binding: ViewDataBinding? = null

    init {
        // init selector
        selector.addOnSelectorChangeListener(this)
        binding = DataBindingUtil.bind(itemView)
    }

    fun bind(item: Item, position: CollectionPosition, bindingMap: SparseArray<Any>) {
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

    override fun onSelectorChanged() {
        setSelected(selector.isSelected(adapterPosition))
    }

    override fun onClick() {
        super.onClick()
        selector?.let {
            it.toggle(adapterPosition)
            setSelected(it.isSelected(adapterPosition))
        }
    }

    private fun setSelected(selected: Boolean) {
        itemView.isSelected = selected
    }
}