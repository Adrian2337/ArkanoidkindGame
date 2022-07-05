package com.example.arkanoidkindgame

import android.graphics.Canvas
import android.view.SurfaceHolder


class GameThread(
    private val surfaceHolder: SurfaceHolder,
    private val gameView: GameView
) : Thread() {
    var running: Boolean = false
    private var canvas: Canvas? = null

    override fun run() {
        while (running) {
            canvas = null

            try {
                canvas = surfaceHolder.lockCanvas()
                synchronized(surfaceHolder) {
                    gameView.update()
                    gameView.draw(canvas!!)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            try {

                sleep(1)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}