package info.frederico.mensaviewer.helper

data class Essen(
        val bezeichnung: String,
        val allergene: List<String>,
        val studentenPreis: String,
        val bedienstetePreis: String,
        val gaestePreis: String)