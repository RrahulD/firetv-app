package tv.accedo.dishonstream2.ui.base.viewholder

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*

abstract class BaseLifeCycleViewHolder(
    view: View,
    parentLifecycleOwner: LifecycleOwner? = null
) : RecyclerView.ViewHolder(view), LifecycleOwner {
    private val lifecycleRegistry = LifecycleRegistry(this)
    private var viewHolderScope: CoroutineScope? = null

    init {
        parentLifecycleOwner?.lifecycle?.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                when (event) {
                    Lifecycle.Event.ON_STOP -> moveLifecycleToStopped()
                    Lifecycle.Event.ON_RESUME -> moveLifeCycleToResumed()
                    Lifecycle.Event.ON_DESTROY -> {
                        parentLifecycleOwner.lifecycle.removeObserver(this)
                        moveLifecycleToDestroyed()
                    }
                }
            }
        })
    }

    open fun attach() {
        moveLifeCycleToResumed()
    }

    open fun detach() {
        moveLifecycleToStopped()
    }

    protected fun getViewHolderScope() =
        viewHolderScope ?: CoroutineScope(Dispatchers.Main + SupervisorJob()).also { viewHolderScope = it }

    private fun moveLifeCycleToResumed() {
        if (lifecycleRegistry.currentState == Lifecycle.State.RESUMED) return
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
    }

    private fun moveLifecycleToStopped() {
        if (lifecycleRegistry.currentState == Lifecycle.State.CREATED) return
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
    }

    private fun moveLifecycleToDestroyed() {
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        if (viewHolderScope?.isActive == true) {
            viewHolderScope?.cancel()
            viewHolderScope = null
        }
    }

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }
}