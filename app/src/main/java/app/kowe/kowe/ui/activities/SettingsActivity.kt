package app.kowe.kowe.ui.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SwitchCompat
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.RelativeLayout
import app.kowe.kowe.KoweSettings
import app.kowe.kowe.R
import app.kowe.kowe.bindView
import org.koin.android.ext.android.inject

class SettingsActivity : AppCompatActivity() {

    private val settings: KoweSettings by inject()

    private val enableAutoRecord by bindView<RelativeLayout>(R.id.btn_enable_auto_recording)
    private val makeRecordsPublic by bindView<RelativeLayout>(R.id.btn_make_public)
    private val enableSync by bindView<RelativeLayout>(R.id.btn_enable_sync)
    private val ghostMode by bindView<RelativeLayout>(R.id.btn_activate_ghost_mode)

    private val enableAutoRecordSwitch by bindView<SwitchCompat>(R.id.switch_enable_auto_recording)
    private val makePublicSwitch by bindView<SwitchCompat>(R.id.switch_make_records_public)
    private val enableSyncSwitch by bindView<SwitchCompat>(R.id.switch_enable_sync)
    private val ghostModeSwitch by bindView<SwitchCompat>(R.id.switch_activate_ghost_mode)

    private val toolbar by bindView<Toolbar>(R.id.toolbar_app_settings)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_app_settings)

        toolbar.title = "Settings"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        enableAutoRecord.setOnClickListener {

            enableAutoRecordSwitch.toggle()
            settings.enableAutoRecording(enableAutoRecordSwitch.isChecked)
        }

        makeRecordsPublic.setOnClickListener {

            makePublicSwitch.toggle()
            settings.setKeepRecordsPrivate(makePublicSwitch.isChecked)
        }

        enableSync.setOnClickListener {

            enableSyncSwitch.toggle()
            settings.setSyncRecords(enableSyncSwitch.isChecked)
        }

        ghostMode.setOnClickListener {

            if (ghostModeSwitch.isChecked) {
                settings.deActivateGhostMode()
            }else {
                settings.activateGhostMode()
            }

            ghostModeSwitch.toggle()
        }
    }

    override fun onResume() {
        super.onResume()

        renderSetting()
    }

    private fun renderSetting() {

        enableAutoRecordSwitch.isChecked = settings.isAutoRecordingEnabled()
        enableSyncSwitch.isChecked = settings.syncRecordsEnabled()
        makePublicSwitch.isChecked = settings.keepRecordsPrivate()
        ghostModeSwitch.isChecked = settings.isInGhostMode()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when(item?.itemId) {

            android.R.id.home -> {
                finish()
            }
        }

        return super.onOptionsItemSelected(item)
    }
}