package com.example.team_on

import android.annotation.SuppressLint
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
import io.reactivex.disposables.CompositeDisposable
import okhttp3.*
import org.json.JSONObject
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import java.time.LocalDateTime

class ActivityChat : AppCompatActivity() {

    private val binding : ActivityChatBinding by lazy { ActivityChatBinding.inflate(layoutInflater) }

    private lateinit var chatEdit : EditText
    private lateinit var const: ConstraintLayout
    private lateinit var chatConst: ConstraintLayout
    private lateinit var btnSend: ImageButton

    private lateinit var stompClient: StompClient
    private val compositeDisposable = CompositeDisposable()

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

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        chatEdit = binding.chatEdit
        const = binding.chatMainConst
        chatConst = binding.chatConst
        btnSend = binding.chatBtnSend

        chatEdit.addTextChangedListener(chatline)

        val url = "ws://34.231.37.92:8080/ws/websocket"

        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, url)
        stompClient.withServerHeartbeat(10000)
        stompClient.connect()

//        val topicDisposable = stompClient.topic("/queue/messages").subscribe(
//            { topicMessage ->
//                Log.d("stomp", "메시지 수신")
//                val payload = topicMessage.payload
//                val jsonObject = JSONObject(payload)
//                val sender = jsonObject.getString("senderId")
//                val message = jsonObject.getString("content")
//            },
//            { throwable ->
//                Log.e("stomp", "Error while receiving message", throwable)
//            }
//        )

        stompClient.topic("/queue/messages/3").subscribe(
            { topicMessage ->
                Log.d("스톰프", "메시지 수신")
                val payload = topicMessage.payload
                val jsonObject = JSONObject(payload)
                val sender = jsonObject.getString("senderId")
                val message = jsonObject.getString("content")
            },
            { throwable ->
                Log.e("stomp", "Error while receiving message", throwable)
            }
        )

        val lifecycleDisposable = stompClient.lifecycle().subscribe { lifecycleEvent ->
            when (lifecycleEvent.type) {
                LifecycleEvent.Type.OPENED -> {
                    Log.d("웹소켓", "연결")
                }
                LifecycleEvent.Type.CLOSED -> {
                    Log.d("웹소켓", "끊김")
                }
                LifecycleEvent.Type.ERROR -> {
                    Log.d("웹소켓", "오류.")
                }
                else->{
                }
            }
        }

        btnSend.setOnClickListener {
            val message = chatEdit.text.toString()
            if (message.isNotBlank()) {
                chatEdit.text.clear()  // 메시지 전송 후 입력창 초기화
            }
            val data = JSONObject()
            data.put("senderId", 1)
            data.put("message", "하이")
            data.put("recipientId", 3)
            stompClient.send("/app/send/2", data.toString()).subscribe()
        }
    }

    fun Int.dpToPx(): Int {
        return (this * Resources.getSystem().displayMetrics.density).toInt()
    }
}