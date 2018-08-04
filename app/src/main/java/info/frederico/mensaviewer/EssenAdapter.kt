package info.frederico.mensaviewer

import android.content.Context
import android.preference.PreferenceManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import kotlinx.android.synthetic.main.essenseintrag_view.view.*
import info.frederico.mensaviewer.helper.Essen

class EssenAdapter(private var essensplan: List<Essen>, private val context: Context) :
RecyclerView.Adapter<EssenAdapter.ViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): EssenAdapter.ViewHolder {
        // create a new view
        val textView = LayoutInflater.from(parent.context)
                .inflate(R.layout.essenseintrag_view, parent, false)
        // set the view's size, margins, paddings and layout parameters
        return ViewHolder(textView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.view.essensTextView.text = essensplan[position].bezeichnung
        when(PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.pref_usergroup), context.getString(R.string.pref_usergroup_defaultValue))){
            context.getString(R.string.pref_usergroup_studentValue) -> {
                holder.view.preisTextView.text = essensplan[position].studentenPreis
            }
            context.getString(R.string.pref_usergroup_employeeValue) -> {
                holder.view.preisTextView.text = essensplan[position].bedienstetePreis
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = essensplan.size

    fun setEssensplan(newEssensplan : List<Essen>){
        essensplan = newEssensplan
        notifyDataSetChanged()
    }
}