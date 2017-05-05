package cz.zcu.kky.asr.lib.model

import android.os.Parcel
import android.os.Parcelable

/**
 * TODO: add description
 *
 * @author Filip Prochazka (@filipproch)
 */
data class AsrGrammar(
        val key: String,
        val name: String,
        val compiled: Boolean
) : Parcelable {
    companion object {
        @JvmField val CREATOR: Parcelable.Creator<AsrGrammar> = object : Parcelable.Creator<AsrGrammar> {
            override fun createFromParcel(source: Parcel): AsrGrammar = AsrGrammar(source)
            override fun newArray(size: Int): Array<AsrGrammar?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            1 == source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(key)
        dest.writeString(name)
        dest.writeInt((if (compiled) 1 else 0))
    }
}