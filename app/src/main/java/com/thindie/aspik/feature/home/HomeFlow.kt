package com.thindie.aspik.feature.home

import com.thindie.aspik.feature.home.input.inputRoute
import com.thindie.engine.core.Route
import com.thindie.engine.core.Router
import com.thindie.engine.core.ScreenFlow

class HomeFlow(val router: Router) : ScreenFlow<Route, HomeFlow.Result>(router) {
  override fun start() {
    go(inputRoute(this))
  }

  fun switchFlow() {
    router.replaceTop(inputRoute(this))
  }

  enum class Result {
    Spiks,
    Success,
    Settings,
  }
}
