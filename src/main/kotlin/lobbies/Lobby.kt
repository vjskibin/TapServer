package lobbies

import clients.Client
import conf.LOBBY_MAX_CLIENT
import conf.RunMode
import conf.ServerConf
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import messages.BaseMessage
import messages.MsgTapCreate
import messages.MsgType
import java.io.IOException
import java.util.UUID

class Lobby {
    private val clients = mutableListOf<Client>()
    private val game = Game()
    val uuid = UUID.randomUUID().toString()

    init {
        println("Lobby created. UUID: $uuid")
    }

    val isAvailable: Boolean
        get() = clients.count() < LOBBY_MAX_CLIENT

    private val jsonSerializer = Json { ignoreUnknownKeys = true }

    fun add(client: Client) {
        clients.add(client)

        println("[SERVER] ATTACH LOBBY [C/${client.uuid}] -> L/$uuid")
        client.acceptMessage("You attached to lobby L/$uuid")

        client.onDisconnect = {
            this.clients.remove(client)
            println("[SERVER] DISCONNECT C/${client.uuid} (L/$uuid)")
            broadcastMessage(client, "[disconnected]")
        }

        client.onMsgReceived = { message ->
            println("[C/${client.uuid}][L/$uuid] $message")
            broadcastMessage(client, message)
            try {
                val msgType = jsonSerializer.decodeFromString<BaseMessage>(message).type
                processPkg(message, msgType)
            } catch (se: SerializationException) {
                println("[JSON Serialization]: Not a valid message (`$message`)")
                if (ServerConf.mode.ordinal <= RunMode.LOGLVL_INFO.ordinal)
                    println(se.localizedMessage)
                if (ServerConf.mode.ordinal <= RunMode.LOGLVL_DEBUG.ordinal)
                    println(se.printStackTrace())
            } catch (ia: IllegalArgumentException) {
                println("[JSON Serialization]: IllegalArgumentException --> ${ia.localizedMessage}")
                if (ServerConf.mode.ordinal <= RunMode.LOGLVL_INFO.ordinal)
                    println(ia.localizedMessage)
                if (ServerConf.mode.ordinal <= RunMode.LOGLVL_DEBUG.ordinal)
                    println(ia.printStackTrace())
            }

        }

        // отправляем клиента слушать сообщения
        Thread(client).start()

        if (clients.count() >= LOBBY_MAX_CLIENT) game.start()
    }

    private fun processPkg(message: String, msgType: MsgType) {
        when (msgType) {
            MsgType.CONNECT -> println("Connect msg")
            MsgType.CREATE -> {
                println("Create msg")
                var msgObj = jsonSerializer.decodeFromString<MsgTapCreate>(message)
            }
            MsgType.DESTROY -> println("Destroy msg")
        }
    }
    private fun broadcastMessage(sender: Client, message: String) {
        for (client in clients) {
            if (client == sender) continue
            try {
                client.acceptMessage("[C/${sender.uuid}]: $message")
            } catch (npEx: NullPointerException) {
                npEx.printStackTrace()
                client.closeConnection()
            } catch (e: IOException) {
                e.printStackTrace()
                client.closeConnection()
            }
        }
    }
}