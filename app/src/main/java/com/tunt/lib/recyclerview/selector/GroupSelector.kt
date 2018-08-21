package com.tunt.lib.recyclerview.selector

import android.util.SparseArray
import com.tunt.lib.recyclerview.expandable.Group

/**
 * Created by TuNT on 8/21/18.
 * tunt.program.04098@gmail.com
 */
class GroupSelector(groups: ArrayList<Group>) : Selector() {

    private var groups = ArrayList<Group>()

    private val groupSelected = SparseArray<Selector>()

    init {
        setGroups(groups)
    }

    fun setGroups(groups: ArrayList<Group>) {
        this.groups = groups
        // initialize selector for each group
        if (groups == null || groups.isEmpty()) return
        for (position in groups.size - 1 downTo 0) {
            val group = groups[position]
            val selector = if (group.multiSelector) MultiSelector() else SingleSelector()
            groupSelected.put(position, selector)
        }
    }

    fun setItemSelected(groupIndex: Int, itemRealPosition: Int, selected: Boolean) {
        groupSelected.get(groupIndex).setSelected(itemRealPosition, selected)
    }

    fun setItemInGroupSelected(groupIndex: Int, itemInGroupPosition: Int, selected: Boolean) {
        var itemRealPosition = itemInGroupPosition
        for (position in 0 until groupIndex) {
            itemRealPosition += groups.get(position).groupItemCount
        }
        setItemSelected(groupIndex, itemRealPosition, selected)
        notifySelectorChange()
    }

    fun clearGroupSelected(groupIndex: Int) {
        groupSelected.get(groupIndex).clearSelected()
        notifySelectorChange()
    }

    fun setGroupSelected(vararg groupSelected: Int) {
        if (groups == null) {
            throw IllegalStateException("Must setup groups first")
        }
        for (groupIndex in 0 until groupSelected.size) {
            setItemSelected(groupIndex, groupSelected[groupIndex], true)
        }
        notifySelectorChange()
    }

    fun setGroupSelected(vararg groupSelected: List<Int>) {
        for (groupIndex in 0 until groupSelected.size) {
            val selected = groupSelected[groupIndex]
            if (selected == null || selected.isEmpty()) continue
            for (itemInGroupPosition in selected) {
                setItemSelected(groupIndex, itemInGroupPosition, true)
            }
        }
        notifySelectorChange()
    }

    override fun setSelectedInternal(position: Int, isSelected: Boolean) {
    }
}