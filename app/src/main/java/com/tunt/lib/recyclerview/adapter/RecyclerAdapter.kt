package com.tunt.lib.recyclerview.adapter

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.RxView
import com.tunt.lib.recyclerview.CollectionPosition
import com.tunt.lib.recyclerview.Constants
import com.tunt.lib.recyclerview.IDestroy
import com.tunt.lib.recyclerview.filter.Filter
import com.tunt.lib.recyclerview.listener.OnItemChildClickListener
import com.tunt.lib.recyclerview.listener.OnItemClickListener
import com.tunt.lib.recyclerview.selector.GroupSelector
import com.tunt.lib.recyclerview.selector.MultiSelector
import com.tunt.lib.recyclerview.selector.Selector
import com.tunt.lib.recyclerview.selector.SingleSelector
import com.tunt.lib.recyclerview.viewholder.BindingViewHolder
import com.tunt.lib.recyclerview.viewholder.SimpleViewHolder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

/**
 * Created by TuNT on 8/23/18.
 * tunt.program.04098@gmail.com
 */
abstract class RecyclerAdapter<Item> internal constructor(val hasHeader: Boolean = false,
                                                          val hasFooter: Boolean = false,
                                                          val allowDeselectItem: Boolean = false)
    : RecyclerView.Adapter<SimpleViewHolder<Item>>(), ItemAdapter<Item>, IDestroy {

    companion object {
        const val TYPE_HEADER: Int = 0
        const val TYPE_FOOTER: Int = 1
        const val TYPE_ITEM: Int = 2
        const val TYPE_MAX = TYPE_ITEM

        /**
         * The enum that defines two types of selector.
         */
        enum class SelectorType {
            SINGLE, MULTI
        }
    }

    /**
     * items list to display
     */
    private var items = ArrayList<Item>()

    /**
     * when filter is enabling -> this list is backup for original list
     */
    private var originItems = ArrayList<Item>()

    /**
     * List selector of origin Item
     */
    private var originSelectedIndex = ArrayList<Int>()

    /**
     * Filter function
     */
    private var filters = SparseArray<Filter<Item>>()

    /**
     * Item click listener
     */
    private var onItemClickListener: OnItemClickListener<Item>? = null

    /**
     * Array of view resId and corresponding OnClickListener
     */
    private val onItemChildClickListeners = SparseArray<OnItemChildClickListener<Item>>()

    var selector: Selector

    private val bindingData = SparseArray<Any>()

    init {
        selector = createSelector()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder<Item> {
        val inflater = LayoutInflater.from(parent.context);
        var itemView: View = when (viewType) {
            TYPE_HEADER -> onCreateHeaderView(inflater, parent)!!
            TYPE_FOOTER -> onCreateFooterView(inflater, parent)!!
            else -> onCreateItemView(inflater, parent, viewType)
        }

        var holder: SimpleViewHolder<Item>
        if (enableBinding(viewType)) {
            holder = BindingViewHolder(itemView, selector)
        } else {
            holder = SimpleViewHolder(itemView)
        }
        /**
         * Setting view click listener
         */
        holder.onItemClickListener = onItemClickListener
        /**
         * Setting child views click listener
         */
        for (i in 0 until onItemChildClickListeners.size()) {
            val resId = onItemChildClickListeners.keyAt(i)
            val listener = onItemChildClickListeners.valueAt(i)
            val childView = holder.itemView.findViewById<View>(resId)
            childView?.let {
                RxView.clicks(childView)
                        .throttleFirst(Constants.DEFAULT_THROTTLE_FIRST_CLICK_TIME, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                        .subscribe { listener.onItemChildClicked(childView, holder.getData(), holder.adapterPosition) }
            }
        }

        // extend setup for view holder
        onAfterCreateViewHolder(holder, viewType)

        return holder
    }

    override fun onBindViewHolder(holder: SimpleViewHolder<Item>, position: Int) {
        if (isHeaderView(position)) return
        if (isFooterView(position)) return
        val offsetPosition = if (hasHeader) 1 else 0
        val positionInCollection = position - offsetPosition
        if (holder is BindingViewHolder) {
            holder.bind(getItem(positionInCollection), CollectionPosition(getCollectionItemCount(), position), bindingData)
        } else {
            holder.bind(getItem(positionInCollection))
        }

        // extend setup for bind view holder
        onAfterBindViewHolder(holder, position)
    }

    override fun getItemViewType(position: Int): Int = when {
        isHeaderView(position) -> TYPE_HEADER
        isFooterView(position) -> TYPE_FOOTER
        else -> getBaseItemViewType(position - if (hasHeader) 1 else 0)
    }

    override fun getCollectionItemCount(): Int = items.size

    override fun getItemCount(): Int = getCollectionItemCount() + (if (hasHeader) 1 else 0) + (if (hasFooter) 1 else 0)

    override fun addItem(item: Item) {
        if (!items.contains(item)) {
            items.add(item)
        }
    }

    override fun clear() {
        items.clear()
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): Item = items[position]

    override fun getItems(): List<Item> = items

    override fun setItems(items: List<Item>) {
        recalculateSelector(getSelected(), this.items, items)
        this.items = items as ArrayList<Item>
        notifyDataSetChanged()
    }

    override fun isEmpty(): Boolean = items.isEmpty()

    override fun prependItem(item: Item) = items.add(0, item)

    override fun removeItem(position: Int) {
        items.removeAt(position)
    }

    override fun destroy() {
        onItemClickListener = null
    }

    /**
     *  Begin Reactive - Observer
     */
    override fun onSubscribe(d: Disposable?) {
    }

    override fun onNext(items: List<Item>?) {
        Log.i(Constants.TAG, "onNext on RecyclerAdapter from source() $items")
        if (items == null) return
        if (this.items.isEmpty()) {
            if (isFiltering()) {
                originItems = ArrayList(items)
                applyFilter()
            } else {
                setItems(items)
            }
            return
        }
        // if has filter
        if (isFiltering()) {
            originItems.addAll(items)
            applyFilter()
        } else {
            this.items.addAll(items)
            notifyDataSetChanged()
        }
    }

    override fun onError(e: Throwable?) {
        notifyDataSetChanged()
        e?.printStackTrace()
    }

    override fun onComplete() {
        Log.i(Constants.TAG, "onCompleted on RecyclerAdapter from source()")
    }
    /**
     * End Reactive - Observer
     */

    fun addBinding(variableId: Int, value: Any) = bindingData.put(variableId, value)

    fun removeBinding(variableId: Int) = bindingData.remove(variableId)

    /**
     * Get type of list item view bind with {@link Item item}. Always start from {@value TYPE_MAX}
     *
     * @param position: position in {@code List<Item>}
     * @return
     */
    open fun getBaseItemViewType(position: Int): Int = TYPE_ITEM

    open fun onCreateHeaderView(inflater: LayoutInflater, parent: ViewGroup): View? = null

    open fun onCreateFooterView(inflater: LayoutInflater, parent: ViewGroup): View? = null

    abstract fun onCreateItemView(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): View

    open fun onAfterCreateViewHolder(holder: RecyclerView.ViewHolder, viewType: Int) {}

    open fun onAfterBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {}

    fun isHeaderView(position: Int): Boolean = hasHeader && position == 0

    fun isFooterView(position: Int): Boolean = hasFooter && position == itemCount - 1

    open fun enableBinding(viewType: Int): Boolean = !(viewType == TYPE_HEADER || viewType == TYPE_FOOTER)

    protected fun createSelector(): Selector = SingleSelector(allowDeselectItem)

    fun isFiltering(): Boolean = filters.size() > 0

    fun applyFilter() {
        if (isFiltering()) {
            // if original Items is empty, backup here
            if (originItems.isEmpty()) {
                originItems = ArrayList(items)
                originSelectedIndex = getSelected() as ArrayList<Int>
            }

            var filterItems = originItems
            for (index in 0 until filterItems.size) {
                filterItems = filters.get(filters.keyAt(index)).apply(filterItems) as ArrayList<Item>
            }

            setItems(filterItems)
            notifyDataSetChanged()
        } else {
            clearFilter()
        }
    }

    fun addFilter(filterIndex: Int? = null, filter: Filter<Item>) {
        filters.put(filterIndex ?: filters.size(), filter)
        applyFilter()
    }

    fun removeFilter(filterIndex: Int) {
        filters.remove(filterIndex)
        applyFilter()
    }

    fun clearFilter() {
        filters.clear()
        selector.clearSelected()
        if (getSelected().isEmpty() || selector is MultiSelector) {
            originSelectedIndex?.let {
                if (!it.isEmpty()) {
                    for (selectedIndex in it) {
                        selector.setSelected(selectedIndex, true)
                    }
                }
            }
        } else {
            recalculateSelector(getSelected(), items, originItems)
        }
        items = ArrayList(originItems)
        originItems.clear()
        notifyDataSetChanged()
    }

    fun getSelected(): List<Int> = selector.getSelected()

    fun getSelectedObject(): List<Item> {
        val selectedObjects = ArrayList<Item>()
        if (!getSelected().isEmpty()) {
            for (index in getSelected()) {
                selectedObjects.add(if (selector is GroupSelector) originItems[index] else items[index])
            }
        }
        return selectedObjects
    }

    private fun recalculateSelector(selected: List<Int>, oldList: List<Item>, newList: List<Item>) {
        if (selector is GroupSelector && isFiltering()) return

        var valid = true
        for (index in selected) {
            if (oldList.size <= index) {
                valid = false
                break
            }
        }
        if (!valid) return

        // combine with original to the final result
        // 1. update to original selected items
        if (selector is MultiSelector && isFiltering()) {
            selector.clearSelected()
            for (index in 0 until oldList.size) {
                val originIndex = originItems.indexOf(oldList[index])
                if (originIndex < 0) continue
                val itemSelected = selected.contains(index)
                if (itemSelected) {
                    if (!originSelectedIndex.contains(originIndex)) {
                        originSelectedIndex.add(originIndex)
                    }
                } else {
                    originSelectedIndex.remove(originIndex)
                }
            }
            for (index in 0 until originSelectedIndex.size) {
                val newIndex = newList.indexOf(originItems[originSelectedIndex[index]])
                if (newIndex < 0) continue
                selector.setSelected(newIndex, true)
            }
            return
        }

        if (selected.isEmpty() || oldList.isEmpty()) return

        // 2. recalculate
        selector.clearSelected()
        for (index in selected) {
            val newIndex = newList.indexOf(oldList[index])
            if (newIndex >= 0) {
                selector.setSelected(newIndex, true)
            }
        }
    }
}