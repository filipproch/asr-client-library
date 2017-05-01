package cz.zcu.kky.asr.lib

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
object AsrCommand {

    /* Command Codes */

    @JvmField val CMD_INIT_SESSION = 1000
    @JvmField val CMD_TERMINATE_SESSION = 1001

    @JvmField val CMD_TAKE_CONTROL = 2000
    @JvmField val CMD_RELEASE_CONTROL = 2001

    @JvmField val CMD_ASR_CONTROL = 3000

    /* Command Fields */

    @JvmField val FIELD_CLIENT_ID = "client_id"
    @JvmField val FIELD_COMMAND_ID = "command_id"

    @JvmField val FIELD_ASR_COMMAND = "asr_command"

}