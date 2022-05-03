package com.example.puzzle

import android.content.ClipData
import android.content.ClipDescription
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.DragEvent
import android.view.View
import android.widget.ImageView
import java.util.*
import kotlin.collections.ArrayList

import android.os.Handler
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    var toDropImages: ArrayList<ImageView> = arrayListOf()
    var seconds = 0
    var running = true
    var allToDropImageIds: ArrayList<Int> = arrayListOf(
        R.drawable.row_1_column_1, R.drawable.row_1_column_2, R.drawable.row_1_column_3, R.drawable.row_1_column_4,
        R.drawable.row_2_column_1, R.drawable.row_2_column_2, R.drawable.row_2_column_3, R.drawable.row_2_column_4,
        R.drawable.row_3_column_1, R.drawable.row_3_column_2, R.drawable.row_3_column_3, R.drawable.row_3_column_4,
        R.drawable.row_4_column_1, R.drawable.row_4_column_2, R.drawable.row_4_column_3, R.drawable.row_4_column_4
    )
    var notSetImageIds: ArrayList<Int> = arrayListOf(
        R.drawable.row_1_column_1, R.drawable.row_1_column_2, R.drawable.row_1_column_3, R.drawable.row_1_column_4,
        R.drawable.row_2_column_1, R.drawable.row_2_column_2, R.drawable.row_2_column_3, R.drawable.row_2_column_4,
        R.drawable.row_3_column_1, R.drawable.row_3_column_2, R.drawable.row_3_column_3, R.drawable.row_3_column_4,
        R.drawable.row_4_column_1, R.drawable.row_4_column_2, R.drawable.row_4_column_3, R.drawable.row_4_column_4
    )
    var currentlySetImageIds: ArrayList<Int> = arrayListOf(
        R.drawable.ic_simple_square, R.drawable.ic_simple_square, R.drawable.ic_simple_square, R.drawable.ic_simple_square, 
        R.drawable.ic_simple_square, R.drawable.ic_simple_square, R.drawable.ic_simple_square, R.drawable.ic_simple_square, 
        R.drawable.ic_simple_square, R.drawable.ic_simple_square, R.drawable.ic_simple_square, R.drawable.ic_simple_square,
        R.drawable.ic_simple_square, R.drawable.ic_simple_square, R.drawable.ic_simple_square, R.drawable.ic_simple_square
    )
    lateinit var toDropImage: ImageView
    var currentImageInx: Int = -1
    var mediaPlayer: MediaPlayer? = null

    fun getIdOfImageForImageView(imageView: ImageView): Int {
        var inx = toDropImages.indexOf(imageView)
        return allToDropImageIds[inx]
    }

    fun setNextImage() {
        if (mediaPlayer != null) {
            mediaPlayer!!.release()
        }
        if (notSetImageIds.size == 0) {
            toDropImage.setImageResource(R.drawable.ic_empty_image)
            running = false
            return
        }
        val randomNumber = Random().nextInt(notSetImageIds.size)
        var randomImage = notSetImageIds[randomNumber]
        toDropImage.setImageResource(randomImage)
        toDropImage.tag = randomNumber
        currentImageInx = allToDropImageIds.indexOf(randomImage)
        for (imageView in toDropImages) {
            var currentImage = currentlySetImageIds[toDropImages.indexOf(imageView)]
            if (currentImage == R.drawable.ic_not_correct) {
                currentlySetImageIds.set(toDropImages.indexOf(imageView), R.drawable.ic_simple_square)
            }
            imageView.setImageResource(currentlySetImageIds[toDropImages.indexOf(imageView)])
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toDropImage = findViewById(R.id.imageView_toDrop)
        toDropImages.add(findViewById(R.id.imageView_r1c1))
        toDropImages.add(findViewById(R.id.imageView_r1c2))
        toDropImages.add(findViewById(R.id.imageView_r1c3))
        toDropImages.add(findViewById(R.id.imageView_r1c4))
        toDropImages.add(findViewById(R.id.imageView_r2c1))
        toDropImages.add(findViewById(R.id.imageView_r2c2))
        toDropImages.add(findViewById(R.id.imageView_r2c3))
        toDropImages.add(findViewById(R.id.imageView_r2c4))
        toDropImages.add(findViewById(R.id.imageView_r3c1))
        toDropImages.add(findViewById(R.id.imageView_r3c2))
        toDropImages.add(findViewById(R.id.imageView_r3c3))
        toDropImages.add(findViewById(R.id.imageView_r3c4))
        toDropImages.add(findViewById(R.id.imageView_r4c1))
        toDropImages.add(findViewById(R.id.imageView_r4c2))
        toDropImages.add(findViewById(R.id.imageView_r4c3))
        toDropImages.add(findViewById(R.id.imageView_r4c4))

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

            val myShadow = MyDragShadowBuilder(v, allToDropImageIds[currentImageInx])

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                v.startDragAndDrop(dragData, myShadow,null,0)
            } else{
                v.startDrag(dragData, myShadow,null,0)
            }
        }
        for (imv in toDropImages) {
            imv.setOnDragListener(imageDragListener)
        }
    }


    private class MyDragShadowBuilder(v: View, imageID: Int) : View.DragShadowBuilder(v) {
        private val shadow = view.getResources().getDrawable(imageID, null)

        override fun onProvideShadowMetrics(size: Point, touch: Point) {
            val width: Int = (view.width / 1.15F).toInt()
            val height: Int = (view.height / 1.15F).toInt()

            shadow.setBounds(0, 0, width, height)
            size.set(width, height)
            touch.set(3* width / 4, 3 * height / 4)
        }
        override fun onDrawShadow(canvas: Canvas) {
            shadow.draw(canvas)
        }
    }

    private val imageDragListener = View.OnDragListener { v, event ->
        val imageView: ImageView = v as ImageView
        var dragedImageInx: Int = toDropImage.tag as Int
        var imageThatShouldBeDroped = getIdOfImageForImageView(imageView)

        if (notSetImageIds.contains(imageThatShouldBeDroped)) {
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> true
                DragEvent.ACTION_DRAG_LOCATION -> true
                DragEvent.ACTION_DRAG_ENDED -> true

                DragEvent.ACTION_DRAG_ENTERED -> {
                    imageView.setImageDrawable(ColorDrawable(0x00000000))
                    true
                }


                DragEvent.ACTION_DRAG_EXITED -> {
                    var imgInx = toDropImages.indexOf(imageView)
                    imageView.setImageResource(currentlySetImageIds[imgInx])
                    true
                }

                DragEvent.ACTION_DROP -> {
                    if (imageThatShouldBeDroped == notSetImageIds[dragedImageInx]) {
                        currentlySetImageIds.set(allToDropImageIds.indexOf(imageThatShouldBeDroped), notSetImageIds[dragedImageInx])
                        imageView.setImageResource(notSetImageIds[dragedImageInx])
                        notSetImageIds.remove(notSetImageIds[dragedImageInx])
                        setNextImage()
                        mediaPlayer = MediaPlayer.create(this, R.raw.correct)
                        mediaPlayer!!.start()
                    }
                    else {
                        imageView.setImageResource(R.drawable.ic_not_correct)
                        currentlySetImageIds.set(toDropImages.indexOf(imageView), R.drawable.ic_not_correct)
                        mediaPlayer = MediaPlayer.create(this, R.raw.wrong)
                        mediaPlayer!!.start()
                    }
                    true
                }

                else -> false
            }
        }

        true
    }

    private fun runTimer() {
        val timeView: TextView = findViewById(R.id.stopperView)
        val handler = Handler()
        handler.post(object : Runnable {
            override fun run() {
                val hours: Int = seconds / 3600
                val minutes: Int = seconds % 3600 / 60
                val secs: Int = seconds % 60
                val time = String
                    .format(
                        Locale.getDefault(),
                        "%d:%02d:%02d", hours,
                        minutes, secs
                    )
                timeView.text = time
                if (running) {
                    seconds++
                }
                handler.postDelayed(this, 1000)
            }
        })
    }
}