package org.pondar.canvasdemokotlin

import android.widget.TextView
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.lang.IllegalArgumentException
import kotlin.math.*
import kotlin.collections.ArrayList

open class Game(private var context: Context, pointsView: TextView, levelView: TextView) {
    var running = false
    var gameOver = false
    private var currLevel: Int = 1
    private var levelView: TextView = levelView

    private var pointsView: TextView = pointsView
    var points: Int = 0
    private var controlsWidth: Int = 400
    private lateinit var gameView: GameView

    var pacmanBitmap: Bitmap = BitmapFactory.decodeResource(context.resources,
                                                            R.drawable.pacman32_right)
    var pacmanSize: Int = 32
    var pacmanSpeed: Int = 16
    var pacx = 0
    var pacy = 0
    var pacCurrDirection = Direction.RIGHT

    private var ghostSpeed: Int = 4
    private var ghostSize: Int = 32
    private var ghostsToCreate = 2
    var ghostsInitialized = false
    var ghosts = ArrayList<Ghost>()

    var coinBitmap: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.coin)
    var coinsInitialized = false
    var coins = ArrayList<GoldCoin>()
    private var coinsToCreate = 10

    fun setGameView(view: GameView) {
        this.gameView = view
    }

    fun initializeGoldCoins() {
        //Initialize gold coins
        for (i in 1..coinsToCreate) {
            val randomX = (32..(gameView.w-pacmanSize-controlsWidth)).random()
            val randomY = (32..(gameView.h-pacmanSize)).random()
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

    fun initializeGhosts() {
        //Initialize ghosts
        for (i in 1..ghostsToCreate) {
            //Random number based on the amount of colors we have
            val randomNumber: Int = (GhostColor.values().indices).random()
            val ghostColor = GhostColor.values()[randomNumber]
            ghosts.add(
                Ghost(
                    //Far right
                    gameView.w - ghostSize - controlsWidth,
                    //Bottom
                    gameView.h - ghostSize,
                    ghostColor,
                    Direction.UP,
                    getGhostSprite(Direction.UP, ghostColor)
                )
            )
        }

        ghostsInitialized = true
    }

    fun newGame() {
        gameOver = false
        //Reset pacman
        pacx = 0
        pacy = 0
        pacmanBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.pacman32_right)
        pacCurrDirection = Direction.RIGHT

        //Update level
        currLevel = 1
        updateLevelText()

        //Reset points & coins
        points = 0
        updatePointsText()
        coins = ArrayList<GoldCoin>()
        coinsToCreate = 10
        coinsInitialized = false

        //Reset ghosts
        ghosts = ArrayList<Ghost>()
        ghostsInitialized = false

        //Invalidate game view so everything updates
        gameView.invalidate()
    }

    private fun startNextLevel() {
        coins = ArrayList<GoldCoin>()
        coinsToCreate += 10
        coinsInitialized = false
        currLevel++
        updateLevelText()

        //Speed up ghosts
        ghostSpeed += 4
        //Add ghost
        val randomNumber: Int = (GhostColor.values().indices).random()
        val ghostColor = GhostColor.values()[randomNumber]
        ghosts.add(
            Ghost(
                gameView.w - ghostSize - controlsWidth,
                //Bottom
                gameView.h - ghostSize,
                ghostColor,
                Direction.UP,
                getGhostSprite(Direction.UP, ghostColor)
            )
        )

        //Invalidate game view so everything updates
        gameView.invalidate()
    }

    //region $Pacman movement
    fun movePacman(distanceToMove: Int) {
        when(pacCurrDirection) {
            Direction.UP -> moveUp(distanceToMove)
            Direction.RIGHT -> moveRight(distanceToMove)
            Direction.DOWN -> moveDown(distanceToMove)
            Direction.LEFT -> moveLeft(distanceToMove)
        }
    }

    private fun moveUp(y: Int) {
        //still within our boundaries?
        if (pacy - y + pacmanSize > pacmanSize - y) {
            pacmanBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.pacman32_up)
            pacy -= y
            pacCurrDirection = Direction.UP
            doCollisionCheck()
            gameView.invalidate() //redraw everything - this ensures onDraw() is called.
        }
    }

    private fun moveRight(x: Int) {
        //still within our boundaries?
        if (pacx + x + pacmanSize < gameView.w - controlsWidth - pacmanSize - x) {
            pacmanBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.pacman32_right)
            pacx += x
            pacCurrDirection = Direction.RIGHT
            doCollisionCheck()
            gameView.invalidate() //redraw everything - this ensures onDraw() is called.
        }
    }

    private fun moveDown(y: Int) {
        //still within our boundaries?
        if (pacy + y + pacmanSize < gameView.h - pacmanSize - y) {
            pacmanBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.pacman32_down)
            pacy += y
            pacCurrDirection = Direction.DOWN
            doCollisionCheck()
            gameView.invalidate() //redraw everything - this ensures onDraw() is called.
        }
    }

    private fun moveLeft(x: Int) {
        //still within our boundaries?
        if (pacx - x + pacmanSize > pacmanSize - x) {
            pacmanBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.pacman32_left)
            pacx -= x
            pacCurrDirection = Direction.LEFT
            doCollisionCheck()
            gameView.invalidate() //redraw everything - this ensures onDraw() is called.
        }
    }


    private fun doCollisionCheck(ghostOnly: Boolean = false) {
        if (!ghostOnly) {
            for (coin in coins) {
                if (!coin.taken && distance(pacx, pacy, coin.x, coin.y) < (pacmanSize * 2.5)) {
                    coin.taken = true
                    points += 1 * currLevel
                    updatePointsText()
                }
            }

            if (coins.all { coin -> coin.taken }) {
                startNextLevel()
            }
        }

        for(ghost in ghosts) {
            if (distance(pacx, pacy, ghost.x, ghost.y) < (pacmanSize*2)) {
                running = false
                gameOver = true
            }
        }
    }

    private fun distance(x1: Int, y1: Int, x2: Int, y2: Int): Double {
        return sqrt(
            (x2.toDouble() - x1.toDouble()).pow(2) + (y2.toDouble() - y1.toDouble()).pow(2)
        )
    }
    //endregion

    //region $Ghost movement
    private fun moveGhost(ghost: Ghost, distanceToMove: Int, changeDirection: Boolean) {
        if (changeDirection) {
            //Random ghost movement
            val randomDirection: Int = (0..3).random()
            val newGhostDirection: Direction = Direction.values()[randomDirection]
            //above is a cleaner way of doing what is below here
            /*var newGhostDirection: Direction = when(randomDirection) {
                0 -> Direction.UP
                1 -> Direction.RIGHT
                2 -> Direction.DOWN
                3 -> Direction.LEFT
                else -> ghost.currDirection
            }*/
            ghost.currDirection = newGhostDirection
            ghost.sprite = getGhostSprite(ghost.currDirection, ghost.ghostColor)
        }

        when(ghost.currDirection) {
            Direction.UP -> {
                if (ghost.y - distanceToMove + ghostSize > ghostSize - distanceToMove) {
                    ghost.y -= distanceToMove
                }
            }
            Direction.RIGHT -> {
                if (ghost.x + distanceToMove + ghostSize < gameView.w - controlsWidth - ghostSize - distanceToMove) {
                    ghost.x += distanceToMove
                }
            }
            Direction.DOWN -> {
                if (ghost.y + distanceToMove + ghostSize < gameView.h - controlsWidth - ghostSize - distanceToMove) {
                    ghost.y += distanceToMove
                }
            }
            Direction.LEFT -> {
                if (ghost.x - distanceToMove + ghostSize > ghostSize - distanceToMove) {
                    ghost.x -= distanceToMove
                }
            }
        }

        doCollisionCheck(true)
        gameView.invalidate()
    }

    fun moveAllGhosts() {
        for (ghost in ghosts) {
            moveGhost(ghost, ghostSpeed, false)
        }
    }

    fun ghostsChangeDirection() {
        for (ghost in ghosts) {
            moveGhost(ghost, ghostSpeed, true)
        }
    }

    private fun getGhostSprite(direction: Direction, ghostColor: GhostColor): Bitmap {
        //Default to red pointing up in case something goes wrong
        var ghostSprite = BitmapFactory.decodeResource(context.resources, R.drawable.ghost_red_up)

        val resourceName = "ghost_${ghostColor.name.lowercase()}_${direction.name.lowercase()}"
        getResourceID(resourceName,"drawable", context)

        ghostSprite = BitmapFactory.decodeResource(context.resources, getResourceID(resourceName,"drawable", context))
        //BitmapFactory.decodeResource(context.resources, R.drawable.ghost_red_up)

        //Could expand to more colors, but keeping it to 2 for now :)
        //Legacy: I figured out how to use string names to get resource files (see above) :D
        /*when(ghostColor) {
            GhostColor.RED -> {
                ghostSprite = when(direction){
                    Direction.UP -> BitmapFactory.decodeResource(context.resources, R.drawable.ghost_red_up)
                    Direction.RIGHT -> BitmapFactory.decodeResource(context.resources, R.drawable.ghost_red_right)
                    Direction.DOWN -> BitmapFactory.decodeResource(context.resources, R.drawable.ghost_red_down)
                    Direction.LEFT -> BitmapFactory.decodeResource(context.resources, R.drawable.ghost_red_left)
                }
            }
            GhostColor.TEAL -> {
                ghostSprite = when(direction){
                    Direction.UP -> BitmapFactory.decodeResource(context.resources, R.drawable.ghost_teal_up)
                    Direction.RIGHT -> BitmapFactory.decodeResource(context.resources, R.drawable.ghost_teal_right)
                    Direction.DOWN -> BitmapFactory.decodeResource(context.resources, R.drawable.ghost_teal_down)
                    Direction.LEFT -> BitmapFactory.decodeResource(context.resources, R.drawable.ghost_teal_left)
                }
            }
        }*/

        return ghostSprite
    }
    //endregion

    private fun updatePointsText() {
        pointsView.text = "Points: $points"
    }

    private fun updateLevelText() {
        levelView.text = "Level: $currLevel"
    }

    protected fun getResourceID(resName: String, resType: String?, ctx: Context): Int {
        val resourceID = ctx.resources.getIdentifier(
            resName, resType,
            ctx.applicationInfo.packageName
        )
        return if (resourceID == 0) {
            throw IllegalArgumentException(
                "No resource string found with name $resName"
            )
        } else {
            resourceID
        }
    }
}