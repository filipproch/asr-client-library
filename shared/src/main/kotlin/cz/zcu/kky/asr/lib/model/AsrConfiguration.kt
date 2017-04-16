package cz.zcu.kky.asr.lib.model

import android.os.Parcel
import android.os.Parcelable

/**
 * @author Filip Prochazka (@filipproch)
 */
class AsrConfiguration(
        val id: String,
        val name: String,
        val engineId: String,
        val engineName: String)
    : Parcelable {

    private constructor(parcel: Parcel)
            : this(parcel.readString(), // id
            parcel.readString(), // name
            parcel.readString(), // engineId
            (parcel.readString())) // engineName)

    override fun writeToParcel(p: Parcel, flags: Int) {
        p.writeString(id)
        p.writeString(name)
        p.writeString(engineId)
        p.writeString(engineName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField
        public val CREATOR = object : Parcelable.Creator<AsrConfiguration> {
            override fun newArray(size: Int): Array<AsrConfiguration?> {
                return Array(size, { null })
            }

            override fun createFromParcel(parcel: Parcel): AsrConfiguration {
                return AsrConfiguration(parcel)
            }
        }
    }

}