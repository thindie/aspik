package com.thindie.server

import kotlinx.serialization.Serializable

@Serializable
data class TextPayload(val text: String)