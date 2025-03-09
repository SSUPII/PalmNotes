package xyz.ssupii.palmnotes.watch

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.InputDevice
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.wear.widget.WearableLinearLayoutManager
import androidx.wear.widget.WearableRecyclerView
import java.io.IOException
import java.util.Locale
import xyz.ssupii.palmnotes.watch.utils.Settings
import xyz.ssupii.palmnotes.watch.utils.Quadruple
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

const val PREFS_NAME = "PalmNotesSettings"

class MainActivity : AppCompatActivity() {

    private lateinit var notes_list: WearableRecyclerView
    private lateinit var adapter: NotesAdapter

    private lateinit var new_note_button: ImageButton
    private lateinit var new_note_button_initial: Button

    private val newNoteButtonListener = View.OnClickListener {
        val intent = Intent(this, EditActivity::class.java)
        startActivity(intent)
    }
    private val settingsButtonListener = View.OnClickListener {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }
    private val noteElementListener = View.OnClickListener { view ->
        val noteFilenameTextView = view.findViewById<TextView>(R.id.note_filename)
        val filename = noteFilenameTextView.text.toString()

        val intent = Intent(this, ViewActivity::class.java)
        intent.putExtra("filename", filename)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //Get settings
        val sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            for (i in 0 until Settings.size()) {
                Settings.set(
                    i, sharedPref.getBoolean(Settings.get(i).first, Settings.get(i).second)
                )
            }
            apply()
        }

        // Night mode fix for WearOS 2 and older
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        new_note_button_initial = findViewById(R.id.new_note_button_initial)
        notes_list = findViewById(R.id.notes_list)

        notes_list.setLayoutManager(WearableLinearLayoutManager(this))
        adapter = NotesAdapter(
            mutableListOf(), newNoteButtonListener, settingsButtonListener, noteElementListener
        )
        notes_list.adapter = adapter

        new_note_button_initial.visibility = Button.VISIBLE
        new_note_button_initial.setOnClickListener(newNoteButtonListener)
    }

    override fun onResume() {
        //Write any settings changes
        val sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            for (i in 0 until Settings.size()) {
                putBoolean(Settings.get(i).first, Settings.get(i).second)
            }
            apply()
        }

        super.onResume()

        val savedNotesFiles: Array<String> = fileList()
        val savedTxtFiles: MutableList<String> = savedNotesFiles.toMutableList()
        val iterator = savedTxtFiles.iterator()
        while (iterator.hasNext()) {
            val file = iterator.next()
            if (!file.lowercase(Locale.ROOT).endsWith(".txt")) {
                iterator.remove()
            }
        }
        var filesFound: Boolean = false

        Log.println(Log.DEBUG, "data", savedTxtFiles.toString())
        adapter.changesComing() //TODO See function implementation
        if (savedTxtFiles.isNotEmpty()) {
            //TODO Add hiding initial big settings button
            for (file in savedTxtFiles) {
                readFile(file)?.let { noteData ->
                    // Create a Note object and add it to the adapter
                    adapter.addNote(Quadruple(noteData.first, noteData.second, noteData.third, noteData.fourth))
                }
                filesFound = true
            }
        }
        if(filesFound) {notes_list.visibility = WearableRecyclerView.VISIBLE; new_note_button_initial.visibility = Button.GONE}
        else{notes_list.visibility = WearableRecyclerView.GONE; new_note_button_initial.visibility = Button.VISIBLE}
    }

    private fun readFile(file: String): Quadruple<String, String, List<String>, String>? {

        var returnObj: Quadruple<String, String, List<String>, String>? = null
        try {
            openFileInput(file).use { input ->
                val lines = input.bufferedReader().readLines()
                if (lines.isNotEmpty()) {
                    val lastModifiedMillis = File(applicationContext.filesDir, file).lastModified()
                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) //TODO allow selection
                    returnObj = Quadruple(file, lines[0], lines.subList(1, lines.size), sdf.format(Date(lastModifiedMillis)))
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return returnObj
    }

    override fun onGenericMotionEvent(event: MotionEvent?): Boolean {
        //API 26+ only. Scroll via disc/knob
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            event?.let {
                if (it.action == MotionEvent.ACTION_SCROLL &&
                    it.source and InputDevice.SOURCE_ROTARY_ENCODER != 0) {

                    // Get the scroll delta (the amount of rotation)
                    val delta = it.getAxisValue(MotionEvent.AXIS_SCROLL)
                    // TODO Adjust SCROLL_FACTOR to control the scroll speed based on setting
                    val SCROLL_FACTOR = 90
                    notes_list.scrollBy(0, (-delta * SCROLL_FACTOR).toInt())
                    return true
                }
            }
        }
        return super.onGenericMotionEvent(event)
    }


}
