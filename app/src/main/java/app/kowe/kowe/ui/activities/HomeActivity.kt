package app.kowe.kowe.ui.activities

import android.Manifest
import android.arch.lifecycle.Observer
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import app.kowe.kowe.L
import app.kowe.kowe.R
import app.kowe.kowe.Util
import app.kowe.kowe.bindView
import app.kowe.kowe.data.models.Record
import app.kowe.kowe.ui.PlayerDialog
import app.kowe.kowe.ui.adapters.RecordsListAdapter
import app.kowe.kowe.ui.viewmodels.HomeViewModels
import net.steamcrafted.materialiconlib.MaterialIconView
import org.koin.android.viewmodel.ext.android.viewModel

class HomeActivity: AppCompatActivity() {

    private val swipeRefreshLayout by bindView<SwipeRefreshLayout>(R.id.swipe_layout_main)
    private val recordsRecyclerView by bindView<RecyclerView>(R.id.rv_recordings_)
    private val emptyDataLayout by bindView<LinearLayout>(R.id.layout_no_records)
    private val settingsButton by bindView<MaterialIconView>(R.id.btn_settings_activity_main)

    private val homeViewModel: HomeViewModels by viewModel()

    private val records = ArrayList<Record>()
    private lateinit var recordsListAdapter: RecordsListAdapter

    private val PERMISSIONS = arrayOf(Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_CONTACTS, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.PROCESS_OUTGOING_CALLS)

    private val PERMISSION_RC = 11


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_main)

        initRecyclerView()
        fetchRecords()
        requestPermissions()

        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorAccent))
        swipeRefreshLayout.setOnRefreshListener {

        }

        settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        homeViewModel.liveData().observe(this, Observer { data ->

            if (data?.isNotEmpty() == true) {
                records.clear()
            }

            data?.forEach {
                records.add(it)
            }

            recordsListAdapter.notifyDataSetChanged()
            if (records.isEmpty()) {
                emptyDataLayout.visibility = View.VISIBLE
            }else {
                emptyDataLayout.visibility = View.GONE
            }
        })
    }

    private fun initRecyclerView() {

        recordsListAdapter = RecordsListAdapter(records)
        recordsRecyclerView.layoutManager = LinearLayoutManager(this)
        recordsRecyclerView.adapter = recordsListAdapter

        recordsListAdapter.clickLister = { record ->

            val dialog = PlayerDialog.newInstance(record.savedPath).apply {
                isCancelable = false
            }
            dialog.show(supportFragmentManager, "PlayerViewDialog")
        }
    }

    private fun fetchRecords() {
        homeViewModel.fetchRecords()
    }

    /*
    *  Request all permissions needed for Kowe to function
    * */
    private fun requestPermissions() {

        if (!hasPermissions()) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_RC)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode != PERMISSION_RC) {
            return
        }

        for (result in grantResults) {

            if (result == PackageManager.PERMISSION_DENIED) {
                // permission dialog denied
                return
            }
        }
    }

    private fun hasPermissions(): Boolean {

        for (perm in PERMISSIONS) {

            if (ActivityCompat.checkSelfPermission(this, perm) !=
                    PackageManager.PERMISSION_GRANTED) return false
        }

        return true
    }
}