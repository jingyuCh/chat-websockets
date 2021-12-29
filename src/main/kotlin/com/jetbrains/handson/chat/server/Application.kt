package com.jetbrains.handson.chat.server

import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.websocket.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*


@Serializable
data class ChatData(val type: String, var message: String, val userName:String)

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    install(WebSockets)

    routing {
        val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())
        webSocket("/chat") {
            println("Adding user!")
            val thisConnection = Connection(this)
            connections += thisConnection
            try {
//                send("You are connected! There are ${connections.count()} users here.")
                for (frame in incoming) {
                    frame as? Frame.Text ?: continue
                    val chatData = Json.decodeFromString<ChatData>(frame.readText())
                    when (chatData.type) {
                        "join" -> {
                            println(chatData.userName +" join")
                        }
                        "message" -> {
                            println(chatData.userName +" send")
                        }
                    }
                    connections.forEach {
                        it.session.send(frame.readText())
                    }
                }
            } catch (e: Exception) {
                println(e.localizedMessage)
            } finally {
                println("Removing $thisConnection!")
                connections -= thisConnection
            }
        }

        static("/static") {
            resources("files")
        }
        get("/") {
            call.respondText("Hello, world!")
        }
        webSocket("/echo") {
            send("Please enter your name")
            for (frame in incoming) {
                frame as? Frame.Text ?: continue
                val chatData = Json.decodeFromString<ChatData>(frame.readText())
                when (chatData.type) {
                    "join" -> {
                        send(Frame.Text("Hi, "+chatData.userName+"!"))
                    }
                    "message" -> {
                        println(chatData.userName +" say " + chatData.message)
                    }
                    "leave" -> {
                        close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
                    }
                }
            }
        }

    }


}
