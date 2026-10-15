package idv.neo.module.utils.fcm.bus

import idv.neo.module.utils.fcm.FcmSignal
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object NotificationBus {
    private val _events = MutableSharedFlow<FcmSignal>(extraBufferCapacity = 10)
    val events = _events.asSharedFlow()

    private val _refreshDashboard = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val refreshDashboard = _refreshDashboard.asSharedFlow()

    fun emit(signal: FcmSignal) {
        _events.tryEmit(signal)
    }

    fun triggerDashboardRefresh() {
        _refreshDashboard.tryEmit(Unit)
    }
}