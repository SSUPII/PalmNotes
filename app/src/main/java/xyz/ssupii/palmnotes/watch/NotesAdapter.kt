import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import xyz.ssupii.palmnotes.watch.R

class NotesAdapter(private val notes: MutableList<Triple<String, String, List<String>>>) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

// ViewHolder holds references to the item views
inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val fileText: TextView = itemView.findViewById(R.id.note_title)
    val titleText: TextView = itemView.findViewById(R.id.note_title)
    val linesPreviewText: TextView = itemView.findViewById(R.id.note_lines_preview)
}

// Inflate the item layout and create the ViewHolder
override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.note_element, parent, false)
    return NoteViewHolder(view)
}

// Bind data to the views in each item
override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
    val note = notes[position]
    holder.fileText.text = note.first
    holder.titleText.text = note.second.getOrNull(0)?.toString() ?: ""
    holder.linesPreviewText.text = note.third.getOrNull(1) ?: ""
    Log.println(Log.DEBUG, "File", note.toString())
}

// Return the total number of items
override fun getItemCount(): Int = notes.size

// Optional: Method to add a new note dynamically
fun addNote(note: Triple<String, String, List<String>>) {
    notes.add(note)
    notifyItemInserted(notes.size - 1)
}
}
