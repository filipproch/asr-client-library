package cz.zcu.kky.asr.lib

import android.os.Parcel
import android.os.Parcelable

/**
 * @author Filip Prochazka (@filipproch)
 */
class AsrConfiguration(
        val id: String,
        val name: String,
        val engineId: String,
        val isBinaryGrammarFileSupported: Boolean)
    : Parcelable {

    private constructor(parcel: Parcel)
            : this(parcel.readString(), // id
            parcel.readString(), // name
            parcel.readString(), // engineId
            (parcel.readInt() == 1)) // isBinaryGrammarFileSupported)

    override fun writeToParcel(p: Parcel, flags: Int) {
        p.writeString(id)
        p.writeString(name)
        p.writeString(engineId)
        p.writeInt(if (isBinaryGrammarFileSupported) 1 else 0)
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