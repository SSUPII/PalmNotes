package xyz.ssupii.palmnotes.watch

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException

class EditActivity : AppCompatActivity() {
    private lateinit var note_layout: LinearLayout
    private lateinit var note_title: EditText
    private lateinit var note_line_initial: EditText
    private lateinit var save_button: Button

    private val note_lines = mutableListOf<EditText>()
    private var textWatcher: TextWatcher? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        note_layout = findViewById(R.id.note_layout)
        note_title = findViewById(R.id.note_title)
        note_line_initial = findViewById(R.id.note_line)
        save_button = findViewById(R.id.save_button)

        note_lines.add(note_line_initial)

        createTextEditListener()
        note_line_initial.addTextChangedListener(textWatcher)

        save_button.setOnClickListener {
            saveFile()
            finish()
        }
    }

    private fun saveFile(){
        val noteTitle = note_title.text.toString()
        val noteLines = note_lines.map { it.text.toString() }
        var finalFile: String = ""

        finalFile += "$noteTitle\n"
        for (line in noteLines) {
            finalFile += "$line\n"
        }

        val fileIdentifier = (1..999999999).random()

        try {
            openFileOutput("$fileIdentifier.txt", Context.MODE_PRIVATE).use { output ->
                output.write(
                    finalFile.toByteArray()
                )
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun createTextEditListener() {
        textWatcher = object : TextWatcher {
            @SuppressLint("ObsoleteSdkInt")
            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty()) {
                    //Clone the base line
                    val clonedEditText = EditText(note_line_initial.context).apply {
                        layoutParams = note_line_initial.layoutParams
                        inputType = note_line_initial.inputType
                        textAlignment = note_line_initial.textAlignment
                        typeface = note_line_initial.typeface
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            setAutofillHints(*note_line_initial.autofillHints)
                        }
                        setText("")
                    }
                    //Workaround for inconsistent text size
                    clonedEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, note_line_initial.textSize)

                    clonedEditText.addTextChangedListener(textWatcher)

                    //Remove the action from the last line
                    removeTextEditListener(note_lines[note_lines.lastIndex])

                    //Add the new line
                    note_lines.add(clonedEditText)
                    note_layout.addView(clonedEditText)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No action needed
            }
        }
    }

    private fun removeTextEditListener(editText: EditText) {
        editText.removeTextChangedListener(textWatcher)
    }
}


