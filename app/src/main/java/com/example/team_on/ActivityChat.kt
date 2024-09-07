package com.example.team_on

import android.content.res.Resources
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.team_on.databinding.ActivityChatBinding
import okhttp3.*

class ActivityChat : AppCompatActivity() {

    private val binding : ActivityChatBinding by lazy { ActivityChatBinding.inflate(layoutInflater) }

    companion object {
        const val NORMAL_CLOSURE_STATUS = 1000
    }

    private lateinit var chatEdit : EditText
    private lateinit var const: ConstraintLayout
    private lateinit var chatConst: ConstraintLayout
    private lateinit var btnSend: ImageButton

    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null
    private val TAG = "WebSocketManager"

    fun connectWebSocket(roomId: Int) {
        val request = Request.Builder()
            .url("ws://34.231.37.92:8080/send/$roomId")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {

            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.i(TAG, "WebSocket Opened: $response")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d(TAG, "Received Message: $text")
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.i(TAG, "Closing WebSocket: Code=$code Reason=$reason")
                webSocket.close(NORMAL_CLOSURE_STATUS, null)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.i(TAG, "WebSocket Closed: Code=$code Reason=$reason")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e(TAG, "WebSocket Error: ${t.message}")
            }
        })
    }

    fun sendMessage(message: String, senderId: Int, recipientId: Int) {
        val payload = """
            {
                "message": "$message",
                "senderId": $senderId,
                "recipientId": $recipientId
            }
        """.trimIndent()

        webSocket?.send(payload)
        Log.d(TAG, "Sent Message: $payload")
    }

    fun closeWebSocket() {
        webSocket?.close(NORMAL_CLOSURE_STATUS, "Closing WebSocket")
        Log.i(TAG, "WebSocket closed manually.")
    }

    private val chatline = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val lines = chatEdit.lineCount
            if(lines == 1){
                const.layoutParams.height = 50.dpToPx()
                chatConst.layoutParams.height = 40.dpToPx()
                chatEdit.layoutParams.height = 30.dpToPx()

                const.requestLayout()
                chatConst.requestLayout()
                chatEdit.requestLayout()
            }else if(lines == 2){
                const.layoutParams.height = 70.dpToPx()
                chatConst.layoutParams.height = 60.dpToPx()
                chatEdit.layoutParams.height = 50.dpToPx()

                const.requestLayout()
                chatConst.requestLayout()
                chatEdit.requestLayout()
            }else if(lines >= 3){
                const.layoutParams.height = 93.dpToPx()
                chatConst.layoutParams.height = 83.dpToPx()
                chatEdit.layoutParams.height = 73.dpToPx()

                const.requestLayout()
                chatConst.requestLayout()
                chatEdit.requestLayout()
            }
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        chatEdit = binding.chatEdit
        const = binding.chatMainConst
        chatConst = binding.chatConst
        btnSend = binding.chatBtnSend

        chatEdit.addTextChangedListener(chatline)

        // 웹소켓 연결
        connectWebSocket(1)

        btnSend.setOnClickListener {
            val message = chatEdit.text.toString()
            if (message.isNotBlank()) {
                sendMessage(message, 1, 2)
                chatEdit.text.clear()  // 메시지 전송 후 입력창 초기화
            }
        }
    }

    fun Int.dpToPx(): Int {
        return (this * Resources.getSystem().displayMetrics.density).toInt()
    }
}