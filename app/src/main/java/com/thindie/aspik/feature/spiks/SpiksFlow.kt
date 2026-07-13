package com.thindie.aspik.feature.spiks

import com.thindie.aspik.feature.spiks.domain.SpiksRepository
import com.thindie.aspik.feature.spiks.list.spiksRoute
import com.thindie.engine.core.Route
import com.thindie.engine.core.Router
import com.thindie.engine.core.ScreenFlow

class SpiksFlow(val router: Router) : ScreenFlow<Route, SpiksFlow.Result>(router) {
  lateinit var spiksRepository: SpiksRepository

  override fun start() {
    go(spiksRoute(this))
  }

  fun switchFlow() {
    router.replaceTop(spiksRoute(this))
  }

  enum class Result {
    Main,
    Success,
    Settings,
  }
}
