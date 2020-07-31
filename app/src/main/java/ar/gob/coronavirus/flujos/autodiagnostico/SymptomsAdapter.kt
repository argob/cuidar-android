package ar.gob.coronavirus.flujos.autodiagnostico

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ar.gob.coronavirus.R
import kotlinx.android.synthetic.main.symptom_view_holder.view.*

typealias SymptomClickListener = (Symptoms, selected: Boolean) -> Unit

data class SymptomElement(val symptoms: Symptoms, val value: Boolean)

class SymptomsAdapter(private val symptoms: List<SymptomElement>, private val clickListener: SymptomClickListener) : RecyclerView.Adapter<SymptomViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SymptomViewHolder {
        return SymptomViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.symptom_view_holder, parent, false))
    }

    override fun onBindViewHolder(holder: SymptomViewHolder, position: Int) {
        holder.bind(symptoms[position], clickListener)
    }

    override fun getItemCount(): Int = symptoms.size
}

class SymptomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(element: SymptomElement, clickListener: SymptomClickListener) {
        itemView.tv_symptom_title.setText(element.symptoms.title)
        itemView.radio_group_symptom.setOnCheckedChangeListener { _, checkedId ->
            clickListener(element.symptoms, checkedId == R.id.radio_symptom_yes)
        }
        itemView.radio_symptom_yes.isChecked = element.value
        itemView.radio_symptom_no.isChecked = !element.value
    }
}