package cz.zcu.kky.asr.lib

import android.os.Bundle
import cz.zcu.kky.asr.lib.model.AsrConfiguration

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
object AsrConfigurations {

    val ASR_CONFIGURATIONS = "asr_configurations"

    fun toBundle(list: ArrayList<AsrConfiguration>): Bundle {
        val bundle = Bundle()
        bundle.putParcelableArrayList(ASR_CONFIGURATIONS, list)
        return bundle
    }

    fun fromBundle(bundle: Bundle): ArrayList<AsrConfiguration> {
        return bundle.getParcelableArrayList(ASR_CONFIGURATIONS)
    }

}