package info.frederico.mensaviewer

import android.content.Context
import android.preference.PreferenceManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import kotlinx.android.synthetic.main.essenseintrag_view.view.*
import info.frederico.mensaviewer.helper.Essen
import info.frederico.mensaviewer.helper.Essensplan
import info.frederico.mensaviewer.helper.ViewableDateElement
import kotlinx.android.synthetic.main.datumseintrag_view.view.*

class EssenAdapter(private var essensplan: Essensplan, private val context: Context) :
RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class EssenViewHolder(val view: View) : RecyclerView.ViewHolder(view)
    class DateViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun getItemViewType(position: Int): Int {
        if (essensplan.getViewableEssensplan()[position] is Essen){
            return 0
        } else {
            return 1
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
        } else {
            val datumseintragView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.datumseintrag_view, parent, false)
            return DateViewHolder(datumseintragView)
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        val viewableEssensplan = essensplan.getViewableEssensplan()
        if(holder.itemViewType == 0){
            val essenViewHolder = holder as EssenViewHolder
            val currentEssen = viewableEssensplan[position] as Essen
            essenViewHolder.view.essensTextView.text = currentEssen.bezeichnung
            when(PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.pref_usergroup), context.getString(R.string.pref_usergroup_defaultValue))){
                context.getString(R.string.pref_usergroup_studentValue) -> {
                    essenViewHolder.view.preisTextView.text = currentEssen.studentenPreis
                }
                context.getString(R.string.pref_usergroup_employeeValue) -> {
                    essenViewHolder.view.preisTextView.text = currentEssen.bedienstetePreis
                }
            }
            if(position+1 == viewableEssensplan.size || viewableEssensplan[position+1] is ViewableDateElement){
                essenViewHolder.view.lineTextView.visibility = View.INVISIBLE
            } else{
                essenViewHolder.view.lineTextView.visibility = View.VISIBLE
            }
        } else if(holder.itemViewType == 1){
            val dateViewHolder = holder as DateViewHolder
            val currentDateElement = viewableEssensplan[position] as ViewableDateElement
            dateViewHolder.view.datumsTextView.text = currentDateElement.datestring
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = essensplan.getViewableEssensplan().size

    fun setEssensplan(newEssensplan : Essensplan){
        essensplan = newEssensplan
        notifyDataSetChanged()
    }
}