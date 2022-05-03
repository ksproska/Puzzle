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
import android.provider.ContactsContract
import android.util.Log
import android.view.DragEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    var dropListeners: ArrayList<ImageView> = arrayListOf()
    var dropImages: ArrayList<Int> = arrayListOf(
        R.drawable.row_1_column_1, R.drawable.row_1_column_2, R.drawable.row_1_column_3, R.drawable.row_1_column_4,
        R.drawable.row_2_column_1, R.drawable.row_2_column_2, R.drawable.row_2_column_3, R.drawable.row_2_column_4,
        R.drawable.row_3_column_1, R.drawable.row_3_column_2, R.drawable.row_3_column_3, R.drawable.row_3_column_4,
        R.drawable.row_4_column_1, R.drawable.row_4_column_2, R.drawable.row_4_column_3, R.drawable.row_4_column_4
    )
    var notSetImages: ArrayList<Int> = arrayListOf(
        R.drawable.row_1_column_1, R.drawable.row_1_column_2, R.drawable.row_1_column_3, R.drawable.row_1_column_4,
        R.drawable.row_2_column_1, R.drawable.row_2_column_2, R.drawable.row_2_column_3, R.drawable.row_2_column_4,
        R.drawable.row_3_column_1, R.drawable.row_3_column_2, R.drawable.row_3_column_3, R.drawable.row_3_column_4,
        R.drawable.row_4_column_1, R.drawable.row_4_column_2, R.drawable.row_4_column_3, R.drawable.row_4_column_4
    )
    lateinit var toDropImage: ImageView

    fun getIdOfImageForImageView(imageView: ImageView): Int {
        var inx = dropListeners.indexOf(imageView)
        return dropImages[inx]
    }

    fun setNextImage() {
        if (notSetImages.size == 0) {
            toDropImage.setImageResource(R.drawable.ic_empty_image)
            return
        }
        val randomNumber = Random().nextInt(notSetImages.size)
        toDropImage.setImageResource(notSetImages[randomNumber])
        Log.i(null, "" + dropImages.indexOf(notSetImages[randomNumber]))
        toDropImage.tag = randomNumber
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toDropImage = findViewById(R.id.imageView_toDrop)
        dropListeners.add(findViewById(R.id.imageView_r1c1))
        dropListeners.add(findViewById(R.id.imageView_r1c2))
        dropListeners.add(findViewById(R.id.imageView_r1c3))
        dropListeners.add(findViewById(R.id.imageView_r1c4))
        dropListeners.add(findViewById(R.id.imageView_r2c1))
        dropListeners.add(findViewById(R.id.imageView_r2c2))
        dropListeners.add(findViewById(R.id.imageView_r2c3))
        dropListeners.add(findViewById(R.id.imageView_r2c4))
        dropListeners.add(findViewById(R.id.imageView_r3c1))
        dropListeners.add(findViewById(R.id.imageView_r3c2))
        dropListeners.add(findViewById(R.id.imageView_r3c3))
        dropListeners.add(findViewById(R.id.imageView_r3c4))
        dropListeners.add(findViewById(R.id.imageView_r4c1))
        dropListeners.add(findViewById(R.id.imageView_r4c2))
        dropListeners.add(findViewById(R.id.imageView_r4c3))
        dropListeners.add(findViewById(R.id.imageView_r4c4))

        setNextImage()
        toDropImage.setOnClickListener { v ->
            setNextImage()
        }

        toDropImage.setOnLongClickListener { v:View ->
            val item = ClipData.Item(toDropImage.id as? CharSequence)

            val dragData = ClipData(
                "imageId",
                arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                item
            )

            val myShadow = MyDragShadowBuilder(v)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                v.startDragAndDrop(dragData, myShadow,null,0)
            } else{
                v.startDrag(dragData, myShadow,null,0)
            }
        }

//        textView2.setOnDragListener(dragListen)
        for (imv in dropListeners) {
            imv.setOnDragListener(imageDragListener)
        }
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
    private val imageDragListener = View.OnDragListener { v, event ->
        val imageView: ImageView = v as ImageView
        var dragedImageInx: Int = toDropImage.tag as Int
        var imageThatShouldBeDroped = getIdOfImageForImageView(imageView)

        if (notSetImages.contains(imageThatShouldBeDroped)) {
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    true
                }

                DragEvent.ACTION_DRAG_ENTERED -> {
                    imageView.setImageResource(R.drawable.ic_input)
                    true
                }

                DragEvent.ACTION_DRAG_LOCATION ->
                    true

                DragEvent.ACTION_DRAG_EXITED -> {
                    imageView.setImageResource(R.drawable.ic_empty_image)
                    true
                }

                DragEvent.ACTION_DROP -> {
                    if (imageThatShouldBeDroped == notSetImages[dragedImageInx]) {
                        imageView.setImageResource(notSetImages[dragedImageInx])
                        notSetImages.remove(notSetImages[dragedImageInx])
                        imageView.setBackgroundColor(Color.BLUE)
                        setNextImage()
                    }
                    else {
//                        imageView.setBackgroundColor(0x00000000)
                        imageView.setImageResource(R.drawable.ic_empty_image)
                    }
                    true
                }

                DragEvent.ACTION_DRAG_ENDED -> {
                    true
                }

                else -> {
                    // An unknown action type was received.
                    false
                }
            }
        }

        true
    }
}