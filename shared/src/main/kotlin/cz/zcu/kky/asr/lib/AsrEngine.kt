package cz.zcu.kky.asr.lib

import android.os.Bundle

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
object AsrEngine {

    val ENGINE_ID = "engine"
    val ENGINE_STATE = "engine_state"
    val ENGINE_ENERGY = "engine_energy"

    fun createEnergyBundle(engineId: String, energyValue: Float): Bundle {
        val bundle = Bundle()
        bundle.putString(ENGINE_ID, engineId)
        bundle.putFloat(ENGINE_ENERGY, energyValue)
        return bundle
    }

    fun parseEnergy(bundle: Bundle): EngineData<Float> {
        return EngineData(bundle.getString(ENGINE_ID),
                bundle.getFloat(ENGINE_ENERGY))
    }

    fun createStateBundle(engineId: String, state: Int): Bundle {
        val bundle = Bundle()
        bundle.putString(ENGINE_ID, engineId)
        bundle.putInt(ENGINE_STATE, state)
        return bundle
    }

    fun parseState(bundle: Bundle): EngineData<Int> {
        return EngineData(bundle.getString(ENGINE_ID),
                bundle.getInt(ENGINE_STATE))
    }

    fun createResultBundle(engineId: String, result: String, confidence: Float = -1f, rejected: Boolean = false): Bundle {
        val bundle = Bundle()
        bundle.putString(ENGINE_ID, engineId)
        bundle.putString(ResponseData.RESULT_TEXT, result)
        bundle.putFloat(ResponseData.RESULT_CONFIDENCE, confidence)
        bundle.putBoolean(ResponseData.RESULT_REJECTED, rejected)
        return bundle
    }

    data class EngineData<out T>(val engineId: String, val data: T)

    data class EngineResult(val engineId: String, val text: String, val confidence: Float, val rejected: Boolean)

}