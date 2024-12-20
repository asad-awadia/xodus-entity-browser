package jetbrains.xodus.browser.web

import io.ktor.server.engine.embeddedServer
import io.ktor.server.jetty.Jetty
import jetbrains.exodus.entitystore.PersistentEntityStoreImpl
import jetbrains.exodus.entitystore.PersistentEntityStores
import jetbrains.exodus.env.Environments

fun main() {
    Home.setup()
    val appPort = Integer.getInteger("server.port", 18080)
    val appHost = System.getProperty("server.host", "localhost")
    val context = System.getProperty("server.context", "/")

    val store = PersistentEntityStores.newInstance(Environments.newInstance("some path to app"), "teamsysstore")

    val server = embeddedServer(Jetty, port = appPort, host = appHost) {
        val webApplication = object : EmbeddableWebApplication(lookup = { listOf(store) }) {

            override fun PersistentEntityStoreImpl.isForcedlyReadonly() = true

        }
        HttpServer(webApplication, context).setup(this)
    }
    server.start(false)

    Browser.launch("http://$appHost:$appPort$context")
}
