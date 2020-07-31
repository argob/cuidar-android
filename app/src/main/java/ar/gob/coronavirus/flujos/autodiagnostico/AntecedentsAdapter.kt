package ar.gob.coronavirus.flujos.autodiagnostico

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ar.gob.coronavirus.R
import kotlinx.android.synthetic.main.antecedent_view_holder.view.*

typealias AntecedentClickListener = (Antecedents, value: Boolean) -> Unit

data class AntecedentElement(val antecedent: Antecedents, val value: Boolean)

class AntecedentsAdapter(private val antecedents: List<AntecedentElement>,
                         private val clickListener: AntecedentClickListener) : RecyclerView.Adapter<AntecedentViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AntecedentViewHolder {
        return AntecedentViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.antecedent_view_holder, parent, false))
    }

    override fun onBindViewHolder(holder: AntecedentViewHolder, position: Int) {
        holder.bind(antecedents[position], clickListener)
    }

    override fun getItemCount(): Int = antecedents.size
}

class AntecedentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(element: AntecedentElement, clickListener: AntecedentClickListener) {
        itemView.antecedent_checkbox.setText(element.antecedent.title)
        itemView.antecedent_checkbox.setOnCheckedChangeListener { _, isChecked ->
            clickListener(element.antecedent, isChecked)
        }
        itemView.antecedent_checkbox.isChecked = element.value
    }
}