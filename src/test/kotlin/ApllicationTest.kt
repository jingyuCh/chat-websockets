import com.jetbrains.handson.chat.server.ChatData
import com.jetbrains.handson.chat.server.module
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.server.testing.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun testRoot() {
        withTestApplication(Application::module) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("Hello, world!", response.content)
            }
            handleRequest(HttpMethod.Get, "static/index.html").apply {
                assertEquals(HttpStatusCode.OK, response.status())
            }
            handleWebSocketConversation("/echo") { incoming, outgoing ->
                val greetingText = (incoming.receive() as Frame.Text).readText()
                assertEquals("Please enter your name", greetingText)

                val format = Json { prettyPrint = true }
                val joinData = ChatData("join","","TEST1")
                outgoing.send(Frame.Text(format.encodeToString(joinData)))
                val responseText = (incoming.receive() as Frame.Text).readText()
                assertEquals("Hi, TEST1!", responseText)

                val leaveData = ChatData("leave","bye","TEST1")
                outgoing.send(Frame.Text(format.encodeToString(leaveData)))
//                outgoing.send(Frame.Text("bye"))
                val closeReason = (incoming.receive() as Frame.Close).readReason()?.message
                assertEquals("Client said BYE", closeReason)
            }
        }
    }

}


