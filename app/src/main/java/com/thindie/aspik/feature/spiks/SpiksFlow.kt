package com.thindie.aspik.feature.spiks

import com.thindie.aspik.feature.home.input.domain.InputRepository
import com.thindie.aspik.feature.home.input.inputRoute
import com.thindie.aspik.feature.settings.domain.SettingsRepository
import com.thindie.aspik.feature.spiks.domain.SpiksRepository
import com.thindie.aspik.feature.spiks.list.spiksRoute
import com.thindie.engine.core.Route
import com.thindie.engine.core.Router
import com.thindie.engine.core.ScreenFlow
import com.thindie.engine.core.Section

class SpiksFlow(val router: Router) : ScreenFlow<Route, SpiksFlow.Result>(router) {
  lateinit var spiksRepository: SpiksRepository
  lateinit var inputRepository: InputRepository

  lateinit var settingsRepository: SettingsRepository

  override fun start() {
    go(spiksRoute(this))
  }

  fun switchFlow() {
    router.replaceTop(spiksRoute(this))
  }

  fun goOperateNote() {
    go(
      inputRoute(
        inputRepository = inputRepository,
        screenFlow = this,
        section = Section.Leaf,
        settingsRepository = settingsRepository,
      ),
    )
  }

  enum class Result {
    Main,
    Success,
    Settings,
  }
}
