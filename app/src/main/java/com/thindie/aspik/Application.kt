package com.thindie.aspik

import android.app.Application
import com.thindie.aspik.di.ApplicationScope
import com.thindie.engine.core.Log
import com.thindie.engine.core.Router
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

class Application : Application() {
  private val scope =
    CoroutineScope(
      SupervisorJob() + Dispatchers.Default +
        CoroutineExceptionHandler { context, throwable ->
          Log.e({ throwable.message.toString() })
        },
    )

  private var applicationScope: ApplicationScope? = null

  private var router: Router? = null

  val finishCommand =
    MutableSharedFlow<Unit>(
      replay = 0,
      extraBufferCapacity = 3,
      BufferOverflow.DROP_LATEST,
    )

  override fun onCreate() {
    super.onCreate()
  }

  fun requireRouter(): Router {
    if (router == null) {
      router =
        Router {
          finishCommand.tryEmit(Unit)
          router = null
        }
    }
    return requireNotNull(router)
  }

  fun requireAppScope(): ApplicationScope {
    if (applicationScope == null) {
      applicationScope = ApplicationScope.configure(this)
    }
    return requireNotNull(applicationScope)
  }

  companion object {
    const val DEEPLINK = "deeplink"
  }
}
