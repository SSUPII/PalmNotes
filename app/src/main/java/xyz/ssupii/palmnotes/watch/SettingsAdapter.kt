package xyz.ssupii.palmnotes.watch

import android.graphics.Typeface
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

sealed class SettingsItem {
    data class NoteDetails(val filename: String, val title: String, val preview: String, val date: String, val settings: List<Pair<String, Boolean>>) : SettingsItem()
    data class SettingRow(val label: String, var isChecked: Boolean) : SettingsItem()
    data class SettingLabel(val label: String) : SettingsItem()
}

class SettingsAdapter(private val items: List<SettingsItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onSettingRowClicked: ((SettingsItem.SettingRow, Int) -> Unit)? = null

    companion object {
        private const val TYPE_NOTE_DETAILS = 0
        private const val TYPE_SETTING_ROW = 1
        private const val TYPE_SETTING_LABEL = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is SettingsItem.NoteDetails -> TYPE_NOTE_DETAILS
            is SettingsItem.SettingRow -> TYPE_SETTING_ROW
            is SettingsItem.SettingLabel -> TYPE_SETTING_LABEL
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_NOTE_DETAILS -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.note_element, parent, false)
                NoteDetailsViewHolder(view)
            }
            TYPE_SETTING_ROW -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.setting_row_item, parent, false)
                SettingRowViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.setting_row_label, parent, false)
                SettingLabelViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is NoteDetailsViewHolder -> {
                val item = items[position] as SettingsItem.NoteDetails
                holder.bind(item)
            }
            is SettingRowViewHolder -> {
                val item = items[position] as SettingsItem.SettingRow
                holder.bind(item)
                holder.itemView.setOnClickListener {
                    onSettingRowClicked?.invoke(item, position)
                }
            }
            is SettingLabelViewHolder -> {
                val item = items[position] as SettingsItem.SettingLabel
                holder.bind(item)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    class NoteDetailsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val filenameTextView: TextView = itemView.findViewById(R.id.note_filename)
        private val titleTextView: TextView = itemView.findViewById(R.id.note_title)
        private val previewTextView: TextView = itemView.findViewById(R.id.note_lines_preview)
        private val dateTextView: TextView = itemView.findViewById(R.id.note_last_modified)

        fun bind(item: SettingsItem.NoteDetails) {
            filenameTextView.text = item.filename
            titleTextView.text = item.title
            previewTextView.text = item.preview
            dateTextView.text = item.date

            itemView.isClickable = false

            //Hide filename
            if(item.settings[0].second){
                filenameTextView.visibility = TextView.GONE
            }else{
                filenameTextView.visibility = TextView.VISIBLE
            }
            //Big title
            val defaultSizePx = itemView.context.resources.getDimension(R.dimen.default_title_text_size) // returns px
            if (item.settings[1].second) {
                titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, defaultSizePx * 1.3f)
            } else {
                titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, defaultSizePx)
            }
            //Serif title
            if(item.settings[2].second){
                titleTextView.typeface = Typeface.create("serif", Typeface.NORMAL)
            }else{
                titleTextView.typeface = Typeface.create("sans-serif", Typeface.NORMAL)
            }
            //Hide contents preview
            if(item.settings[3].second){
                previewTextView.visibility = TextView.GONE
            }else{
                previewTextView.visibility = TextView.VISIBLE
            }
        }
    }

    class SettingRowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkBox: CheckBox = itemView.findViewById(R.id.setting_checkbox)
        private val label: TextView = itemView.findViewById(R.id.setting_label)

        fun bind(item: SettingsItem.SettingRow) {
            checkBox.isChecked = item.isChecked
            label.text = item.label
        }
    }

    class SettingLabelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val label: TextView = itemView.findViewById(R.id.setting_group_label)

        fun bind(item: SettingsItem.SettingLabel) {
            label.text = item.label
        }
    }
}
