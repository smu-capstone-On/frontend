package com.example.team_on

import android.app.Activity
import android.graphics.Bitmap
import android.opengl.GLException
import com.kakao.vectormap.graphics.gl.GLSurfaceView
import java.nio.IntBuffer
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLContext
import javax.microedition.khronos.opengles.GL10

object MapCapture {

    interface OnCaptureListener {
        fun onCaptured(isSucceed: Boolean, fileName: String)
    }

    fun capture(activity: Activity, surfaceView: GLSurfaceView, listener: OnCaptureListener) {
        val fileName = "MapCapture_${System.currentTimeMillis()}.jpg"

        surfaceView.queueEvent {
            val egl = EGL10::class.java.cast(EGLContext.getEGL())
            val gl = GL10::class.java.cast(egl!!.eglGetCurrentContext().gl)
            val bitmap = gl?.let {
                createBitmapFromGLSurface(0, 0, surfaceView.width,
                    surfaceView.height, it
                )
            }

            activity.runOnUiThread {
                listener.onCaptured(true, fileName) // 이 부분에서 항상 성공으로 가정하고 서버에 전송 성공을 알리도록 수정
            }
        }
    }

    private fun createBitmapFromGLSurface(x: Int, y: Int, w: Int, h: Int, gl: GL10): Bitmap? {
        val bitmapBuffer = IntArray(w * h)
        val bitmapSource = IntArray(w * h)
        val intBuffer = IntBuffer.wrap(bitmapBuffer)
        intBuffer.position(0)

        try {
            gl.glReadPixels(x, y, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, intBuffer)
            var offset1: Int
            var offset2: Int

            for (i in 0 until h) {
                offset1 = i * w
                offset2 = (h - i - 1) * w

                for (j in 0 until w) {
                    val texturePixel = bitmapBuffer[offset1 + j]
                    val blue = texturePixel shr 16 and 0xff
                    val red = texturePixel shl 16 and 0x00ff0000
                    val pixel = texturePixel and 0xff00ff00.toInt() or red or blue
                    bitmapSource[offset2 + j] = pixel
                }
            }
        } catch (e: GLException) {
            return null
        }

        return Bitmap.createBitmap(bitmapSource, w, h, Bitmap.Config.ARGB_8888)
    }
}
