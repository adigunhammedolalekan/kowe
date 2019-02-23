package app.kowe.kowe.ui

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import app.kowe.kowe.L
import app.kowe.kowe.R
import app.kowe.kowe.bindView
import net.steamcrafted.materialiconlib.MaterialIconView
import java.io.File

class PlayerDialog: DialogFragment() {

    private val closePlayer by bindView<MaterialIconView>(R.id.btn_close_player)
    private val pausePlayButton by bindView<ImageView>(R.id.btnPlayPause)
    private val seekBar by bindView<SeekBar>(R.id.seekBar)

    private lateinit var mediaPlayer: MediaPlayer
    private var playerState = PAUSED
    private val PAUSED_ICON = R.drawable.ic_pause
    private val PLAY_ICON = R.drawable.ic_play

    companion object {

        const val EXTRA_RECORD_PATH = "record_path"
        const val PLAYING = 0
        const val PAUSED = 1

        fun newInstance(recordPath: String?): PlayerDialog {

            val bundle = Bundle().apply {
                putString(EXTRA_RECORD_PATH, recordPath)
            }

            return PlayerDialog().apply {
                arguments = bundle
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeMediaPlayer()
    }

    private fun initializeMediaPlayer() {

        try {

            val recordPath = arguments?.getString(EXTRA_RECORD_PATH) ?: return
            mediaPlayer = MediaPlayer.create(activity, Uri.fromFile(File(recordPath)))
            mediaPlayer.prepareAsync()

            mediaPlayer.setOnPreparedListener { mediaPlayer ->
                mediaPlayer.start()
            }

        }catch (e: Exception) {
            L.error(e)
        }
    }

    private fun play() {

        try {
            mediaPlayer.start();playerState = PLAYING
            pausePlayButton.setImageResource(PLAY_ICON) } catch (e: Exception) {
            L.error(e)
        }
    }

    private fun pause() {

        try { mediaPlayer.pause(); playerState = PAUSED
              pausePlayButton.setImageResource(PAUSED_ICON) } catch (e: Exception) {
            L.error(e)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater.inflate(R.layout.layout_player_dialog, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStyle(STYLE_NORMAL, theme)

        closePlayer.setOnClickListener {
            dismiss()
        }

        pausePlayButton.setOnClickListener {
            when(playerState) {
                PLAYING -> pause()
                PAUSED -> play()
            }
        }
    }
}