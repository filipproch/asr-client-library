package cz.zcu.kky.asrclientlibrary.event

import cz.zcu.kky.asr.lib.model.AsrGrammar

/**
 * TODO: add description
 *
 * @author Filip Prochazka (@filipproch)
 */
data class AsrGrammarsChangedEvent(val grammars: MutableList<AsrGrammar>?) : ServiceEvent