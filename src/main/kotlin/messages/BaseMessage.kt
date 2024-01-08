package messages

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BaseMessage(

    @SerialName("type")
    val type: MsgType,

    @SerialName("client_id")
    val clientId: Int
)
