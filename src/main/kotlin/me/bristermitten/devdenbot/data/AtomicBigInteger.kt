package me.bristermitten.devdenbot.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import me.bristermitten.devdenbot.util.atomic
import java.math.BigInteger
import java.util.concurrent.atomic.AtomicReference

@Serializable(with = AtomicBigInteger.Serializer::class)
class AtomicBigInteger(v: BigInteger) : AtomicReference<BigInteger>(v) {

    operator fun plusAssign(other: BigInteger) {
        getAndAccumulate(other, BigInteger::add)
    }

    operator fun plus(other: AtomicBigInteger) = AtomicBigInteger(get() + other.get())

    operator fun minusAssign(other: BigInteger) {
        getAndAccumulate(other, BigInteger::minus)
    }

    operator fun compareTo(other: BigInteger) = get().compareTo(other)

    operator fun inc() = getAndAccumulate(BigInteger.ONE, BigInteger::add).atomic()

    operator fun dec() = getAndAccumulate(BigInteger.ONE, BigInteger::minus).atomic()


    companion object {
        val ZERO = AtomicBigInteger(BigInteger.ZERO)
    }

    object Serializer : KSerializer<AtomicBigInteger> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("AtomicBigInteger", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): AtomicBigInteger {
            return AtomicBigInteger(decoder.decodeString().toBigInteger())
        }

        override fun serialize(encoder: Encoder, value: AtomicBigInteger) {
            encoder.encodeString(value.get().toString())
        }
    }
}
