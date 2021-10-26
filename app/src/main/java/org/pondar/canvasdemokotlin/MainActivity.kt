package org.pondar.canvasdemokotlin

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorStateListDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*
import org.pondar.canvasdemokotlin.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {

    private var gameTimer: Timer = Timer()
    //private var timeLeft: Int = 0
    var timeSpent: Int = 0
    var timerTicks: Int = 0
    lateinit var binding : ActivityMainBinding
    private lateinit var game: Game

    //private val builder = AlertDialog.Builder(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        game = Game(this, binding.pointsView, binding.levelView)
        game.setGameView(binding.gameView)
        binding.gameView.setGame(game)
        game.newGame()
        game.running = true

        //Couldn't get dialog to work, found it pretty confusing
        //builder.setTitle(R.string.gameOverTitle)?.setMessage("You got: ${game.points} points!")
        //builder.create()

        gameTimer.schedule(object: TimerTask() {
            override fun run() {
                timerMethod()
            }
        }, 0 , 50)

        //adding a click listeners

        binding.pause.setOnClickListener {
            game.running = !game.running
            if (game.running) {
              binding.pause.text = getString(R.string.pauseBtn)
            } else {
                binding.pause.text = getString(R.string.unpauseBtn)
            }
        }

        binding.newGameBtn.setOnClickListener {
            //Reset timers
            //timeLeft = 120
            timeSpent = 0
            timerTicks = 0
            //binding.timeView.text = "Time: $timeLeft"
            binding.timeView.text = "Time: $timeSpent"

            //Reenable pause button (gets disabled when you run out of time)
            binding.pause.isEnabled = true

            //Reset pause state
            game.running = true
            binding.pause.text = getString(R.string.pauseBtn)

            //Reset game
            game.newGame()
        }

        binding.moveUpButton.setOnClickListener {
            game.pacCurrDirection = Direction.UP
            //game.moveUp(16)
        }

        binding.moveRightButton.setOnClickListener {
            game.pacCurrDirection = Direction.RIGHT
            //game.moveRight(16)
        }

        binding.moveDownButton.setOnClickListener {
            game.pacCurrDirection = Direction.DOWN
            //game.moveDown(16)
        }

        binding.moveLeftButton.setOnClickListener {
            game.pacCurrDirection = Direction.LEFT
            //game.moveLeft(16)
        }
    }

    private fun timerMethod() {
        this.runOnUiThread(timerTick)
    }

    private val timerTick = Runnable {
        if(game.gameOver) {
            game.running = false
            binding.pause.isEnabled = false

            //I wanted to use dialog, but couldn't get it to work
            Toast.makeText(applicationContext, "You got: ${game.points} points!", Toast.LENGTH_LONG).show()
        }

        if (game.running) {
            timerTicks+= 50

            if (timerTicks == 1000) {
                //1 second has passed
                timerTicks = 0

                timeSpent++
                binding.timeView.text = "Time: ${DateUtils.formatElapsedTime(timeSpent.toLong())}"

                /*timeLeft--
                binding.timeView.text = "Time: $timeLeft"*/

                game.ghostsChangeDirection()
            }

            /*if (timeLeft == 0) {
                game.running = false
                //Display dialog with game over
                //builder.show()
                binding.pause.isEnabled = false
                Toast.makeText(applicationContext, "You got: ${game.points} points!", Toast.LENGTH_LONG).show()
            }*/

            //We handle direction in the Game class
            game.movePacman(game.pacmanSpeed)
            game.moveAllGhosts()
        }
    }
}
