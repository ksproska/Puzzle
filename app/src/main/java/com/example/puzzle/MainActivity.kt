package com.example.puzzle

import android.content.ClipData
import android.content.ClipDescription
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.DragEvent
import android.view.View
import android.widget.TextView
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var textView: TextView
    lateinit var textView2: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.textView)
        textView2 = findViewById(R.id.textView2)

        val randomNumber = Random().nextInt(100)
        textView.text = "$randomNumber"
        textView.tag = textView.text

        textView.setOnClickListener {v->
            val randomNumber = Random().nextInt(100)
            (v as TextView).text = "$randomNumber"
            (v as TextView).tag = (v as TextView).text
        }

        textView.setOnLongClickListener { v:View ->
            val item = ClipData.Item(v.tag as? CharSequence)

            val dragData = ClipData(
                v.tag as CharSequence,
                arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                item
            )

            val myShadow = MyDragShadowBuilder(v)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                v.startDragAndDrop(dragData,myShadow,null,0)
            }else{
                v.startDrag(dragData,myShadow,null,0)
            }
        }

        textView2.setOnDragListener(dragListen)
    }


    private class MyDragShadowBuilder(v: View) : View.DragShadowBuilder(v) {
        private val shadow = ColorDrawable(Color.LTGRAY)

        override fun onProvideShadowMetrics(size: Point, touch: Point) {
            val width: Int = (view.width / 1.2F).toInt()
            val height: Int = (view.height / 1.2F).toInt()
            //val width: Int = view.width / 2
            //val height: Int = view.height / 2

            shadow.setBounds(0, 0, width, height)
            size.set(width, height)
            touch.set(width / 2, height / 2)
        }
        override fun onDrawShadow(canvas: Canvas) {
            shadow.draw(canvas)
        }
    }


    // Creates a new drag event listener
    private val dragListen = View.OnDragListener { v, event ->
        val receiverView: TextView = v as TextView

        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                if (event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                    receiverView.setBackgroundColor(Color.CYAN)
                    receiverView.text = "Hold and drag here."
                    v.invalidate()
                    true
                } else {
                    false
                }
            }

            DragEvent.ACTION_DRAG_ENTERED -> {
                receiverView.setBackgroundColor(Color.GREEN)
                receiverView.text = "Good, put here."
                v.invalidate()
                true
            }

            DragEvent.ACTION_DRAG_LOCATION ->
                true

            DragEvent.ACTION_DRAG_EXITED -> {
                receiverView.setBackgroundColor(Color.YELLOW)
                receiverView.text = "Oh! you exited."
                v.invalidate()
                true
            }

            DragEvent.ACTION_DROP -> {
                val item: ClipData.Item = event.clipData.getItemAt(0)
                val dragData = item.text
                receiverView.text = "You dropped : $dragData"
                v.invalidate()
                true
            }

            DragEvent.ACTION_DRAG_ENDED -> {
                receiverView.setBackgroundColor(Color.WHITE)
                v.invalidate()


                when(event.result) {
                    true ->
                        // drop was handled
                        receiverView.setBackgroundColor(Color.WHITE)
                    else ->{
                        // drop didn't work
                        receiverView.text = "Drop failed."
                        receiverView.setBackgroundColor(Color.RED)
                    }
                }

                // returns true; the value is ignored.
                true
            }

            else -> {
                // An unknown action type was received.
                false
            }
        }
    }
}