package clients

import ClientHandler
import com.google.gson.Gson
import java.io.*
import java.net.Socket
import java.util.*

class Client(socket: Socket) : Runnable {
    private var socket: Socket? = null
    private var bufferedReader: BufferedReader? = null
    private var bufferedWriter: BufferedWriter? = null
    val uuid = UUID.randomUUID().toString()

    var onDisconnect: (() -> Unit)? = null
    var onMsgReceived: ((String) -> Unit)? = null

    init {
        try {
            this.socket = socket
            bufferedWriter = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))
            bufferedReader = BufferedReader(InputStreamReader(socket.getInputStream()))
            println("New client connected. UUID: C/$uuid")
        } catch (e: IOException) {
            closeConnection()
        }
    }

    override fun run() {
        var message: String?
        while (socket?.isConnected == true) {
            try {
                message = bufferedReader?.readLine()
                if (message == null) continue
                onMsgReceived?.invoke(message)
            } catch (e: IOException) {
                closeConnection()
                break
            }
        }
    }

    fun acceptMessage(msg: String) {
        bufferedWriter?.write(msg)
        bufferedWriter?.newLine()
        bufferedWriter?.flush()
    }

    fun closeConnection() {
        onDisconnect?.invoke()

        try {
            bufferedReader?.close()
            bufferedWriter?.close()
            socket?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}