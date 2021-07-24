package com.monsterbrain.slideandplaceview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

class DragAndPlaceGameView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private lateinit var imgSrcRect: Rect
    private lateinit var dstRect: Rect
    private var mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var debugPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val numCols = 3
    private val numRows = 3

    private var imgBitmap: Bitmap? = null
    private val pieceRects = mutableListOf<Rect>()

    private val pieces = mutableListOf<PuzzlePiece>()
    private var movingPiece: PuzzlePiece? = null

    private var bufferBitmap: Bitmap? = null
    private var bufferCanvas: Canvas? = null

    inner class PuzzlePiece(
        val srcRect: Rect,
        var destRect: Rect
    ) {
        private val initialRect = Rect(destRect)

        fun moveToTouchPoint(x: Float, y: Float) {
            destRect.offsetTo((x - destRect.width()/2).toInt(), (y -destRect.height()/2).toInt())
        }
    }

    init {
        debugPaint.style = Paint.Style.STROKE
        debugPaint.color = Color.RED
        debugPaint.strokeWidth = 2f
    }

    fun setBitmap(bmp: Bitmap) {
        imgBitmap = bmp
        imgSrcRect = Rect(0, 0, bmp.width, bmp.height)

        invalidate()
    }

    private val imgWidthInPercent = 0.60f
    private val imgHeightInPercent = 0.90f

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        bufferBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        bufferCanvas = Canvas(bufferBitmap!!)

        var imgWidth = imgWidthInPercent * w
        val imgHeight = if (imgWidth > h) h*imgHeightInPercent else imgWidth
        imgWidth = imgHeight // h == w

        val leftMargin = ((w - imgWidth) / 2f).toInt()
        val topMargin = ((h - imgHeight) / 2f).toInt()

        dstRect = Rect(leftMargin, topMargin, leftMargin + imgWidth.toInt(), topMargin + imgHeight.toInt())

        val numPieces = numCols * numRows
        val pieceWidth = (imgWidth / numCols).toInt()

        val srcWidth = imgBitmap?.width ?: 0
        val srcPieceWidth = srcWidth / numCols

        for (i in 0 until numRows) {
            for (j in 0 until numCols) {
                val rect = Rect(
                    leftMargin + (j*pieceWidth),
                    topMargin + (i*pieceWidth),
                    leftMargin + (j+1)*pieceWidth, topMargin + (i+1)*pieceWidth
                )
                pieceRects.add(rect)

                val srcRect = Rect(
                    (j*srcPieceWidth), (i*srcPieceWidth),
                    (j+1)*srcPieceWidth, (i+1)*srcPieceWidth
                )
                val piece = PuzzlePiece(srcRect, rect)
                pieces.add(piece)
            }
        }

        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        imgBitmap?.let {
            // canvas?.drawBitmap(it, imgSrcRect, dstRect, mPaint) //picture to draw
            bufferCanvas?.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

            for (piece in pieces) {
                bufferCanvas?.drawBitmap(it, piece.srcRect, piece.destRect, mPaint) //picture parts to draw
            }
        }

        bufferBitmap?.let { canvas?.drawBitmap(it, 0f, 0f, mPaint) } //buffered bitmap for performance

        drawDebugRects(pieceRects, canvas)
    }

    private fun drawDebugRects(rectList: MutableList<Rect>, canvas: Canvas?) {
        canvas?.let {
            for (rect in rectList) {
                it.drawRect(rect, debugPaint)
            }
        }
    }

    // currently touch is handled in the activity
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                for (piece in pieces) {
                    if (piece.destRect.contains(event.x.toInt(), event.y.toInt())) {
                        movingPiece = piece
                        Log.i("xxy", "touched: $piece")
                        break
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                movingPiece?.let {
                    it.moveToTouchPoint(event.x, event.y)
                    invalidate()
                }
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                invalidate()
            }
        }
        return true
    }
}