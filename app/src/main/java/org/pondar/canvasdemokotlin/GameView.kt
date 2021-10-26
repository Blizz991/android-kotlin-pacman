package org.pondar.canvasdemokotlin


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.widget.TextView

class GameView : View {
    private lateinit var game: Game
    var h: Int = 0
    var w: Int = 0 //used for storing our height and width

    fun setGame(game: Game) {
        this.game = game
    }

    /* The next 3 constructors are needed for the Android view system,
	when we have a custom view.
	 */
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)


    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    //In the onDraw we put all our code that should be
    //drawn whenever we update the screen.
    override fun onDraw(canvas: Canvas) {
        //Here we get the height and weight
        h = canvas.height
        w = canvas.width
        println("h = $h, w = $w")
        //Making a new paint object
        val paint = Paint()
        //setting the color
        //paint.color = Color.RED
        //paint.strokeWidth = 10.0f
        //canvas.drawColor(Color.WHITE) //clear entire canvas to white color
        //drawing a line from (0,0) -> (300,200)
        //canvas.drawLine(0f, 0f, 300f, 200f, paint)
        //paint.color = Color.GREEN
        //canvas.drawLine(0f, 200f, 300f, 0f, paint)


        //setting the color using the format: Transparency, Red, Green, Blue
        //paint.color = -0xffff67

        //drawing a circle with radius 20, and center in (100,100)
        //canvas.drawCircle(100f, 100f, 30f, paint)


        if (!(game.coinsInitialized))
            game.initializeGoldCoins()

        for (coin in game.coins) {
            canvas.drawBitmap(game.coinBitmap, coin.x.toFloat(), coin.y.toFloat(), paint)
        }

        canvas.drawBitmap(game.pacmanBitmap, game.pacx.toFloat(), game.pacy.toFloat(), paint)
        super.onDraw(canvas)
    }

}




