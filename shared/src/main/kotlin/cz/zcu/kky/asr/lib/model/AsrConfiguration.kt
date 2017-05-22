package cz.zcu.kky.asr.lib.model

import android.os.Parcel
import android.os.Parcelable

/**
 * @author Filip Prochazka (@filipproch)
 */
data class AsrConfiguration(
        val id: String,
        val name: String,
        val engineId: String,
        val engineName: String
) : Parcelable {
    companion object {
        @JvmField val CREATOR: Parcelable.Creator<AsrConfiguration> = object : Parcelable.Creator<AsrConfiguration> {
            override fun createFromParcel(source: Parcel): AsrConfiguration = AsrConfiguration(source)
            override fun newArray(size: Int): Array<AsrConfiguration?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(id)
        dest?.writeString(name)
        dest?.writeString(engineId)
        dest?.writeString(engineName)
    }
}