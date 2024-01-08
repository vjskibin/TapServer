package taps

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class TapType(val type: String) {
    @SerialName("simple")
    SIMPLE("simple"),

    @SerialName("double")
    DOUBLE("double")
}