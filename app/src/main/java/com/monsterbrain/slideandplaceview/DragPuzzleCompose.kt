package com.monsterbrain.slideandplaceview

import android.graphics.Bitmap
import android.graphics.Rect
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.math.min

/**
 * Jetpack Compose equivalent of DragAndPlaceGameView
 * Implements a 3x3 drag puzzle game using Compose Canvas and gesture handling
 */
@Composable
fun DragPuzzleCompose(
    bitmap: Bitmap?,
    modifier: Modifier = Modifier,
    debugMode: Boolean = false
) {
    val density = LocalDensity.current
    
    // Game state
    var pieces by remember { mutableStateOf<List<PuzzlePiece>>(emptyList()) }
    var movingPiece by remember { mutableStateOf<PuzzlePiece?>(null) }
    var canvasSize by remember { mutableStateOf(Size.Zero) }
    
    // Constants matching the original implementation
    val numCols = 3
    val numRows = 3
    val imgWidthInPercent = 0.60f
    val imgHeightInPercent = 0.90f
    
    // Initialize pieces when bitmap or canvas size changes
    LaunchedEffect(bitmap, canvasSize) {
        bitmap?.let { bmp ->
            if (canvasSize != Size.Zero) {
                pieces = createPuzzlePieces(
                    bitmap = bmp,
                    canvasSize = canvasSize,
                    numCols = numCols,
                    numRows = numRows,
                    imgWidthInPercent = imgWidthInPercent,
                    imgHeightInPercent = imgHeightInPercent
                )
            }
        }
    }
    
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(pieces) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            // Find which piece was touched
                            movingPiece = pieces.find { piece ->
                                val rect = piece.destRect
                                offset.x >= rect.left && offset.x <= rect.right &&
                                offset.y >= rect.top && offset.y <= rect.bottom
                            }
                        },
                        onDrag = { _, dragAmount ->
                            // Update the position of the moving piece
                            movingPiece?.let { piece ->
                                val newRect = androidx.compose.ui.geometry.Rect(
                                    left = piece.destRect.left + dragAmount.x,
                                    top = piece.destRect.top + dragAmount.y,
                                    right = piece.destRect.right + dragAmount.x,
                                    bottom = piece.destRect.bottom + dragAmount.y
                                )
                                
                                // Update pieces list with new position
                                pieces = pieces.map { p ->
                                    if (p == piece) {
                                        p.copy(destRect = newRect)
                                    } else {
                                        p
                                    }
                                }
                                
                                // Update the moving piece reference
                                movingPiece = pieces.find { p -> p == piece }
                            }
                        },
                        onDragEnd = {
                            movingPiece = null
                        }
                    )
                }
        ) { size ->
            canvasSize = size
            
            bitmap?.let { bmp ->
                drawPuzzle(
                    bitmap = bmp.asImageBitmap(),
                    pieces = pieces,
                    debugMode = debugMode
                )
            }
        }
    }
}

/**
 * Data class representing a puzzle piece
 */
data class PuzzlePiece(
    val srcRect: androidx.compose.ui.geometry.Rect,
    val destRect: androidx.compose.ui.geometry.Rect,
    val id: Int
)

/**
 * Creates the initial puzzle pieces based on the bitmap and canvas size
 */
private fun createPuzzlePieces(
    bitmap: Bitmap,
    canvasSize: Size,
    numCols: Int,
    numRows: Int,
    imgWidthInPercent: Float,
    imgHeightInPercent: Float
): List<PuzzlePiece> {
    val pieces = mutableListOf<PuzzlePiece>()
    
    // Calculate image dimensions and position (matching original logic)
    var imgWidth = imgWidthInPercent * canvasSize.width
    val imgHeight = if (imgWidth > canvasSize.height) {
        canvasSize.height * imgHeightInPercent
    } else {
        imgWidth
    }
    imgWidth = imgHeight // Make it square like original
    
    val leftMargin = (canvasSize.width - imgWidth) / 2f
    val topMargin = (canvasSize.height - imgHeight) / 2f
    
    val pieceWidth = imgWidth / numCols
    val pieceHeight = imgHeight / numRows
    
    val srcWidth = bitmap.width.toFloat()
    val srcHeight = bitmap.height.toFloat()
    val srcPieceWidth = srcWidth / numCols
    val srcPieceHeight = srcHeight / numRows
    
    for (i in 0 until numRows) {
        for (j in 0 until numCols) {
            val id = i * numCols + j
            
            // Destination rectangle (where to draw on screen)
            val destRect = androidx.compose.ui.geometry.Rect(
                left = leftMargin + (j * pieceWidth),
                top = topMargin + (i * pieceHeight),
                right = leftMargin + ((j + 1) * pieceWidth),
                bottom = topMargin + ((i + 1) * pieceHeight)
            )
            
            // Source rectangle (where to read from bitmap)
            val srcRect = androidx.compose.ui.geometry.Rect(
                left = j * srcPieceWidth,
                top = i * srcPieceHeight,
                right = (j + 1) * srcPieceWidth,
                bottom = (i + 1) * srcPieceHeight
            )
            
            pieces.add(PuzzlePiece(srcRect, destRect, id))
        }
    }
    
    return pieces
}

/**
 * Draws the puzzle pieces on the canvas
 */
private fun DrawScope.drawPuzzle(
    bitmap: ImageBitmap,
    pieces: List<PuzzlePiece>,
    debugMode: Boolean
) {
    // Clear the canvas (similar to buffer clearing in original)
    drawRect(Color.Transparent, size = size)
    
    // Draw each piece
    pieces.forEach { piece ->
        drawImage(
            image = bitmap,
            srcOffset = androidx.compose.ui.unit.IntOffset(
                piece.srcRect.left.toInt(),
                piece.srcRect.top.toInt()
            ),
            srcSize = androidx.compose.ui.unit.IntSize(
                (piece.srcRect.right - piece.srcRect.left).toInt(),
                (piece.srcRect.bottom - piece.srcRect.top).toInt()
            ),
            dstOffset = androidx.compose.ui.unit.IntOffset(
                piece.destRect.left.toInt(),
                piece.destRect.top.toInt()
            ),
            dstSize = androidx.compose.ui.unit.IntSize(
                (piece.destRect.right - piece.destRect.left).toInt(),
                (piece.destRect.bottom - piece.destRect.top).toInt()
            )
        )
        
        // Draw debug rectangles if enabled
        if (debugMode) {
            drawRect(
                color = Color.Red,
                topLeft = Offset(piece.destRect.left, piece.destRect.top),
                size = Size(
                    piece.destRect.right - piece.destRect.left,
                    piece.destRect.bottom - piece.destRect.top
                ),
                style = Stroke(width = 2.dp.toPx())
            )
        }
    }
}