package com.thindie.aspik.feature.spiks.list

import com.thindie.aspik.domain.Id
import com.thindie.aspik.domain.SpeekNote
import com.thindie.engine.core.Command

internal sealed interface SpiksCommand : Command {
  data object Back : SpiksCommand

  data class OpenNewNote(val text: String = "") : SpiksCommand

  data class UpdateNote(val note: SpeekNote) : SpiksCommand

  data class DeleteNote(val id: Id) : SpiksCommand
}
