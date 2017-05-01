package cz.zcu.kky.asr.lib.model

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
data class AsrCommandResponse(
        val command: Int,
        val commandId: String,
        val success: Boolean,
        val extras: Bundle?
) : Parcelable {
    companion object {
        @JvmField val CREATOR: Parcelable.Creator<AsrCommandResponse> = object : Parcelable.Creator<AsrCommandResponse> {
            override fun createFromParcel(source: Parcel): AsrCommandResponse = AsrCommandResponse(source)
            override fun newArray(size: Int): Array<AsrCommandResponse?> = arrayOfNulls(size)
        }

        @JvmField val EXTRA_CLIENT_ID = "client_id"
    }

    constructor(source: Parcel) : this(
            source.readInt(),
            source.readString(),
            1 == source.readInt(),
            source.readParcelable<Bundle?>(Bundle::class.java.classLoader)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeInt(command)
        dest?.writeString(commandId)
        dest?.writeInt((if (success) 1 else 0))
        dest?.writeParcelable(extras, 0)
    }
}