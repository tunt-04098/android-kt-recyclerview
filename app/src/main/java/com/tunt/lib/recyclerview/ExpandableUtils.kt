package com.tunt.lib.recyclerview

import com.tunt.lib.recyclerview.expandable.Group

/**
 * Created by TuNT on 8/26/18.
 * tunt.program.04098@gmail.com
 */
object ExpandableUtils {

    private const val NUMBER_ITEM_WHEN_COLLAPSING = 1

    fun findGroupIndexOfItem(groups: List<Group>, position: Int): Int {
        var startItem = 0
        var endItem = 0
        for (i in groups.indices) {
            val group = groups[i]
            val groupItemCount = if (group.isExpand) group.groupItemCount else NUMBER_ITEM_WHEN_COLLAPSING
            endItem = startItem + groupItemCount
            if (position >= startItem && position < endItem) {
                return i
            }
            startItem = endItem
        }
        return groups.size - 1
    }

    fun findRealPositionOfItem(groups: List<Group>, position: Int): Int {
        val groupIndex = findGroupIndexOfItem(groups, position)
        var realPosition = 0
        var currentPosition = 0
        for (i in 0 until groupIndex) {
            val group = groups[i]
            val groupItemCount = if (group.isExpand) group.groupItemCount else NUMBER_ITEM_WHEN_COLLAPSING
            currentPosition += groupItemCount
            realPosition += group.groupItemCount
        }
        return realPosition + position - currentPosition
    }

    fun findCurrentPositionFromRealPosition(groups: List<Group>, realPosition: Int): Int {
        var start = 0
        var current = 0
        for (i in groups.indices) {
            val group = groups[i]
            val end = start + group.groupItemCount
            if (end > realPosition) {
                return if (group.isExpand) {
                    current + realPosition - start
                } else -1
            }
            start = end
            current += if (group.isExpand) group.groupItemCount else 1
        }
        return 0
    }

    fun indexOfGroup(groups: List<Group>, groupIndex: Int): IntArray {
        val result = IntArray(2)
        for (i in 0 until groupIndex) {
            val group = groups[i]
            result[0] += if (group.isExpand) group.groupItemCount else 1
        }
        val group = groups[groupIndex]
        result[1] = (if (group.isExpand) group.groupItemCount else 1) + result[0]
        return result
    }
}