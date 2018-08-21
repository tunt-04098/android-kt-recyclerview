package com.tunt.lib.recyclerview.selector

/**
 * Created by TuNT on 8/21/18.
 * tunt.program.04098@gmail.com
 */
class SingleSelector(private val isAllowDeselectItem: Boolean = false) : Selector() {

    override fun setSelectedInternal(position: Int, isSelected: Boolean) {
        if (isSelected) {
            selected.clear()
            selected.put(position, isSelected)
        } else if (isAllowDeselectItem) {
            selected.put(position, isSelected)
        }
    }
}