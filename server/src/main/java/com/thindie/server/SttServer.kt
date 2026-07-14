package com.thindie.server

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.awt.Robot
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.awt.event.KeyEvent
import java.time.Duration

fun main() {
    val robot = Robot()
    // Переменная для хранения предыдущего промежуточного текста (чтобы стирать старое при вводе "на лету")
    var lastTextLength = 0

    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        install(CallLogging)
        // Подключаем поддержку WebSockets на сервере
        install(WebSockets) {
            pingPeriod = Duration.ofSeconds(15)
            timeout = Duration.ofSeconds(15)
            maxFrameSize = Long.MAX_VALUE
            masking = false
        }

        routing {
            // Веб-сокет эндпоинт, который состыкуется с вашим Android-кодом
            webSocket("/speech") {
                println("🔌 Android подключился по WebSocket сессии!")
                lastTextLength = 0 // Сброс счетчика для новой сессии

                try {
                    // Текст поступает непрерывным потоком фреймов
                    for (frame in incoming) {
                        if (frame is Frame.Text) {
                            val receivedText = frame.readText()

                            // Вызываем метод умного стирания и ввода текста
                            updateTextOnHost(robot, receivedText, lastTextLength)

                            // Запоминаем длину текущего текста для следующей итерации onPartialResults
                            lastTextLength = receivedText.length
                        }
                    }
                } catch (e: Exception) {
                    println("⚠️ Соединение закрыто: ${e.localizedMessage}")
                } finally {
                    println("❌ Android отключился.")
                    lastTextLength = 0
                }
            }
        }
    }.start(wait = true)
}

/**
 * Метод для ввода текста "на лету" (работает в паре с onPartialResults)
 */
fun updateTextOnHost(robot: Robot, newText: String, lastLength: Int) {
    if (newText.isEmpty()) return

    // Так как onPartialResults шлет накопительный текст (например: "при", "привет", "привет как"),
    // нам нужно сначала стереть старый кусок текста ("привет") нажатиями Backspace,
    // а затем вставить новый обновленный ("привет как").
    if (lastLength > 0) {
        for (i in 0 until lastLength) {
            robot.keyPress(KeyEvent.VK_BACK_SPACE)
            robot.keyRelease(KeyEvent.VK_BACK_SPACE)
        }
    }

    // Вставляем свежую фразу через буфер обмена
    val stringSelection = StringSelection(newText)
    Toolkit.getDefaultToolkit().systemClipboard.setContents(stringSelection, null)

    val isMac = System.getProperty("os.name").lowercase().contains("mac")
    val modifierKey = if (isMac) KeyEvent.VK_META else KeyEvent.VK_CONTROL

    robot.keyPress(modifierKey)
    robot.keyPress(KeyEvent.VK_V)
    robot.keyRelease(KeyEvent.VK_V)
    robot.keyRelease(modifierKey)
}