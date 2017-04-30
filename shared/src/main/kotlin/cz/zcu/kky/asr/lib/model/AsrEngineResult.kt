package cz.zcu.kky.asr.lib.model

import android.os.Parcel
import android.os.Parcelable

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
data class AsrEngineResult(
        val engineId: String,
        val result: String,
        val confidence: Float,
        val rejected: Boolean
) : Parcelable {
    companion object {
        @JvmField val CREATOR: Parcelable.Creator<AsrEngineResult> = object : Parcelable.Creator<AsrEngineResult> {
            override fun createFromParcel(source: Parcel): AsrEngineResult = AsrEngineResult(source)
            override fun newArray(size: Int): Array<AsrEngineResult?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readFloat(),
            1 == source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(engineId)
        dest?.writeString(result)
        dest?.writeFloat(confidence)
        dest?.writeInt((if (rejected) 1 else 0))
    }
}