package messages

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import taps.TapType

@Serializable
data class MsgTapCreate(

    @SerialName("tapType")
    val tapType: TapType,

    @SerialName("tapId")
    val tapId: Int,

    @SerialName("xPos")
    val xPos: Int,

    @SerialName("yPos")
    val yPos: Int,
)
