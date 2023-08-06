package rahbar.abolfazl.dunimusic

import android.media.AudioManager
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import com.google.android.material.slider.Slider
import rahbar.abolfazl.dunimusic.databinding.ActivityMainBinding
import java.util.Locale
import java.util.Timer
import java.util.TimerTask

class MainActivity : AppCompatActivity() {
    // add binding in project ->
    lateinit var binding: ActivityMainBinding

    // Add Media player to play music ->
    lateinit var mediaPlayer: MediaPlayer

    // This variable is used to check the performance of the song ->
    var isPlaying = true

    var isUserChanging = false
    var isMute = false
    private lateinit var timer: Timer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // This function is called when the program is run for the first time ->
        prepareMusic()

        // When this button (play pause) is clicked, the configureMusic function is called->
        binding.btnPlayPause.setOnClickListener { configureMusic() }

        // When this button (go before) is clicked, the goBeforeMusic function is called->
        binding.btnGoBefore.setOnClickListener { goBeforeMusic() }

        // When this button (go after) is clicked, the goAfterMusic function is called->
        binding.btnGoAfter.setOnClickListener { goAfterMusic() }


        binding.btnVolumeOnOff.setOnClickListener { configureVolume() }

        binding.sliderMain.addOnChangeListener { slider, value, fromUser ->
            binding.txtLeft.text = convertMillisToString(value.toLong())
            isUserChanging = fromUser
        }

        binding.sliderMain.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {}

            override fun onStopTrackingTouch(slider: Slider) {
                mediaPlayer.seekTo(slider.value.toInt())
            }
        })
    }

    private fun configureVolume() {

        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager

        if (isMute) {
            audioManager.adjustVolume(AudioManager.ADJUST_UNMUTE, AudioManager.FLAG_SHOW_UI)
            binding.btnVolumeOnOff.setImageResource(R.drawable.ic_volume_on)
            isMute = false

        } else {
            audioManager.adjustVolume(AudioManager.ADJUST_MUTE, AudioManager.FLAG_SHOW_UI)
            binding.btnVolumeOnOff.setImageResource(R.drawable.ic_volume_off)
            isMute = true
        }

    }

    private fun goAfterMusic() {

        val now = mediaPlayer.currentPosition
        val newValue = now + 15000
        mediaPlayer.seekTo(newValue)

    }

    private fun goBeforeMusic() {

        val now = mediaPlayer.currentPosition
        val newValue = now - 15000
        mediaPlayer.seekTo(newValue)
    }

    private fun configureMusic() {

        // This code is to pause if the music was playing and to play if it was pause ->
        if (isPlaying) {

            mediaPlayer.pause()
            binding.btnPlayPause.setImageResource(R.drawable.ic_play)
            isPlaying = false

        } else {

            mediaPlayer.start()
            binding.btnPlayPause.setImageResource(R.drawable.ic_pause)
            isPlaying = true

        }

    }

    private fun prepareMusic() {

        // load music ->
        mediaPlayer = MediaPlayer.create(this, R.raw.music_file)


        //With this command, the music starts playing ->
        mediaPlayer.start()
        isPlaying = true

        // Set the play pause image for when the program is running ->
        binding.btnPlayPause.setImageResource(R.drawable.ic_pause)

        // Set valueTo slider to media player ->
        binding.sliderMain.valueTo = mediaPlayer.duration.toFloat()

        // Get the time from the media player and convert it to a string through the following function ->
        binding.txtRight.text = convertMillisToString(mediaPlayer.duration.toLong())

        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    if (!isUserChanging) {
                        binding.sliderMain.value = mediaPlayer.currentPosition.toFloat()
                    }
                }
            }

        }, 1000, 1000)


    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
        mediaPlayer.release()
    }

    // convert millis to string for txt Right - slider
    private fun convertMillisToString(duration: Long): String {

        val second = duration / 1000 % 60
        val minute = duration / (1000 * 60) % 60

        return java.lang.String.format(Locale.US, "%02d:%02d", minute, second)

    }


}