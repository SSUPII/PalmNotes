package xyz.ssupii.palmnotes.watch

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.wear.widget.WearableLinearLayoutManager
import androidx.wear.widget.WearableRecyclerView

class SettingsActivity : AppCompatActivity() {
    private lateinit var settingsWrapper: WearableRecyclerView
    private lateinit var settingsAdapter: SettingsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        settingsWrapper = findViewById(R.id.settings_wrapper)

        val exampleNoteSettings = mutableListOf(
            true, true, true, true
        )
        val settingsItems = listOf(
            SettingsItem.SettingLabel(getString(R.string.setting_main_category)),
            SettingsItem.NoteDetails(getString(R.string.note_filename_example), getString(R.string.note_title_example), getString(R.string.note_contents_example), exampleNoteSettings),
            SettingsItem.SettingRow(getString(R.string.setting_filename_hide), exampleNoteSettings[0]),
            SettingsItem.SettingRow(getString(R.string.setting_big_title), exampleNoteSettings[1]),
            SettingsItem.SettingRow(getString(R.string.setting_serif_title), exampleNoteSettings[2]),
            SettingsItem.SettingRow(getString(R.string.setting_contents_hide), exampleNoteSettings[3]),
            SettingsItem.SettingLabel(getString(R.string.setting_editor_category)),
            SettingsItem.SettingRow(getString(R.string.setting_editor_larger), true) //TODO
        )

        settingsWrapper.setLayoutManager(WearableLinearLayoutManager(this))
        settingsAdapter = SettingsAdapter(settingsItems)
        settingsAdapter.onSettingRowClicked = { item, position ->
            item.isChecked = !item.isChecked
            if(position in 2..5)
                exampleNoteSettings[position-2] = item.isChecked
            settingsAdapter.notifyItemChanged(position)
            settingsAdapter.notifyItemChanged(1)
        }
        settingsWrapper.adapter = settingsAdapter

        //DEBUG
        (settingsItems[2] as SettingsItem.SettingRow).isChecked = true
        settingsAdapter.notifyItemChanged(2)

    }
}