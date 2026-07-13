package com.thindie.aspik.feature.spiks.list

import com.thindie.aspik.HomeSection
import com.thindie.aspik.feature.spiks.SpiksFlow
import com.thindie.engine.core.RouteFactory
import com.thindie.engine.core.ScreenScope

fun spiksRoute(homeFlow: SpiksFlow) =
  RouteFactory.create(
    id = "home_flow_spiks_route",
    initialState = SpiksState(),
    execute = { command: SpiksCommand, state: SpiksState -> homeFlow.spiksExec(state, command) },
    section = HomeSection.Spiks,
    routeContent = { screenScope: ScreenScope<SpiksState, SpiksCommand> ->
      SpiksScreenContent(screenScope)
    },
  )
