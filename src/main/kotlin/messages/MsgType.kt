package messages

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class MsgType(val type: String) {
    @SerialName("createTap")
    CREATE("createTap"),

    @SerialName("destroyTap")
    DESTROY("destroyTap"),

    @SerialName("connect")
    CONNECT("connect")
}