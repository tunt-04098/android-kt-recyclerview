package com.tunt.lib.recyclerview.selector

/**
 * Created by TuNT on 8/21/18.
 * tunt.program.04098@gmail.com
 */
class MultiSelector : Selector() {

    override fun setSelectedInternal(position: Int, isSelected: Boolean) {
        selected.put(position, isSelected)
    }
}