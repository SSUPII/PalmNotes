package xyz.ssupii.palmnotes.watch

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.InputDevice
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.wear.widget.WearableLinearLayoutManager
import androidx.wear.widget.WearableRecyclerView
import xyz.ssupii.palmnotes.watch.utils.Settings
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SettingsActivity : AppCompatActivity() {
    private lateinit var settingsWrapper: WearableRecyclerView
    private lateinit var settingsAdapter: SettingsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        settingsWrapper = findViewById(R.id.settings_wrapper)

        val settingsItems = listOf(
            SettingsItem.SettingLabel(getString(R.string.setting_main_category)),
            SettingsItem.NoteDetails(getString(
                R.string.note_filename_example),
                getString(R.string.note_title_example),
                getString(R.string.note_contents_example),
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(0)),
                Settings.get()
            ),
            SettingsItem.SettingRow(getString(R.string.setting_filename_hide), Settings.get(0).second, 0),
            SettingsItem.SettingRow(getString(R.string.setting_big_title), Settings.get(1).second, 1),
            SettingsItem.SettingRow(getString(R.string.setting_serif_title), Settings.get(2).second, 2),
            SettingsItem.SettingRow(getString(R.string.setting_contents_hide), Settings.get(3).second, 3),
            SettingsItem.SettingLabel(getString(R.string.setting_editor_category)),
            SettingsItem.SettingRow(getString(R.string.setting_editor_larger), Settings.get(4).second, 4)
        )

        settingsWrapper.setLayoutManager(WearableLinearLayoutManager(this))
        settingsAdapter = SettingsAdapter(settingsItems)
        settingsAdapter.onSettingRowClicked = { item, position, id ->
            item.isChecked = !item.isChecked

            Settings.set(id, item.isChecked)
            settingsAdapter.notifyItemChanged(position)
            if(position in 2..5)
                settingsAdapter.notifyItemChanged(1)

            val sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                putBoolean(Settings.get(id).first, Settings.get(id).second)
                apply()
            }
        }
        settingsWrapper.adapter = settingsAdapter

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
                    settingsWrapper.scrollBy(0, (-delta * SCROLL_FACTOR).toInt())
                    return true
                }
            }
        }
        return super.onGenericMotionEvent(event)
    }
}