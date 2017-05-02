package cz.zcu.kky.asr.lib

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
object AsrEvent {

    /* Event Codes */

    @JvmField val EVENT_COMMAND_RESPONSE = 1000

    @JvmField val EVENT_ASR_ENGINE_STATE_CHANGED = 2000
    @JvmField val EVENT_ASR_ENGINE_RESULT = 2001
    @JvmField val EVENT_ASR_ENGINE_ENERGY_LEVEL_CHANGED = 2002
    @JvmField val EVENT_ASR_CONFIGURATIONS_CHANGED = 2003
    @JvmField val EVENT_ASR_GRAMMARS_CHANGED = 2004

    /* Event Fields */

    @JvmField val FIELD_RESPONSE_OBJECT = "response_object"

}