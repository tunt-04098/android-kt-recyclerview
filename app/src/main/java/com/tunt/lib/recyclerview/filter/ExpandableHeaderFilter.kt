package com.tunt.lib.recyclerview.filter

import com.tunt.lib.recyclerview.expandable.Group
import java.util.ArrayList

/**
 * Created by TuNT on 8/26/18.
 * tunt.program.04098@gmail.com
 */
class ExpandableHeaderFilter<Item>(var groups: List<Group>) : Filter<Item> {

    private var emptyItem: Item? = null

    override fun apply(items: List<Item>): List<Item?> {
        val result = ArrayList<Item?>()
        if (items == null || items.isEmpty()) return result
        var index = 0
        for (i in 0 until groups.size) {
            val group = groups[i]
            if (group.groupItemCount === 0) {
                result.add(emptyItem)
                continue
            }
            if (group.isExpand) {
                for (j in 0 until group.groupItemCount) {
                    result.add(items[(index + j)])
                }
            } else {
                result.add(items[index])
            }
            index += group.groupItemCount
        }
        return result
    }
}