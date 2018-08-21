package com.tunt.lib.recyclerview.expandable

/**
 * Created by TuNT on 8/21/18.
 * tunt.program.04098@gmail.com
 */
data class Group(
        var isExpand: Boolean,
        var groupItemCount: Int,
        var multiSelector: Boolean = false,
        var allowUnCheck: Boolean = false
)