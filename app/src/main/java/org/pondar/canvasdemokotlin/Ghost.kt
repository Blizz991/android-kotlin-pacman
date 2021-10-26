package org.pondar.canvasdemokotlin

import android.graphics.Bitmap

class Ghost(
    var x: Int,
    var y: Int,
    val ghostColor: GhostColor,
    var currDirection: Direction,
    var sprite : Bitmap) {
}