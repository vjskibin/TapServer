import com.google.gson.Gson
import messages.BaseMessage
import java.io.*
import java.net.Socket

class ClientHandler(socket: Socket) : Runnable {
    private var socket: Socket? = null
    private var bufferedReader: BufferedReader? = null
    private var bufferedWriter: BufferedWriter? = null
    private var clientName: String? = null

    init {
        try {
            this.socket = socket
            bufferedWriter = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))
            bufferedReader = BufferedReader(InputStreamReader(socket.getInputStream()))
            clientName = bufferedReader?.readLine()
            clientHandlers.add(this)
            broadcastMessage("SERVER: $clientName joined the server")
        } catch (e: IOException) {
            closeConnections()
        }
    }

    override fun run() {
        var gson = Gson()
        var message: String?
        while (socket?.isConnected == true) {
            try {
                message = bufferedReader?.readLine()
                if (message == null) continue
//                val mesObj = gson.fromJson<BaseMessage>(message)
                println(message)
                broadcastMessage(message)
            } catch (e: IOException) {
                closeConnections()
                break
            }
        }
    }

    private fun broadcastMessage(message: String) {
        for (clientHandler in clientHandlers.filter { it.clientName != clientName }) {
            try {
                clientHandler.bufferedWriter?.write(message)
                clientHandler.bufferedWriter?.newLine()
                clientHandler.bufferedWriter?.flush()
            } catch (npEx: NullPointerException) {
                npEx.printStackTrace()
                clientHandler.closeConnection(clientHandler)
            } catch (e: IOException) {
                closeConnections()
            }
        }
    }

    private fun closeConnection(clientHandler: ClientHandler) {
        clientHandlers.remove(clientHandler)
        try {
            clientHandler.bufferedReader?.close()
            clientHandler.bufferedWriter?.close()
            clientHandler.socket?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun closeConnections() {
        clientHandlers.remove(this)
        broadcastMessage("SERVER: $clientName has left the server")
        try {
            bufferedReader?.close()
            bufferedWriter?.close()
            socket?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    companion object {
        var clientHandlers = ArrayList<ClientHandler>()
    }
}