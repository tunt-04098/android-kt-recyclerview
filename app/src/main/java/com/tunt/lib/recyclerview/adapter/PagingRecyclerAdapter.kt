package com.tunt.lib.recyclerview.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tunt.lib.recyclerview.R
import com.tunt.lib.recyclerview.listener.OnNextPageListener
import com.tunt.lib.recyclerview.viewholder.SimpleViewHolder
import kotlinx.android.synthetic.main.layout_paging_error.view.*

/**
 * Created by TuNT on 8/26/18.
 * tunt.program.04098@gmail.com
 */
abstract class PagingRecyclerAdapter<Item>(hasHeader: Boolean = false,
                                           hasFooter: Boolean = false,
                                           allowDeselectItem: Boolean = false) : RecyclerAdapter<Item>(hasHeader, hasFooter, allowDeselectItem), PagingAwareAdapter<Item> {

    companion object {
        const val TYPE_LOADING = 2
        const val TYPE_ERROR = 3
        const val TYPE_ITEM = 4
        const val TYPE_MAX = TYPE_ITEM
    }

    private var pagingState = PagingAwareAdapter.PagingState.LOADING
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    /**
     * Retry button click listener
     */
    private var onErrorRetryClickListener: View.OnClickListener? = null

    /**
     * On next page listener
     */
    private var onNextPageListener: OnNextPageListener? = null

    override fun isIdle(): Boolean = pagingState == PagingAwareAdapter.PagingState.IDLE

    override fun onCreateItemView(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): View = when(viewType) {
        TYPE_LOADING -> onCreatePagingLoadingView(inflater, parent)
        TYPE_ERROR -> onCreatePagingErrorView(inflater, parent)
        else -> onCreatePagingItemView(inflater, parent, viewType)
    }

    override fun onBindViewHolder(holder: SimpleViewHolder<Item>, position: Int, payloads: MutableList<Any>) {
        super.onBindViewHolder(holder, position, payloads)
        if (position == itemCount - 1) {
            onNextPageListener?.let {
                if (isIdle()) {
                    // trick do not update when recycler view in-layout
                    holder.itemView.post { it.onNextPage() }
                }
            }
        }
    }

    override fun getBaseItemViewType(position: Int): Int {
        if (position < getCollectionItemCount()) {
            return getPagingItemViewType(position)
        }
        return if (pagingState == PagingAwareAdapter.PagingState.LOADING) TYPE_LOADING else TYPE_ERROR
    }

    override fun getItemCount(): Int {
        var count = super.getItemCount()
        if (pagingState != PagingAwareAdapter.PagingState.IDLE) {
            count++
        }
        return count
    }

    /**
     *  Begin Reactive - Observer
     */
    override fun onNext(items: List<Item>?) {
        pagingState = PagingAwareAdapter.PagingState.IDLE
        super.onNext(items)
    }

    override fun onError(e: Throwable?) {
        pagingState = PagingAwareAdapter.PagingState.ERROR
        super.onError(e)
    }

    override fun onComplete() {
        super.onComplete()
        pagingState = PagingAwareAdapter.PagingState.IDLE
    }
    /**
     * End Reactive - Observer
     */

    override fun enableBinding(viewType: Int): Boolean = if (viewType in arrayOf(TYPE_LOADING, TYPE_ERROR)) false else super.enableBinding(viewType)

    open fun getPagingItemViewType(position: Int) = TYPE_ITEM

    open fun onCreatePagingLoadingView(inflater: LayoutInflater, parent: ViewGroup): View = inflater.inflate(R.layout.layout_paging_progress, parent, false)

    open fun onCreatePagingErrorView(inflater: LayoutInflater, parent: ViewGroup): View {
        val view = inflater.inflate(R.layout.layout_paging_error, parent, false)
        view.buttonPagingRetry.setOnClickListener(onErrorRetryClickListener)
        return view
    }

    abstract fun onCreatePagingItemView(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): View
}