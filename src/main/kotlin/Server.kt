import clients.Client
import conf.SERVER_PORT
import lobbies.LobbyInteractor
import java.io.IOException
import java.net.InetAddress
import java.net.ServerSocket

class Server {
    private var serverSocket: ServerSocket? = null
    private val lobbyInteractor = LobbyInteractor()

    private fun startServer() {
        println("Server started")
        println("PORT: $SERVER_PORT")
        println("HOST: ${InetAddress.getLoopbackAddress()}")

        try {
            while (serverSocket?.isClosed == false) {
                val socket = serverSocket?.accept() ?: continue
                val client = Client(socket)
                lobbyInteractor.process(client)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun closeServerSocket() {
        try {
            serverSocket?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun run() {
        serverSocket = ServerSocket(SERVER_PORT)
        startServer()
    }
}