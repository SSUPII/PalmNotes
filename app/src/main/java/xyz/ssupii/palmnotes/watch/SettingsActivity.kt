package xyz.ssupii.palmnotes.watch

import android.content.Context
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

        val settingsItems = listOf(
            SettingsItem.SettingLabel(getString(R.string.setting_main_category)),
            SettingsItem.NoteDetails(getString(R.string.note_filename_example), getString(R.string.note_title_example), getString(R.string.note_contents_example), Settings.get()),
            SettingsItem.SettingRow(getString(R.string.setting_filename_hide), Settings.get(0).second),
            SettingsItem.SettingRow(getString(R.string.setting_big_title), Settings.get(1).second),
            SettingsItem.SettingRow(getString(R.string.setting_serif_title), Settings.get(2).second),
            SettingsItem.SettingRow(getString(R.string.setting_contents_hide), Settings.get(3).second),
            SettingsItem.SettingLabel(getString(R.string.setting_editor_category)),
            SettingsItem.SettingRow(getString(R.string.setting_editor_larger), true) //TODO
        )

        settingsWrapper.setLayoutManager(WearableLinearLayoutManager(this))
        settingsAdapter = SettingsAdapter(settingsItems)
        settingsAdapter.onSettingRowClicked = { item, position ->
            item.isChecked = !item.isChecked
            if(position in 2..5)
                Settings.set(position-2, item.isChecked)
            settingsAdapter.notifyItemChanged(position)
            settingsAdapter.notifyItemChanged(1)

            val sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                for (i in 0 until Settings.size()) {
                    putBoolean(Settings.get(i).first, Settings.get(i).second)
                }
                apply()
            }
        }
        settingsWrapper.adapter = settingsAdapter

    }
}