package lobbies
import conf.SERVER_LOBBIES
import clients.Client
import java.sql.DriverManager
import java.util.concurrent.ConcurrentLinkedQueue

class LobbyInteractor {
    private var lobbies: MutableList<Lobby>  = mutableListOf<Lobby>()

    companion object {
        var clientsQueue = ConcurrentLinkedQueue<Client>()
    }

    init {
        for (i in 0..SERVER_LOBBIES) lobbies.add(Lobby())

        // каждые полсекунды будет проверять очередь клиентов
        Thread {
            while (true) {
                if (clientsQueue.peek() == null) continue

                for (lobby in lobbies) {
                    if (lobby.isAvailable) {
                        lobby.add(clientsQueue.poll())
                        break
                    } else {
                        println("C/${clientsQueue.peek().uuid} NA to L/${lobby.uuid}")
                    }
                }
            }
        }.start()
    }

    fun process(client: Client) {
        clientsQueue.add(client)
    }


}