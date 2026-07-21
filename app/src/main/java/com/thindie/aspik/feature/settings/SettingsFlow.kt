package com.thindie.aspik.feature.settings

import com.thindie.aspik.feature.settings.domain.SettingsRepository
import com.thindie.aspik.feature.settings.ip.settingsIpRoute
import com.thindie.aspik.feature.settings.main.settingsRoute
import com.thindie.engine.core.Route
import com.thindie.engine.core.Router
import com.thindie.engine.core.ScreenFlow

class SettingsFlow(val router: Router) : ScreenFlow<Route, SettingsFlow.Result>(router) {
  lateinit var settingsRepository: SettingsRepository

  override fun start() {
    go(settingsRoute(this))
  }

  fun switchFlow() {
    router.replaceTop(settingsRoute(this))
  }

  fun openIpScreen() {
    go(
      settingsIpRoute(),
    )
  }

  enum class Result {
    Main,
    Success,
    Spiks,
  }
}
