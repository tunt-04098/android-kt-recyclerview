package com.tunt.lib.recyclerview

import android.util.SparseArray
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration
import java.lang.reflect.Field

/**
 * Created by TuNT on 8/26/18.
 * tunt.program.04098@gmail.com
 */
object ViewModelUtils {

    /**
     * Trick to reset the header rects in Sticky Header
     */
    fun resetStickyHeaderData(itemDecoration: StickyRecyclerHeadersDecoration) {
        var privateSparseArrayField: Field? = null
        try {
            privateSparseArrayField = StickyRecyclerHeadersDecoration::class.java.getDeclaredField("mHeaderRects")
            privateSparseArrayField!!.isAccessible = true
            val rects = privateSparseArrayField.get(itemDecoration) as SparseArray<*>
            rects.clear()
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }

    }
}