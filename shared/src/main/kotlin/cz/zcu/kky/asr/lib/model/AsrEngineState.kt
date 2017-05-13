package cz.zcu.kky.asr.lib.model

import android.os.Parcel
import android.os.Parcelable

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
data class AsrEngineState(
        val engineKey: String,
        val initialized: Boolean,
        val recognizing: Boolean,
        val configurationKey: String?,
        val grammarKey: String?
) : Parcelable {
    companion object {
        @JvmField val CREATOR: Parcelable.Creator<AsrEngineState> = object : Parcelable.Creator<AsrEngineState> {
            override fun createFromParcel(source: Parcel): AsrEngineState = AsrEngineState(source)
            override fun newArray(size: Int): Array<AsrEngineState?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
            source.readString(),
            1 == source.readInt(),
            1 == source.readInt(),
            source.readString(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(engineKey)
        dest?.writeInt((if (initialized) 1 else 0))
        dest?.writeInt((if (recognizing) 1 else 0))
        dest?.writeString(configurationKey)
        dest?.writeString(grammarKey)
    }
}