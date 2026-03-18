package com.mudita.libraries.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

/**
 * QUESTIONS:
 *  - czy na pewno chcemy zeby viewmodelscope byl naszym bazowym? Czy na pewno chcemy
 *    zeby kazda operacja bez zmiany dispatchera byla na MainThreadie? Czy moze jednak
 *    wykonywanie na MainThread jest szczegolna akcja - ViewModelScope niestety bazowo
 *    jest MainThread i trzeba robic context switch z withContext, etc.
 *  -
 **/

/**
 * Implementation of intent starter which manages it's tasks using [IntentsHolder].
 * Default IntentHolder is [ConcurrentIntentHolder]. There are multiple strategies
 * for intents [key] collision resolution which are listed over [IntentStrategy].
 * Default strategy is [IntentStrategy.Restart].
 *
 * - [key] - id that should uniquely identify [block].
 * - [holder] - Intent registry. It is cleared when [ViewModel] is being cleared
 * - [coroutineScope] - scope under which all [block]'s are started (if [strategy]
 * conditions are met)
 * - [strategy] - resolution of [key]/[block] collision.
 * [IntentStrategy.Restart] and [IntentStrategy.Drop] should be sufficient in most
 * cases but there is also [IntentStrategy.Feral] which is untracked.
 * - [block] - body of intent code to be executed
 *
 * */
fun ViewModel.intent(
    key: Any = block::javaClass.name,
    holder: IntentsHolder = obtainIntentHolder(),
    coroutineScope: CoroutineScope = viewModelScope,
    strategy: IntentStrategy = IntentStrategy.Restart,
    block: suspend CoroutineScope.() -> Unit,
) {
    val entry = holder[key]
    val completionHandler = { error: Throwable? ->
        if (error !is CancellationException) {
            holder.remove(key)
        }
    }

    val disposeHandler: () -> Unit = {
        entry?.handle?.dispose()
        entry?.job?.cancel()
    }

    /**
     * NOTE: IntentStrategy may launch its jobs using `CoroutineStart.LAZY` so we
     * will start it manually. It is the safest way to launch new coroutine in case
     * of attaching invokeOnCompletion handler to the Job. Normally [Job.launch] will
     * start the execution of its tasks at first suspension point of the Dispatcher.
     * Launching it using `CoroutineStart.LAZY` we decide when the [Job] starts
     * and there is no race condition in case of cancellation and removing
     * entry in completionHandler.
     * */
    val enqueueHandler = { job: Job ->
        holder[key] = IntentsHolder.Entry(
            handle = job.invokeOnCompletion(completionHandler),
            job = job
        )

        if (!job.isActive) {
            job.start()
        }
    }

    strategy(
        scope = coroutineScope,
        currentJob = entry?.job,
        disposeJob = disposeHandler,
        enqueueJob = enqueueHandler,
        block = block,
    )
}
