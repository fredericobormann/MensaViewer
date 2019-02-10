package info.frederico.mensaviewer.helper

import java.util.*
import kotlin.collections.ArrayList

class Essensplan(val today: List<Essen>, val nextday: List<Essen>, val dateOfNextDay: Date){
    fun getViewableEssensplan(): List<ViewableEssenElement>{
        val viewableEssensplan: MutableList<ViewableEssenElement> = ArrayList()
        if (!today.isEmpty()){
            viewableEssensplan.add(ViewableDateElement("Heute"))
            viewableEssensplan.addAll(today)
        }
        if (!nextday.isEmpty()){
            viewableEssensplan.add(ViewableDateElement(dateOfNextDay.toString()))
        }
        return viewableEssensplan
    }
}