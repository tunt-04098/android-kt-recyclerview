package com.tunt.lib.recyclerview.filter

import io.reactivex.functions.Function

/**
 * Created by TuNT on 8/22/18.
 * tunt.program.04098@gmail.com
 */
interface Filter<Item> : Function<List<Item>, List<Item>>