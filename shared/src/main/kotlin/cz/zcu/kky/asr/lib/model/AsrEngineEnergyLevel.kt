package cz.zcu.kky.asr.lib.model

import android.os.Parcel
import android.os.Parcelable

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
data class AsrEngineEnergyLevel(
        val engineId: String,
        val energyLevel: Float
) : Parcelable {
    companion object {
        @JvmField val CREATOR: Parcelable.Creator<AsrEngineEnergyLevel> = object : Parcelable.Creator<AsrEngineEnergyLevel> {
            override fun createFromParcel(source: Parcel): AsrEngineEnergyLevel = AsrEngineEnergyLevel(source)
            override fun newArray(size: Int): Array<AsrEngineEnergyLevel?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
    source.readString(),
    source.readFloat()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(engineId)
        dest?.writeFloat(energyLevel)
    }
}