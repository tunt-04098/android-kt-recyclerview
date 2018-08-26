package com.tunt.lib.recyclerview.adapter

import io.reactivex.Observer

/**
 * Created by TuNT on 8/23/18.
 * tunt.program.04098@gmail.com
 */
interface ItemAdapter<Item> : Observer<List<Item>> {

    fun prependItem(item: Item)

    fun addItem(item: Item)

    fun removeItem(position: Int)

    fun clear()

    fun getCollectionItemCount(): Int

    fun getItems(): List<Item>

    fun setItems(items: List<Item>)

    fun getItem(position: Int): Item

    fun isEmpty(): Boolean

    fun notifyDataSetChanged()
}