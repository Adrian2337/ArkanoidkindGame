package com.example.arkanoidkindgame

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.graphics.*

import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.Math.abs


class GameView(context: Context, attributeSet: AttributeSet) : SurfaceView(context, attributeSet),
    SurfaceHolder.Callback {
    val numberOfBrickRows = 3

    var paddleStartHeight = 30
    var paddleStartWidth = 250
    var paddleStartX = 500
    var paddleStartY = 1400
    var ballStartX = -1
    var ballStartY = -1
    var bricksEmpty = false
    var ballSize = 10
    var spacing = 3
    var brickWidth = 10
    var brickHeight = 60
    val prefName = "MyPreferences"
    var thread: GameThread
    var bricks: ArrayList<Brick> = ArrayList()
    val paddle: Paddle = Paddle(paddleStartHeight, paddleStartWidth, paddleStartX, paddleStartY)
    val ball: Ball = Ball(ballStartX, ballStartY, ballSize)
    var time: Long = 0
    var timeStart: Long = 0
    var timeEnd: Long = 0
    var won = false


    init {
        holder.addCallback(this)
        thread = GameThread(holder, this)
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        loadData()
        brickWidth = width / 5 - spacing / 2 + 1
        brickHeight = height / 20
        ballSize = 20
        ballStartX = width / 2
        ballStartY = height / 3
        resetBall()
        if (bricksEmpty) reset()
        timeStart = System.currentTimeMillis()
        thread.running = true
        thread.start()

    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        timeEnd = System.currentTimeMillis()
        time += timeEnd - timeStart
        thread.running = false
        thread.join()
    }

    fun reset() {

        var currX = 0
        var currY = 0
        for (i in 1..numberOfBrickRows) {
            while (currX < width) {
                bricks.add(
                    Brick(
                        brickHeight - spacing,
                        brickWidth - spacing,
                        currX + spacing,
                        currY + spacing
                    )
                )
                currX += brickWidth
            }
            currX = 0
            currY += brickHeight
        }
    }

    fun resetBall() {
        ball.xPosition = width / 2
        ball.yPosition = height / 3
        ball.dy = 10
        ball.dx = 0
    }

    fun update() {

        updateBall()
        updateBrics()

    }

    fun updateBall() {

        if (ball.yPosition + ball.R + ball.dy >= paddle.yPosition && ball.yPosition + ball.R + ball.dy <= paddle.yPosition + paddle.height) {
            if (ball.xPosition <= paddle.xPosition + paddle.width && ball.xPosition + ball.dx >= paddle.xPosition) {
                val help = paddle.width / 5

                if (ball.xPosition <= paddle.xPosition + help)
                    ball.dx = -ball.dx
                else {
                    if (ball.xPosition >= paddle.xPosition + 4 * help)
                        ball.dx = -ball.dx
                    else ball.dx = ball.dx
                }


                ball.dy = -ball.dy

            }
        }
        if (ball.xPosition <= 0 || ball.xPosition >= width)
            ball.bounceHor()
        if (ball.yPosition <= 0) ball.bounceVert()
        if (ball.yPosition >= height) resetBall()//wypadła
        ball.move()
    }


    fun updateBrics() {
        saveData()
        for (b in bricks) {
            if (!b.destroyed && b.contains(ball.xPosition, ball.yPosition, ball.R)) {
                b.destroy()
                ball.bounceVert()
                if (checkWin()) {


                    timeEnd = System.currentTimeMillis()
                    time += timeEnd - timeStart
                    this.won = true
                }
            }
        }

    }

    override fun draw(canvas: Canvas?) {

        super.draw(canvas)
        if (canvas == null) return
        canvas.drawRGB(0, 0, 0)
        if (!won) {


            val p = Paint()
            p.setARGB(999, 0, 900, 0)
            if (ball.xPosition == -1 && ball.yPosition == -1) {
                ball.xPosition = width / 2
                ball.yPosition = height / 2
            }
            canvas.drawOval(
                RectF(
                    ball.xPosition.toFloat(),
                    ball.yPosition.toFloat(),
                    ball.xPosition.toFloat() + ballSize,
                    ball.yPosition.toFloat() + ballSize
                ),
                p
            )
            canvas.drawRect(
                paddle.xPosition.toFloat(),
                paddle.yPosition.toFloat(),
                paddle.xPosition + paddle.width.toFloat(),
                paddle.yPosition - paddle.height.toFloat(), p
            )
            for (b in bricks) {
                if (!b.destroyed) {
                    canvas.drawRect(
                        b.xPosition.toFloat(),
                        b.yPosition.toFloat(),
                        (b.xPosition + b.width).toFloat(),
                        (b.yPosition + b.height).toFloat(),
                        p
                    )
                }
            }
        } else {


            val bmp = BitmapFactory.decodeResource(resources, R.drawable.win)
            canvas.drawBitmap(bmp, 0f, 0f, null)

            saveHighScore()


        }

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        paddle.xPosition = Math.round(event.x) - (paddle.width / 2)
        return true
    }


    fun loadData() {
        val gson = Gson()
        val json =
            context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
                .getString("Bricks", "")

        val itemType = object : TypeToken<ArrayList<Brick>>() {}.type
        try {
            bricks = gson.fromJson<ArrayList<Brick>>(json, itemType)
        } catch (e: Exception) {
            bricksEmpty = true
        }


    }

    fun saveData() {
        val s = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
        val prefEditor = s.edit()
        val gson = Gson()
        val json = gson.toJson(bricks)
        prefEditor.putString("Bricks", json)
        prefEditor.commit()
    }

    fun checkWin(): Boolean {
        for (b in bricks) {
            if (!b.destroyed)
                return false
        }
        return true
    }

    fun saveHighScore() {
        var highscore = Integer.MAX_VALUE
        val gson = Gson()
        var json =
            context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
                .getString("High Score", "")

        val itemType = object : TypeToken<Int>() {}.type
        try {
            highscore = gson.fromJson<Int>(json, itemType)
        } catch (e: Exception) {
            bricksEmpty = true
        }
        if (highscore > time) {
            val s = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
            val prefEditor = s.edit()

            json = gson.toJson(time)
            prefEditor.putString("High Score", json)
            prefEditor.commit()
        }
    }

}