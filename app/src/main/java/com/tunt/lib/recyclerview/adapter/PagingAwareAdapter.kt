package com.tunt.lib.recyclerview.adapter

/**
 * Created by TuNT on 8/26/18.
 * tunt.program.04098@gmail.com
 */
interface PagingAwareAdapter<Item> : ItemAdapter<Item> {

    enum class PagingState {
        IDLE,
        LOADING,
        ERROR
    }

    fun isIdle(): Boolean
}