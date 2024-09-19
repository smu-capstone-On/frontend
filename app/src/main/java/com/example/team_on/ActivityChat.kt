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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.team_on.databinding.ActivityChatBinding
import com.gmail.bishoybasily.stomp.lib.StompClient
import com.gmail.bishoybasily.stomp.lib.Event
import io.reactivex.disposables.Disposable
import okhttp3.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar

class ActivityChat : AppCompatActivity() {

    private val binding : ActivityChatBinding by lazy { ActivityChatBinding.inflate(layoutInflater) }

    private lateinit var chatEdit : EditText
    private lateinit var const: ConstraintLayout
    private lateinit var chatConst: ConstraintLayout
    private lateinit var btnSend: ImageButton
    private val listChat = mutableListOf<ChatMessage>()
    private lateinit var recyclerChat: RecyclerView
    private lateinit var chatAdapter: AdapterChat

    lateinit var stompConnection: Disposable

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

    @SuppressLint("CheckResult", "NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        chatEdit = binding.chatEdit
        const = binding.chatMainConst
        chatConst = binding.chatConst
        btnSend = binding.chatBtnSend
        recyclerChat = binding.chatRv

        recyclerChat.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        chatAdapter = AdapterChat(listChat)
        recyclerChat.adapter = chatAdapter

        chatEdit.addTextChangedListener(chatline)

        val url = "ws://34.231.37.92:8080/ws/websocket"
        val intervalMillis = 1000L
        val client = OkHttpClient()

        val stomp = StompClient(client, intervalMillis).apply { this@apply.url = url }

        stompConnection = stomp.connect().subscribe {
            when (it.type) {
                Event.Type.OPENED -> {
                    Log.d("stomp", "연결")
                }

                Event.Type.CLOSED -> {

                }

                Event.Type.ERROR -> {

                }

                null -> TODO()
            }
        }

        stomp.join("/user/3/queue/messages")
            .subscribe(
                { message ->
                    Log.d("stomp", "메시지: $message")
                },
                { throwable ->
                    Log.d("stomp", "에러 메시지: ${throwable.message}")
                }
            )

        btnSend.setOnClickListener {
            val message = chatEdit.text.toString()
            if (message.isNotBlank()) {
                chatEdit.text.clear()  // 메시지 전송 후 입력창 초기화
            }
            val data = JSONObject()
            data.put("senderId", 1)
            data.put("message", message)
            data.put("recipientId", 3)
            stomp.send("/app/send/2", data.toString()).subscribe()
            listChat.add(ChatMessage(message, getCurrentTime()))
            recyclerChat.adapter?.notifyDataSetChanged()
        }
    }

    fun Int.dpToPx(): Int {
        return (this * Resources.getSystem().displayMetrics.density).toInt()
    }

    //오전 or 오후 몇 시인지 변환
    @SuppressLint("SimpleDateFormat")
    fun getCurrentTime(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("a hh:mm")
        return dateFormat.format(calendar.time)
    }
}