package info.frederico.mensaviewer.helper

import info.frederico.mensaviewer.MensaViewer
import info.frederico.mensaviewer.R
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList

class Essensplan(private val today: List<Essen>? = ArrayList(), private val nextday: List<Essen>? = ArrayList()){
    private val jsonDateFormat = SimpleDateFormat("yyyy-MM-dd")
    private val outputDateFormat = SimpleDateFormat("EEEE, d. MMMM")
    private val outputDateFormatWithoutWeekday = SimpleDateFormat("d. MMMM")

    fun getViewableEssensplan(veggieFilterOption: VeggieFilterOption): List<ViewableEssenElement>{
        val viewableEssensplan: MutableList<ViewableEssenElement> = ArrayList()
        if (today != null && today.isNotEmpty()){
            val dateOfToday = jsonDateFormat.parse(today[0].date)
            viewableEssensplan.add(ViewableDateElement(MensaViewer.res.getString(R.string.today) + ", " + outputDateFormatWithoutWeekday.format(dateOfToday)))
            val todayFiltered = today.filter { essen -> checkIfEssenMatchesFilter(essen, veggieFilterOption) }
            viewableEssensplan.addAll(todayFiltered)
            if(todayFiltered.isEmpty()){
                viewableEssensplan.add(ViewableTextElement(MensaViewer.res.getString(R.string.no_result_filter_message)))
            }
        }
        if (nextday != null && nextday.isNotEmpty()){
            val dateOfNextDay = jsonDateFormat.parse(nextday[0].date)
            viewableEssensplan.add(ViewableDateElement(outputDateFormat.format(dateOfNextDay)))
            val nextdayFiltered = nextday.filter { essen -> checkIfEssenMatchesFilter(essen, veggieFilterOption) }
            viewableEssensplan.addAll(nextdayFiltered)
            if(nextdayFiltered.isEmpty()){
                viewableEssensplan.add(ViewableTextElement(MensaViewer.res.getString(R.string.no_result_filter_message)))
            }
        }
        return viewableEssensplan
    }

    private fun checkIfEssenMatchesFilter(essen: Essen, veggieFilterOption: VeggieFilterOption): Boolean {
        return (veggieFilterOption == VeggieFilterOption.SHOW_ALL_DISHES)
                || (veggieFilterOption == VeggieFilterOption.SHOW_ONLY_VEGETARIAN && (essen.vegetarian || essen.vegan))
                || (veggieFilterOption == VeggieFilterOption.SHOW_ONLY_VEGAN && essen.vegan)
    }

    fun isEmpty(): Boolean{
        return today?.isEmpty() ?: true && nextday?.isEmpty() ?: true
    }
}