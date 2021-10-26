package org.pondar.canvasdemokotlin

import android.widget.TextView
import android.content.Context
import android.graphics.BitmapFactory
import kotlin.math.*
import java.util.*

class Game(private var context: Context, view: TextView) {
    var pacmanBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.pacman32_right)
    var pacmanHeight: Int = 32
    var coinBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.coin)
    var coinHeight: Int = 16
    //The coordinates for our dear pacman: (0,0) is the top-left corner
    var pacx = 0
    var pacy = 0
    private var pointsView: TextView = view
    private var points: Int = 0
    private var controlsWidth: Int = 400
    private lateinit var gameView: GameView

    var coinsInitialized = false
    var coins = ArrayList<GoldCoin>()

    fun setGameView(view: GameView) {
        this.gameView = view
    }

    fun initializeGoldCoins() {
        //Initialize gold coins
        for (i in 0..9) {
            var randomX = (32..(gameView.w-pacmanHeight-controlsWidth)).random()
            var randomY = (32..(gameView.h-pacmanHeight-controlsWidth)).random()
            coins.add(
                GoldCoin(
                    randomX,
                    randomY,
                    false
                )
            )
        }

        coinsInitialized = true
    }

    fun newGame() {
        //Reset pacman
        pacx = 0
        pacy = 0
        pacmanBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.pacman32_right)
        //Reset points & coins
        points = 0
        updatePoints()
        coins = ArrayList<GoldCoin>()
        coinsInitialized = false

        //Invalidate game view so everything updates
        gameView.invalidate()
    }

    fun moveUp(y: Int) {
        //still within our boundaries?
        if (pacy - y + pacmanHeight > pacmanHeight - y) {
            pacmanBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.pacman32_up)
            pacy -= y
            doCollisionCheck()
            gameView.invalidate() //redraw everything - this ensures onDraw() is called.
        }
    }

    fun moveRight(x: Int) {
        //still within our boundaries?
        if (pacx + x + pacmanHeight < gameView.w - controlsWidth - pacmanHeight - x) {
            pacmanBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.pacman32_right)
            pacx += x
            doCollisionCheck()
            gameView.invalidate() //redraw everything - this ensures onDraw() is called.
        }
    }

    fun moveDown(y: Int) {
        //still within our boundaries?
        if (pacy + y + pacmanHeight < gameView.h - pacmanHeight - y) {
            pacmanBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.pacman32_down)
            pacy += y
            doCollisionCheck()
            gameView.invalidate() //redraw everything - this ensures onDraw() is called.
        }
    }

    fun moveLeft(x: Int) {
        //still within our boundaries?
        if (pacx - x + pacmanHeight > pacmanHeight - x) {
            pacmanBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.pacman32_left)
            pacx -= x
            doCollisionCheck()
            gameView.invalidate() //redraw everything - this ensures onDraw() is called.
        }
    }

    private fun doCollisionCheck() {
        for (coin in coins) {
            if (!coin.taken && distance(pacx, pacy, coin.x, coin.y) < (pacmanHeight*2.5)){
                coin.taken = true
                points += 10
                updatePoints()
            }
        }
    }

    private fun distance(x1: Int, y1: Int, x2: Int, y2: Int): Double {
        return sqrt(
            (x2.toDouble() - x1.toDouble()).pow(2) + (y2.toDouble() - y1.toDouble()).pow(2)
        )
    }

    private fun updatePoints() {
        pointsView.text = "Points: $points"
    }
}