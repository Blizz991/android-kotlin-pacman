package org.pondar.canvasdemokotlin

import android.widget.TextView
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import java.util.*

class Game(private var context: Context, view: TextView) {
    var pacmanBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.pacman32_right)
    var pacmanHeight: Int = 32
    var coinBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.coin)
    var coinHeight: Int = 16
    //The coordinates for our dear pacman: (0,0) is the top-left corner
    var pacx = 0
    var pacy = 0
    private var pointsView: View = view
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
        pacx = 0
        pacy = 0
        coinsInitialized = false
        points = 0
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

    fun doCollisionCheck() {

    }
}