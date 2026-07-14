package com.thindie.server

import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import java.awt.Robot
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.awt.event.KeyEvent
import java.time.Duration

const val STT_ENDPOINT = "/speech"

fun main() {
  val robot = Robot()
  embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
    install(CallLogging)
    install(WebSockets) {
      pingPeriod = Duration.ofSeconds(15)
      timeout = Duration.ofSeconds(15)
      maxFrameSize = Long.MAX_VALUE
      masking = false
    }

    routing {
      webSocket(STT_ENDPOINT) {
        println("🔌 Android подключился по WebSocket сессии!")

        try {
          for (frame in incoming) {
            if (frame is Frame.Text) {
              val receivedText = frame.readText()

              setText(robot, receivedText)
            }
          }
        } catch (e: Exception) {
          println("⚠️ Соединение закрыто: ${e.localizedMessage}")
        } finally {
          println("❌ Android отключился.")
        }
      }
    }
  }.start(wait = true)
}

fun setText(
  robot: Robot,
  newText: String,
) {
  if (newText.isEmpty()) return

  val stringSelection = StringSelection(newText)
  Toolkit.getDefaultToolkit()
    .systemClipboard.setContents(stringSelection, null)

  robot.keyPress(KeyEvent.VK_CONTROL)
  robot.keyPress(KeyEvent.VK_V)
  robot.keyRelease(KeyEvent.VK_V)
  robot.keyRelease(KeyEvent.VK_CONTROL)
  robot.keyPress(KeyEvent.VK_ENTER)
  robot.keyRelease(KeyEvent.VK_ENTER)
}
