package org.pondar.canvasdemokotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import org.pondar.canvasdemokotlin.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {

    private var myTimer: Timer = Timer()
    var counter: Int = 0
    lateinit var binding : ActivityMainBinding
    private lateinit var game: Game

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

        myTimer.schedule(object: TimerTask() {
            override fun run() {
                timerMethod()
            }
        }, 0 , 50)

        //adding a click listeners
        binding.newGameBtn.setOnClickListener {
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
        if (game.running) {
            counter++

            //We handle direction in the Game class
            game.movePacman(8)
        }
    }
}
