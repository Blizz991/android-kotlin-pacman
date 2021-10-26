package org.pondar.canvasdemokotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import org.pondar.canvasdemokotlin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    //This is for viewbinding
    lateinit var binding : ActivityMainBinding
    private lateinit var game: Game

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        game = Game(this, binding.pointsView)
        game.setGameView(binding.gameView)
        binding.gameView.setGame(game)
        game.newGame()

        //adding a click listeners

        binding.newGameBtn?.setOnClickListener {
            game.newGame()
        }

        binding.moveUpButton.setOnClickListener {
            game.moveUp(16)
        }

        binding.moveRightButton.setOnClickListener {
            game.moveRight(16)
        }

        binding.moveDownButton.setOnClickListener {
            game.moveDown(16)
        }

        binding.moveLeftButton.setOnClickListener {
            game.moveLeft(16)
        }
    }
}
