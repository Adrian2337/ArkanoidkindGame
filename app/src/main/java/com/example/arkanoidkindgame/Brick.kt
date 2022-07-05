package com.example.arkanoidkindgame

class Brick(var height: Int, var width: Int, var xPosition: Int, var yPosition: Int) {

    var destroyed = false;


    fun destroy() {
        destroyed = true;
    }

    fun contains(x: Int, y: Int, r: Int): Boolean {
        if (x >= xPosition && x <= xPosition + width &&
            y >= yPosition && y <= yPosition + height
        )
            return true
        if (x + r >= xPosition && x + r <= xPosition + width &&
            y >= yPosition && y <= yPosition + height
        )
            return true
        if (x - r >= xPosition && x - r <= xPosition + width &&
            y >= yPosition && y <= yPosition + height
        )
            return true
        return false
    }
}