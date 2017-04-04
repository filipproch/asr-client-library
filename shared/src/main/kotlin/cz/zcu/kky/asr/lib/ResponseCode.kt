package cz.zcu.kky.asr.lib

/**
 * Command Response codes
 */
object ResponseCode {

    /**
     * Command was executed successfully
     */
    val SUCCESS = 0

    /**
     * Command execution failed, check ResponseData.ERROR_MSG or ResponseData.ERROR_CODE
     */
    val FAILURE = 1

}