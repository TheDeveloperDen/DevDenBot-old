package me.bristermitten.devdenbot.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import me.bristermitten.devdenbot.util.atomic
import java.math.BigInteger
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author AlexL
 */
object AtomicIntegerSerializer : KSerializer<AtomicInteger> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("AtomicInteger", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): AtomicInteger {
        return decoder.decodeString().toInt().atomic()
    }

    override fun serialize(encoder: Encoder, value: AtomicInteger) {
        encoder.encodeString(value.get().toString())
    }
}
