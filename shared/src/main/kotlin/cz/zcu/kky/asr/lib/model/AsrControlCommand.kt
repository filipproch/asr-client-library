package cz.zcu.kky.asr.lib.model

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
data class AsrControlCommand(val code: Int, val extras: Bundle?) : Parcelable {
    companion object {
        @JvmField val CREATOR: Parcelable.Creator<AsrControlCommand> = object : Parcelable.Creator<AsrControlCommand> {
            override fun createFromParcel(source: Parcel): AsrControlCommand = AsrControlCommand(source)
            override fun newArray(size: Int): Array<AsrControlCommand?> = arrayOfNulls(size)
        }

        /* Control Commands */

        @JvmField val CMD_LOAD_CONFIGURATION = 1000
        @JvmField val CMD_RELEASE_CONFIGURATION = 1001

        @JvmField val CMD_START_RECOGNIZING = 1002
        @JvmField val CMD_STOP_RECOGNIZING = 1003

        @JvmField val CMD_UPDATE_SENSITIVITY_LEVEL = 1004

        @JvmField val CMD_SET_GRAMMAR = 1005
        @JvmField val CMD_SET_GRAMMAR_FILE = 1006
        @JvmField val CMD_COMPILE_GRAMMAR = 1007
        @JvmField val CMD_USE_COMPILED_GRAMMAR = 1008

        /* Extra Fields */

        @JvmField val EXTRA_CONFIGURATION_ID = "config_id"
    }

    constructor(source: Parcel) : this(
            source.readInt(),
            source.readParcelable<Bundle?>(Bundle::class.java.classLoader)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeInt(code)
        dest?.writeParcelable(extras, 0)
    }
}