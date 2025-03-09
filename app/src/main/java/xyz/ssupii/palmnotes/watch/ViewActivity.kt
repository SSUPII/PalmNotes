package xyz.ssupii.palmnotes.watch

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
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

        deleteButton = findViewById(R.id.delete_button)
        deleteButton.setOnClickListener(onDeleteButtonClicked)
    }
}