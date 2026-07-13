package com.thindie.aspik.feature.home

import com.thindie.aspik.HomeSection
import com.thindie.aspik.feature.home.input.domain.InputRepository
import com.thindie.aspik.feature.home.input.inputRoute
import com.thindie.engine.core.Route
import com.thindie.engine.core.Router
import com.thindie.engine.core.ScreenFlow

class HomeFlow(val router: Router) : ScreenFlow<Route, HomeFlow.Result>(router) {
  lateinit var inputRepository: InputRepository

  override fun start() {
    go(
      inputRoute(
        inputRepository = inputRepository,
        section = HomeSection.Main,
        screenFlow = this,
      ),
    )
  }

  fun switchFlow() {
    router.replaceTop(
      inputRoute(
        inputRepository = inputRepository,
        section = HomeSection.Main,
        screenFlow = this,
      ),
    )
  }

  enum class Result {
    Spiks,
    Success,
    Settings,
  }
}
