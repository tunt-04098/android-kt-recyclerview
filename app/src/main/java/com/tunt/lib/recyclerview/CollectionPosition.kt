package com.tunt.lib.recyclerview

/**
 * Created by TuNT on 8/23/18.
 * tunt.program.04098@gmail.com
 */
data class CollectionPosition(var size: Int,
                              var position: Int) {

    fun isFirst(): Boolean {
        return position == 0
    }

    fun isLast(): Boolean {
        return position == size - 1
    }

    fun isMiddle(): Boolean {
        return !isFirst() && !isLast()
    }
}