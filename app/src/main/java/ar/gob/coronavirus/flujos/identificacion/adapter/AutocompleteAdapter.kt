package ar.gob.coronavirus.flujos.identificacion.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.annotation.LayoutRes
import ar.gob.coronavirus.utils.strings.normalizeToUnicode

class AutocompleteAdapter<T>(
        context: Context,
        @LayoutRes private val layoutResource: Int,
        private val list: List<T>
) : ArrayAdapter<T>(context, layoutResource, list), Filterable {

    private var listFiltered: List<T> = list

    var autoCompleteHasFocus = false
        set(value) {
            field = value
        }

    var positionSelected = 0
        get() = field
        set(value) {
            field = value
        }

    fun getValues(): List<T> {
        return list;
    }

    fun getValueSelected(): T? {
        return if (positionSelected > -1) list.get(positionSelected) else null
    }

    fun getPositionFromValue(value: Any): Int {
        return list.map { it.toString().normalizeToUnicode() }.indexOf(value.toString().normalizeToUnicode())
    }

    fun restartList() {
        listFiltered = list
    }

    override fun getCount(): Int {
        return listFiltered.size
    }

    override fun getItem(p0: Int): T? {
        return listFiltered.get(p0)
    }

    override fun getItemId(position: Int): Long {
        return list.indexOf(listFiltered.get(position)).toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: TextView = convertView as TextView? ?: LayoutInflater.from(context)
                .inflate(layoutResource, parent, false) as TextView
        view.setText(listFiltered.get(position).toString())
        return view
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            var filterResults = FilterResults()

            override fun performFiltering(constraint: CharSequence?): FilterResults {

                if (autoCompleteHasFocus) {
                    val queryString = constraint?.normalizeToUnicode()

                    if (queryString.isNullOrEmpty()) {
                        filterResults.values = list
                    } else {
                        filterResults.values = list.filter {
                            it.toString().normalizeToUnicode().contains(queryString)
                        }
                    }
                } else {
                    filterResults.values = list
                }

                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults) {
                listFiltered = results.values as List<T>
                notifyDataSetChanged()
            }
        }
    }
}