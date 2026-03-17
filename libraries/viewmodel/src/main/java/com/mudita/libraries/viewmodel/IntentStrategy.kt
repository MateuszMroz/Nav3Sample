package com.mudita.libraries.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Strategy of how to handle conflicting intents (intents that have the same key value
 * within the same [IntentsHolder] bucket. Default implementation is [Restart].
 * Currently there are following strategies:
 *  - [Restart]
 *  - [Drop]
 *  - [Feral]
 * Check the description of each to know which one suite your needs best.
 * */
fun interface IntentStrategy {
    /**
     * Strategy is provided with following parameters:
     * - [scope] - [kotlinx.coroutines.CoroutineScope] that new [block] should be launch with if invoked
     * - [currentJob] - [kotlinx.coroutines.Job] that is currently running. If `null` then either last [block]
     * has already finished and was cleaned up or there was no previous invocations of the [block]
     * - [disposeJob] - disposes the execution of [block] for currently running
     * job. This should be used if the [currentJob] is going to be changed.
     * - [enqueueJob] - add [kotlinx.coroutines.Job] to be started. Keep in mind that [kotlinx.coroutines.Job] should be
     * lazily initialized if you want for the dispose/enqueue mechanism to work correctly.
     * Prefer to launch coroutines using `CoroutineStart.LAZY` as `start` type.
     * - [block] - code that is intent to run if conditions are met
     * */
    operator fun invoke(
        scope: CoroutineScope,
        currentJob: Job?,
        disposeJob: () -> Unit,
        enqueueJob: (Job) -> Unit,
        block: suspend CoroutineScope.() -> Unit
    )

    companion object {
        /**
         * Strategy that always cancels previously running jobs and starts them again
         * from using newly provided `block`. If the intent should be always in sync
         * with the newest data flow this one should be used
         * */
        val Restart = IntentStrategy { scope, currentJob, dispose, enqueue, block ->
            if (currentJob != null) {
                dispose()
            }
            enqueue(
                scope.launch(start = CoroutineStart.LAZY, block = block)
            )
        }

        /**
         * Strategy that always drops new `block` execution in favor of finishing
         * what was already scheduled. This is intended to be used in situations like
         * long running task which should finish without interruption
         * */
        val Drop = IntentStrategy { scope, currentJob, _, enqueue, block ->
            if (currentJob != null && !currentJob.isCompleted) {
                return@IntentStrategy
            }
            enqueue(
                scope.launch(start = CoroutineStart.LAZY, block = block)
            )
        }

        /**
         * Strategy that is not tracked at all. It is not handled by any registry
         * and controlled only by its CoroutineScope
         * */
        val Feral = IntentStrategy { scope, _, _, enqueue, block ->
            scope.launch(block = block)
        }
    }
}