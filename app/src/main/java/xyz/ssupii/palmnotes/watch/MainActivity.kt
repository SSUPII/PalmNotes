package xyz.ssupii.palmnotes.watch

import NotesAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.wear.widget.WearableLinearLayoutManager
import androidx.wear.widget.WearableRecyclerView
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var notes_list: WearableRecyclerView
    private lateinit var adapter: NotesAdapter

    private lateinit var new_note_button: ImageButton
    private lateinit var new_note_button_initial: Button

    private val newNoteButtonListener = View.OnClickListener {
        val intent = android.content.Intent(this, EditActivity::class.java)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Toast.makeText(this, "onCreate", Toast.LENGTH_SHORT).show()
        if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.P) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        new_note_button_initial = findViewById(R.id.new_note_button_initial)
        notes_list = findViewById(R.id.notes_list)

        notes_list.setLayoutManager(WearableLinearLayoutManager(this));
        adapter = NotesAdapter(mutableListOf())
        notes_list.adapter = adapter

        //TODO implement counting notes to dynamically exchange button
        new_note_button_initial.visibility = Button.VISIBLE

        new_note_button_initial.setOnClickListener(newNoteButtonListener)
    }

    override fun onResume() {
        Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show()

        super.onResume()

        val savedNotesFiles: Array<String> = fileList()

        if (savedNotesFiles.isNotEmpty()) {
            new_note_button = layoutInflater.inflate(R.layout.new_note_button_initial, null).findViewById(R.id.new_note_button)
            new_note_button.setOnClickListener(newNoteButtonListener)
            notes_list.visibility = WearableRecyclerView.VISIBLE
            new_note_button_initial.visibility = Button.GONE
            for (file in savedNotesFiles) {
                if (file.toLowerCase().endsWith(".txt")) {
                    readFile(file)?.let { noteData ->
                        // Create a Note object and add it to the adapter
                        adapter.addNote(Triple(noteData.first, noteData.second, noteData.third))
                    }
                }
            }
        }
    }

    private fun readFile(file: String): Triple<String, String, List<String>>? {

        var returnTriple: Triple<String, String, List<String>>? = null
        try {
            openFileInput(file).use { input ->
                val lines = input.bufferedReader().readLines()
                if (lines.isNotEmpty()) {
                    returnTriple = Triple(file, lines[0], lines.subList(1, lines.size))
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return returnTriple
    }

}
