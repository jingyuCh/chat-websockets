import com.jetbrains.handson.chat.server.module
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.ktor.websocket.*
import kotlin.test.*

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
        }
    }

}


