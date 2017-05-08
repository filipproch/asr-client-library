package cz.zcu.kky.asrclientlibrary.api

import android.os.Bundle
import cz.zcu.kky.asr.lib.model.AsrCommandResponse
import cz.zcu.kky.asr.lib.model.AsrConfiguration

internal data class IncomingEvent(val code: Int, val data: Bundle)

data class Response(
        val inProgress: Boolean,
        val success: Boolean? = null,
        val error: Throwable? = null
)

data class AsrConfigurations(
        val waiting: Boolean,
        val configurations: List<AsrConfiguration>?
)

data class AsrGrammars(
        val waiting: Boolean,
        val configurations: List<AsrConfiguration>?
)

data class CommandResponse(
        val waitingForResponse: Boolean,
        val success: Boolean? = null,
        val error: Throwable? = null,
        val data: Bundle? = null
) {
    companion object {
        val WAITING = CommandResponse(true)

        fun fromError(error: Throwable?): CommandResponse {
            return CommandResponse(false, false, error)
        }

        fun fromResponse(response: AsrCommandResponse): CommandResponse {
            return CommandResponse(false, response.success, null, response.extras)
        }
    }
}