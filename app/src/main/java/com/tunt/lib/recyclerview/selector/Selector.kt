package com.tunt.lib.recyclerview.selector

import android.util.SparseBooleanArray

/**
 * Created by TuNT on 8/21/18.
 * tunt.program.04098@gmail.com
 */
abstract class Selector {

    val selected = SparseBooleanArray()

    private val changeListener = ArrayList<OnSelectorChangeListener>()

    fun setSelected(position: Int, isSelected: Boolean) {
        setSelectedInternal(position, isSelected)
        notifySelectorChange()
    }

    fun toggle(position: Int) = setSelected(position, !isSelected(position))

    open fun getSelected(): List<Int> {
        val selectedList = ArrayList<Int>()
        for (position in 0 until selected.size()) {
            if (selected.valueAt(position)) {
                selectedList.add(selected.keyAt(position))
            }
        }
        return selectedList
    }

    open fun isSelected(position: Int): Boolean = selected.get(position, false)

    fun addOnSelectorChangeListener(listener: OnSelectorChangeListener) {
        if (!changeListener.contains(listener)) {
            changeListener.add(listener)
        }
    }

    fun removeOnSelectorChangeListener(listener: OnSelectorChangeListener) {
        changeListener.remove(listener)
    }

    fun clearSelected() {
        selected.clear()
        notifySelectorChange()
    }

    fun notifySelectorChange() {
        changeListener.forEach {
            it?.onSelectorChanged()
        }
    }

    abstract fun setSelectedInternal(position: Int, isSelected: Boolean)

    interface OnSelectorChangeListener {
        fun onSelectorChanged()
    }
}