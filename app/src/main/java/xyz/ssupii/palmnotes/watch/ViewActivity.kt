package xyz.ssupii.palmnotes.watch

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.InputDevice
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import xyz.ssupii.palmnotes.watch.utils.LocalFile
import xyz.ssupii.palmnotes.watch.utils.Quadruple
import java.io.File

class ViewActivity: AppCompatActivity() {
    private lateinit var filename: String

    private lateinit var titleView: TextView
    private lateinit var noteView: TextView
    private lateinit var deleteButton: Button
    private lateinit var scroll: ScrollView

    private lateinit var contents: Quadruple<String, String, List<String>, String>
    private lateinit var file: LocalFile

    private val onDeleteButtonClicked = View.OnClickListener { view ->
        if (deleteButton.text == view.context.getString(R.string.delete)){
            deleteButton.text = view.context.getString(R.string.sure_delete)
            val drawable = AppCompatResources.getDrawable(view.context, R.drawable.trash_cross_icon)
            drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
            deleteButton.setCompoundDrawables(drawable, null, null, null)
        }else{
            val f = File(view.context.filesDir, file.getFilename())
            if (f.exists()) {
                if (f.delete()) {
                    finish()
                }else{
                    Log.println(Log.ERROR, "File operation", "Delete "+file.getFilename()+" failed")
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view)

        filename = intent.getStringExtra("filename") ?: throw IllegalStateException("A file must be provided to this activity.")
        file = LocalFile(this, filename)
        contents = file.readFile()!!

        titleView = findViewById(R.id.view_note_title)
        noteView = findViewById(R.id.view_note_contents)
        titleView.text = contents.second
        noteView.text = contents.third.joinToString("\n")

        //TODO edit button

        deleteButton = findViewById(R.id.delete_button)
        deleteButton.setOnClickListener(onDeleteButtonClicked)

        scroll = findViewById(R.id.view_note_scroll)
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
                    scroll.scrollBy(0, (-delta * SCROLL_FACTOR).toInt())
                    return true
                }
            }
        }
        return super.onGenericMotionEvent(event)
    }
}