package com.tunt.lib.recyclerview.adapter

import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tunt.lib.recyclerview.expandable.ExpandableUtils
import com.tunt.lib.recyclerview.expandable.Group
import com.tunt.lib.recyclerview.filter.ExpandableHeaderFilter
import com.tunt.lib.recyclerview.selector.GroupSelector

/**
 * Created by TuNT on 8/26/18.
 * tunt.program.04098@gmail.com
 */
abstract class ExpandableAdapter<Item>(sections: Array<List<Item>>?,
                                       isMultiSelectors: BooleanArray? = null,
                                       hasHeader: Boolean = false,
                                       hasFooter: Boolean = false,
                                       allowDeselectItem: Boolean = false) : StickyHeaderAdapter<Item>(hasHeader, hasFooter, allowDeselectItem) {

    companion object {
        private const val FILTER_EXPANDABLE_INDEX = 0xf117e12
    }

    private var groups = ArrayList<Group>()

    init {
        if (sections != null && !sections.isEmpty()) {
            val multiSelectors: BooleanArray = isMultiSelectors ?: BooleanArray(sections.size)
            calculateGroup(sections, multiSelectors)
        }
    }

    fun setGroupSelected(groupSelected: IntArray) {
        (selector as GroupSelector).setGroupSelected(groupSelected)
    }

    fun setGroupSelected(groupSelected: Array<List<Int>>?) {
        if (groupSelected == null) return
        selector?.let {
            val result = arrayOfNulls<List<Int>>(groupSelected.size)
            result[0] = groupSelected[0]

            if (groupSelected.size > 1) {
                for (i in 1 until groupSelected.size) {
                    var numberItem = 0
                    for (j in 0 until i) {
                        numberItem += groups[j].groupItemCount
                    }
                    val indexList = ArrayList<Int>()
                    for (index in groupSelected[i]) {
                        if (index < 0) continue
                        indexList.add(index + numberItem)
                    }
                    result[i] = indexList
                }
            }
            (selector as GroupSelector).setGroupSelected(result)
        }
    }

    protected abstract fun onBindGroupView(groupView: View, position: Int, item: Item, groupIndex: Int, expanded: Boolean)

    protected abstract fun onCreateGroupView(inflater: LayoutInflater, parent: ViewGroup): View

    private fun calculateGroup(sections: Array<List<Item>>, isMultiSelectors: BooleanArray) {
        // Calculate groups
        for (position in 0 until sections.size) {
            val group = Group(true, sections[position].size, isMultiSelectors[position], false)
            groups.add(group)
        }
        selector = GroupSelector(groups)
        addFilter(FILTER_EXPANDABLE_INDEX, ExpandableHeaderFilter(groups))
    }

    private fun findGroupIndexOfItem(position: Int): Int = ExpandableUtils.findGroupIndexOfItem(groups, position)
}