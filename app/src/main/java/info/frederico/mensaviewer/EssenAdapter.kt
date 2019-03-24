package info.frederico.mensaviewer

import android.content.Context
import android.preference.PreferenceManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.TextView
import info.frederico.mensaviewer.helper.*
import kotlinx.android.synthetic.main.essenseintrag_view.view.*
import kotlinx.android.synthetic.main.datumseintrag_view.view.*
import kotlinx.android.synthetic.main.texteintrag_view.view.*

class EssenAdapter(private var essensplan: Essensplan, private val context: Context) :
RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class EssenViewHolder(val view: View) : RecyclerView.ViewHolder(view)
    class DateViewHolder(val view: View) : RecyclerView.ViewHolder(view)
    class TexteintragViewHolder(val view: View) : RecyclerView.ViewHolder(view)
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun getVeggieFilterOption(): VeggieFilterOption{
        return VeggieFilterOption.values()[sharedPreferences.getInt(MensaViewer.res.getString(R.string.pref_filter),0)]
    }

    override fun getItemViewType(position: Int): Int {
        if (essensplan.getViewableEssensplan(getVeggieFilterOption())[position] is Essen){
            return 0
        } else if (essensplan.getViewableEssensplan((getVeggieFilterOption()))[position] is ViewableDateElement){
            return 1
        } else {
            return 2
        }
    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 0){
            // create a new view
            val essenseintragView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.essenseintrag_view, parent, false)
            // set the view's size, margins, paddings and layout parameters
            return EssenViewHolder(essenseintragView)
        } else if (viewType == 1){
            val datumseintragView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.datumseintrag_view, parent, false)
            return DateViewHolder(datumseintragView)
        } else {
            val texteintragView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.texteintrag_view, parent, false)
            return TexteintragViewHolder(texteintragView)
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        val viewableEssensplan = essensplan.getViewableEssensplan(getVeggieFilterOption())
        if(holder.itemViewType == 0){
            val essenViewHolder = holder as EssenViewHolder
            val currentEssen = viewableEssensplan[position] as Essen
            essenViewHolder.view.essensTextView.text = currentEssen.bezeichnung
            if(currentEssen.vegan){
                showVeganIcon(essenViewHolder)
            } else if(currentEssen.vegetarian) {
                showVegetarianIcon(essenViewHolder)
            } else {
                hideVeggieIcon(essenViewHolder)
            }
            when(PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.pref_usergroup), context.getString(R.string.pref_usergroup_defaultValue))){
                context.getString(R.string.pref_usergroup_studentValue) -> {
                    essenViewHolder.view.preisTextView.text = currentEssen.studentenPreis
                }
                context.getString(R.string.pref_usergroup_employeeValue) -> {
                    essenViewHolder.view.preisTextView.text = currentEssen.bedienstetePreis
                }
            }
            hideBottomLineIfNecessary(essenViewHolder.view.lineTextView, position, viewableEssensplan)
        } else if(holder.itemViewType == 1){
            val dateViewHolder = holder as DateViewHolder
            val currentDateElement = viewableEssensplan[position] as ViewableDateElement
            dateViewHolder.view.datumsTextView.text = currentDateElement.datestring
        } else {
            val textViewHolder = holder as TexteintragViewHolder
            val currentTextElement = viewableEssensplan[position] as ViewableTextElement
            textViewHolder.view.textTextView.text = currentTextElement.text
            hideBottomLineIfNecessary(textViewHolder.view.textViewBottomLine, position, viewableEssensplan)
        }
    }

    fun hideBottomLineIfNecessary(lineTextView: TextView, position: Int, viewableEssensplan: List<ViewableEssenElement>){
        if(position+1 == viewableEssensplan.size || viewableEssensplan[position+1] is ViewableDateElement){
            lineTextView.visibility = View.INVISIBLE
        } else{
            lineTextView.visibility = View.VISIBLE
        }
    }

    fun showVegetarianIcon(essenViewHolder: EssenViewHolder){
        essenViewHolder.view.veggieImageView.setImageResource(R.drawable.ic_vegetarian)
        essenViewHolder.view.veggieImageView.visibility = View.VISIBLE
    }

    fun showVeganIcon(essenViewHolder: EssenViewHolder){
        essenViewHolder.view.veggieImageView.setImageResource(R.drawable.ic_vegan)
        essenViewHolder.view.veggieImageView.visibility = View.VISIBLE
    }

    fun hideVeggieIcon(essenViewHolder: EssenViewHolder){
        essenViewHolder.view.veggieImageView.visibility = View.INVISIBLE
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = essensplan.getViewableEssensplan(getVeggieFilterOption()).size

    fun setEssensplan(newEssensplan : Essensplan){
        essensplan = newEssensplan
        notifyDataSetChanged()
    }
}