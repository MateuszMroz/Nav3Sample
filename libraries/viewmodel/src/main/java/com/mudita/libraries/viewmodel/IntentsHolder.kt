package com.mudita.libraries.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.Job
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

/**
 * Default name for the [IntentsHolder] object kept in closeables of [ViewModel]
 * */
private const val DefaultHolderName = "com.internals.intent.holder"

/**
 * Holder which keeps track of active jobs requested by [intent] method.
 * Basic implementation of that holder is [ConcurrentIntentHolder] which is
 * thread safe. There is [obtainIntentHolder] extension function for [androidx.lifecycle.ViewModel]
 * which manages obtaining and creating [IntentsHolder]
 *
 * For more information see:
 *  - [ConcurrentIntentHolder]
 *  - [Entry]
 *  - [obtainIntentHolder]
 * */
interface IntentsHolder : AutoCloseable, MutableMap<Any, IntentsHolder.Entry> {

    /**
     * [handle] - stores [Job.invokeOnCompletion] [DisposableHandle] for currently running [Job].
     * It is used for disposing object from the [IntentsHolder]
     * [job] - stores [Job] for the currently running intent. It is used for cancelling task in
     * case of restart or dropping tasks
     * */
    data class Entry(
        val handle: DisposableHandle,
        val job: Job,
    )
}

/**
 * Retrieves [IntentsHolder] from [ViewModel] closeables [AutoCloseable].
 * If holder does not exists uses [factory] to create and store new [IntentsHolder]
 * - [key] - id of [AutoCloseable] within [ViewModel]
 * - [factory] - [IntentsHolder] factory method
 * */
fun ViewModel.obtainIntentHolder(
    key: String = DefaultHolderName,
    factory: () -> IntentsHolder = ::ConcurrentIntentHolder,
): IntentsHolder {
    return getCloseable(key) ?: run {
        val holder = factory()
        addCloseable(key, holder)
        holder
    }
}

/**
 * Concurrent implementation of [IntentsHolder] which is based on
 * thread-safe [ConcurrentHashMap] implementation.
 * */
class ConcurrentIntentHolder(
    private val intents: ConcurrentMap<Any, IntentsHolder.Entry> = ConcurrentHashMap()
) : IntentsHolder, MutableMap<Any, IntentsHolder.Entry> by intents {

    override fun close() = intents.clear()
}
