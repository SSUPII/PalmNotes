package xyz.ssupii.palmnotes.watch

import android.graphics.Typeface
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import xyz.ssupii.palmnotes.watch.utils.Quadruple
import xyz.ssupii.palmnotes.watch.utils.Settings

class NotesAdapter(private val notes: MutableList<Quadruple<String, String, List<String>, String>>, private val onHeaderClickListener: View.OnClickListener,
                   private val onFooterClickListener: View.OnClickListener, private val onNoteClickListener: View.OnClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_NEW_NOTE_BUTTON = 0
        private const val VIEW_TYPE_NOTE = 1
        private const val VIEW_TYPE_SETTINGS_BUTTON = 2
    }

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val headerButton: ImageButton = itemView.findViewById(R.id.new_note_button)
    }

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val layout: LinearLayout = itemView.findViewById(R.id.note_layout)
        val fileText: TextView = itemView.findViewById(R.id.note_filename)
        val titleText: TextView = itemView.findViewById(R.id.note_title)
        val linesPreviewText: TextView = itemView.findViewById(R.id.note_lines_preview)
        val lastModified: TextView = itemView.findViewById(R.id.note_last_modified)
    }

    class FooterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val footerButton: ImageButton = itemView.findViewById(R.id.settings_button)
    }


    // Inflate the item layout and create the ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_NEW_NOTE_BUTTON -> {
                val headerView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.new_note_button_small, parent, false)
                HeaderViewHolder(headerView)
            }

            VIEW_TYPE_SETTINGS_BUTTON -> {
                val footerView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.settings_button_small, parent, false)
                FooterViewHolder(footerView)
            }
            else -> {
                // Regular note item
                val noteView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.note_element, parent, false)
                NoteViewHolder(noteView)
            }
        }
    }


    // Bind data to the views in each item
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> {
                holder.headerButton.setOnClickListener(onHeaderClickListener)
            }
            is FooterViewHolder -> {
                holder.footerButton.setOnClickListener(onFooterClickListener)
            }
            is NoteViewHolder -> {
                // Because position=0 is header, note #0 is actually at
                // position-1 in the notes list
                val notePosition = position - 1
                val note = notes[notePosition]
                holder.fileText.text = note.first
                holder.titleText.text = note.second
                holder.linesPreviewText.text = note.third.joinToString(" ")
                holder.lastModified.text = note.fourth

                //Hide filename
                if(Settings.get(0).second){
                    holder.fileText.visibility = TextView.GONE
                }else{
                    holder.fileText.visibility = TextView.VISIBLE
                }
                //Big title
                val defaultSizePx = holder.itemView.context.resources.getDimension(R.dimen.default_title_text_size) // returns px
                if (Settings.get(1).second) {
                    holder.titleText.setTextSize(TypedValue.COMPLEX_UNIT_PX, defaultSizePx * 1.3f)
                } else {
                    holder.titleText.setTextSize(TypedValue.COMPLEX_UNIT_PX, defaultSizePx)
                }
                //Serif title
                if(Settings.get(2).second){
                    holder.titleText.typeface = Typeface.create("serif", Typeface.NORMAL)
                }else{
                    holder.titleText.typeface = Typeface.create("sans-serif", Typeface.NORMAL)
                }
                //Hide contents preview
                if(Settings.get(3).second){
                    holder.linesPreviewText.visibility = TextView.GONE
                }else{
                    holder.linesPreviewText.visibility = TextView.VISIBLE
                }

                holder.layout.setOnClickListener(onNoteClickListener)
            }
        }
    }


    // Return the total number of items
    override fun getItemCount(): Int = notes.size + 2

    override fun getItemViewType(position: Int): Int {
        // If the first item: header
        if (position == 0) {
            return VIEW_TYPE_NEW_NOTE_BUTTON
        }
        // If the last item: footer
        else if (position == itemCount - 1) {
            return VIEW_TYPE_SETTINGS_BUTTON
        }
        // Otherwise: it's a note
        return VIEW_TYPE_NOTE
    }

    fun addNote(note: Quadruple<String, String, List<String>, String>) {
        notes.add(note)
        notifyItemInserted(notes.size - 1)
    }

    fun changesComing(){
        notes.clear()
        notifyDataSetChanged() //TODO Change with more specific events
    }
}
