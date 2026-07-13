package com.thindie.aspik.feature.spiks.list

import com.thindie.aspik.HomeSection
import com.thindie.aspik.feature.spiks.SpiksFlow
import com.thindie.aspik.feature.spiks.domain.SpeekNote
import com.thindie.aspik.feature.spiks.domain.SpiksRepository
import com.thindie.engine.core.RouteFactory
import com.thindie.engine.core.ScreenScope
import com.thindie.engine.core.stateSink
import com.thindie.engine.core.sub
import com.thindie.engine.core.transition

fun spiksRoute(homeFlow: SpiksFlow) =
  RouteFactory.create(
    id = "home_flow_spiks_route",
    initialState = SpiksState(),
    execute = { command: SpiksCommand, state: SpiksState -> homeFlow.spiksExec(state, command) },
    section = HomeSection.Spiks,
    routeContent = { screenScope: ScreenScope<SpiksState, SpiksCommand> ->
      SpiksScreenContent(screenScope)
    },
    stateSink = { scope ->
      scope.subscribeToNotes(homeFlow.spiksRepository)
    },
  )

internal fun ScreenScope<SpiksState, SpiksCommand>.subscribeToNotes(repository: SpiksRepository) {
  stateSink(this) { s ->
    s.sub(repository.notes).transition(
      block = { prevState: SpiksState, newNotes: List<SpeekNote> ->
        prevState.copy(notes = newNotes)
      },
    )
  }
}
