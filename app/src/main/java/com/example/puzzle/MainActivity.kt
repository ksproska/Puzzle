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
import android.util.Log
import android.view.DragEvent
import android.view.View
import android.widget.ImageView
import java.util.*
import kotlin.collections.ArrayList

import android.os.Handler
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    var dropListeners: ArrayList<ImageView> = arrayListOf()
    var seconds = 0
    var running = true
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
    var currentlySetImages: ArrayList<Int> = arrayListOf(
        R.drawable.ic_simple_square, R.drawable.ic_simple_square, R.drawable.ic_simple_square, R.drawable.ic_simple_square, 
        R.drawable.ic_simple_square, R.drawable.ic_simple_square, R.drawable.ic_simple_square, R.drawable.ic_simple_square, 
        R.drawable.ic_simple_square, R.drawable.ic_simple_square, R.drawable.ic_simple_square, R.drawable.ic_simple_square,
        R.drawable.ic_simple_square, R.drawable.ic_simple_square, R.drawable.ic_simple_square, R.drawable.ic_simple_square
    )
    lateinit var toDropImage: ImageView
    var currentImageInx: Int = -1

    fun getIdOfImageForImageView(imageView: ImageView): Int {
        var inx = dropListeners.indexOf(imageView)
        return dropImages[inx]
    }

    fun setNextImage() {
        if (notSetImages.size == 0) {
            toDropImage.setImageResource(R.drawable.ic_empty_image)
            running = false
            return
        }
        val randomNumber = Random().nextInt(notSetImages.size)
        toDropImage.setImageResource(notSetImages[randomNumber])
        Log.i(null, "" + dropImages.indexOf(notSetImages[randomNumber]))
        toDropImage.tag = randomNumber
        currentImageInx = dropImages.indexOf(notSetImages[randomNumber])
        for (imageView in dropListeners) {
            var currentImage = currentlySetImages[dropListeners.indexOf(imageView)]
            if (currentImage == R.drawable.ic_not_correct) {
                currentlySetImages.set(dropListeners.indexOf(imageView), R.drawable.ic_simple_square)
            }
            imageView.setImageResource(currentlySetImages[dropListeners.indexOf(imageView)])
        }
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
        runTimer()
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

            val myShadow = MyDragShadowBuilder(v, dropImages[currentImageInx])

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


    private class MyDragShadowBuilder(v: View, imageID: Int) : View.DragShadowBuilder(v) {
        private val shadow = view.getResources().getDrawable(imageID, null)

        override fun onProvideShadowMetrics(size: Point, touch: Point) {
            val width: Int = (view.width / 1.2F).toInt()
            val height: Int = (view.height / 1.2F).toInt()

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
                    imageView.setImageDrawable(ColorDrawable(0x00000000))
                    true
                }

                DragEvent.ACTION_DRAG_LOCATION ->
                    true

                DragEvent.ACTION_DRAG_EXITED -> {
                    var imgInx = dropListeners.indexOf(imageView)
                    imageView.setImageResource(currentlySetImages[imgInx])
                    true
                }

                DragEvent.ACTION_DROP -> {
                    if (imageThatShouldBeDroped == notSetImages[dragedImageInx]) {
                        currentlySetImages.set(dropImages.indexOf(imageThatShouldBeDroped), notSetImages[dragedImageInx])
                        imageView.setImageResource(notSetImages[dragedImageInx])
                        notSetImages.remove(notSetImages[dragedImageInx])
//                        imageView.setBackgroundColor(Color.BLUE)
                        setNextImage()
                    }
                    else {
//                        imageView.setBackgroundColor(0x00000000)
                        imageView.setImageResource(R.drawable.ic_not_correct)
                        currentlySetImages.set(dropListeners.indexOf(imageView), R.drawable.ic_not_correct)
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

    private fun runTimer() {

        // Get the text view.
        val timeView: TextView = findViewById(R.id.stopperView)

        // Creates a new Handler
        val handler = Handler()

        // Call the post() method,
        // passing in a new Runnable.
        // The post() method processes
        // code without a delay,
        // so the code in the Runnable
        // will run almost immediately.
        handler.post(object : Runnable {
            override fun run() {
                val hours: Int = seconds / 3600
                val minutes: Int = seconds % 3600 / 60
                val secs: Int = seconds % 60

                // Format the seconds into hours, minutes,
                // and seconds.
                val time = String
                    .format(
                        Locale.getDefault(),
                        "%d:%02d:%02d", hours,
                        minutes, secs
                    )

                // Set the text view text.
                timeView.text = time

                // If running is true, increment the
                // seconds variable.
                if (running) {
                    seconds++
                }

                // Post the code again
                // with a delay of 1 second.
                handler.postDelayed(this, 1000)
            }
        })
    }
}