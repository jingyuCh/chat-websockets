package com.jetbrains.handson.chat.server

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ObjectType
import com.typesafe.config.ConfigFactory
import io.ktor.application.*
import io.ktor.config.*
import io.ktor.features.*
import io.ktor.http.*

import io.ktor.http.cio.websocket.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.websocket.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString

import java.util.*


@Serializable
data class ChatData(val type: String, val message: String,val userName:String)

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
                            println(chatData.userName +"join")
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



    }


}
