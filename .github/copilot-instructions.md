# Copilot Instructions - Android Drag Picture Puzzle Game

## Project Overview

This is a native Android application built with Kotlin that implements a drag-and-drop picture puzzle game. The game splits an image into a 3x3 grid and allows users to drag pieces around to solve the puzzle.

## Architecture & Technology Stack

- **Language**: Kotlin
- **UI Framework**: Android Views (not Jetpack Compose)
- **Minimum SDK**: 16 (Android 4.1)
- **Target SDK**: 31 (Android 12)
- **Key Libraries**: AndroidX, Material Design Components
- **Build System**: Gradle with Android Gradle Plugin

## Project Structure

```
app/
├── src/main/
│   ├── AndroidManifest.xml           # App configuration (landscape orientation set)
│   ├── java/com/monsterbrain/slideandplaceview/
│   │   ├── DragAndPlaceGameView.kt   # Main custom view (puzzle game logic)
│   │   └── MainActivity.kt           # Simple activity setup
│   └── res/
│       ├── drawable/
│       │   └── mountain.png          # Default puzzle image
│       ├── layout/
│       │   └── activity_main.xml     # ConstraintLayout with custom view
│       └── values/                   # App themes and resources
├── build.gradle                     # Module-level build configuration
└── proguard-rules.pro               # ProGuard configuration for release builds
```

## Debugging & Development Tips

### Enable Debug Mode
The custom view has built-in debug rectangle drawing. To enable/disable:
```kotlin
// In onDraw method, this line shows piece boundaries:
drawDebugRects(pieceRects, canvas)
// Comment out or add conditional logic for production builds
```

### Logging Touch Events
Add logging to understand touch behavior:
```kotlin
override fun onTouchEvent(event: MotionEvent?): Boolean {
    Log.d("TouchEvent", "Action: ${event?.action}, X: ${event?.x}, Y: ${event?.y}")
    // ... rest of touch handling
}
```

### Performance Monitoring
Monitor custom view performance:
```kotlin
override fun onDraw(canvas: Canvas?) {
    val startTime = System.currentTimeMillis()
    // ... drawing code ...
    val endTime = System.currentTimeMillis()
    Log.d("Performance", "Draw time: ${endTime - startTime}ms")
}
```

## Key Constants & Configuration

```kotlin
// In DragAndPlaceGameView.kt:
private val numCols = 3                    // Puzzle columns (change for different difficulty)
private val numRows = 3                    // Puzzle rows (change for different difficulty)  
private val imgWidthInPercent = 0.60f      // Image size relative to screen width
private val imgHeightInPercent = 0.90f     // Image size relative to screen height
```

## Game Mechanics & Algorithms

### Piece Creation Algorithm
1. Calculate screen dimensions and image scaling in `onSizeChanged()`
2. Divide source bitmap into `numCols × numRows` rectangles
3. Create corresponding screen rectangles with proper scaling
4. Initialize `PuzzlePiece` objects with source and destination rectangles

### Touch Detection Algorithm
```kotlin
// Find touched piece by checking if point is inside any piece rectangle
for (piece in pieces) {
    if (piece.destRect.contains(event.x.toInt(), event.y.toInt())) {
        movingPiece = piece
        break
    }
}
```

### Piece Movement Algorithm
```kotlin
fun moveToTouchPoint(x: Float, y: Float) {
    // Center piece on touch point
    destRect.offsetTo(
        (x - destRect.width()/2).toInt(), 
        (y - destRect.height()/2).toInt()
    )
}
```

### Rendering Pipeline
1. **Buffer Clear**: Clear the off-screen buffer canvas
2. **Piece Rendering**: Draw each piece from source bitmap to buffer using `drawBitmap()`
3. **Buffer Blit**: Copy entire buffer to main canvas in one operation
4. **Debug Overlay**: Optionally draw debug rectangles on main canvas

### Memory Management Strategy
- Use single `bufferBitmap` for all drawing operations
- Reuse `Paint` objects to avoid allocations in draw loop
- Maintain piece rectangles as mutable lists that are modified in-place
- Avoid creating new objects in `onDraw()` or touch event handlers

## Key Components

### 1. DragAndPlaceGameView (Custom View)
**File**: `app/src/main/java/com/monsterbrain/slideandplaceview/DragAndPlaceGameView.kt`

This is the core component that handles the puzzle game logic:

- **Inheritance**: Extends `View` class
- **Functionality**: 
  - Splits input bitmap into 3x3 grid pieces
  - Handles touch events for dragging pieces
  - Manages piece positioning and movement
  - Implements double buffering for smooth performance
  - Provides debug drawing for development

**Key Methods**:
- `setBitmap(bmp: Bitmap)`: Sets the puzzle image
- `onSizeChanged()`: Calculates piece dimensions and positions
- `onDraw()`: Renders the puzzle pieces using double buffering
- `onTouchEvent()`: Handles drag-and-drop interactions

### 2. MainActivity
**File**: `app/src/main/java/com/monsterbrain/slideandplaceview/MainActivity.kt`

Simple activity that:
- Sets up the game view
- Loads the puzzle image from resources
- Forces landscape orientation

### 3. Layout Structure
**File**: `app/src/main/res/layout/activity_main.xml`

- Uses `ConstraintLayout` as root container
- Contains the custom `DragAndPlaceGameView` filling the screen
- Includes a "Hello World!" TextView (may be removed in production)

## Development Guidelines

### Custom View Patterns

When working with the `DragAndPlaceGameView`:

1. **Performance Optimization**: The view uses double buffering (bufferBitmap/bufferCanvas) to avoid flickering during animations
2. **Touch Handling**: Follow the ACTION_DOWN → ACTION_MOVE → ACTION_UP pattern for smooth interactions
3. **Coordinate Systems**: Be aware of the mapping between source bitmap coordinates and screen coordinates

### Code Style & Conventions

- Use Kotlin idioms and nullable safety
- Follow Android naming conventions
- Keep view logic focused on UI concerns
- Use inner classes for closely related functionality (like `PuzzlePiece`)

### Key Classes and Data Structures

```kotlin
// Puzzle piece representation
inner class PuzzlePiece(
    val srcRect: Rect,          // Source rectangle in original bitmap
    var destRect: Rect          // Destination rectangle on screen
)

// Important member variables
private val pieces = mutableListOf<PuzzlePiece>()  // All puzzle pieces
private var movingPiece: PuzzlePiece? = null       // Currently dragged piece
private var bufferBitmap: Bitmap? = null           // Double buffer for performance
```

## Common Development Tasks

### Adding New Features

1. **Changing Grid Size**: Modify `numCols` and `numRows` constants in `DragAndPlaceGameView`
2. **Adding Sound Effects**: Implement in touch event handlers (ACTION_DOWN, ACTION_UP)
3. **Win Condition**: Add logic to check if pieces are in correct positions
4. **Score System**: Track moves, time, or completion percentage

### Customization Points

- **Puzzle Dimensions**: Adjust `imgWidthInPercent` and `imgHeightInPercent` for different sizing
- **Debug Mode**: Toggle debug rectangle drawing via `drawDebugRects()`
- **Touch Sensitivity**: Modify touch detection logic in `onTouchEvent()`
- **Visual Effects**: Add animations, shadows, or highlighting in `onDraw()`

### Performance Considerations

1. **Bitmap Management**: Always recycle large bitmaps when done
2. **Canvas Operations**: Minimize complex drawing operations in `onDraw()`
3. **Touch Events**: Avoid heavy calculations in `ACTION_MOVE` handler
4. **Memory Usage**: Be mindful of bitmap sizes for different screen densities

## Testing Strategies

### Manual Testing
- Test on different screen sizes and orientations
- Verify touch responsiveness across the puzzle area
- Check piece positioning accuracy
- Validate image scaling and aspect ratio preservation

### Automated Testing (if implementing)
- Unit tests for piece positioning calculations
- UI tests for touch interaction scenarios
- Performance tests for smooth animation

## Common Issues & Solutions

### Touch Event Problems
- **Issue**: Pieces don't respond to touch
- **Solution**: Check if `onTouchEvent()` returns `true` and verify bounds checking

### Performance Issues
- **Issue**: Lag during dragging
- **Solution**: Optimize `onDraw()` method and ensure proper double buffering

### Layout Problems
- **Issue**: Custom view not displaying correctly
- **Solution**: Verify `onSizeChanged()` calculations and constraint layout setup

## Extension Ideas

1. **Shuffle Animation**: Animate pieces to random positions at game start
2. **Snap-to-Grid**: Add magnetic effect when pieces are near correct positions
3. **Multiple Difficulty Levels**: Support 2x2, 4x4, 5x5 grids
4. **Image Selection**: Allow users to choose puzzle images from gallery
5. **Timer and Scoring**: Add competitive elements
6. **Multiplayer**: Network-based multiplayer puzzle solving

## Development Environment Setup

1. Use Android Studio with Kotlin support enabled
2. Ensure Android SDK 31+ is installed
3. Test on both emulator and physical devices
4. Consider using different screen densities for testing

## Specific Implementation Details

### Touch Event Handling Pattern
```kotlin
override fun onTouchEvent(event: MotionEvent?): Boolean {
    when (event!!.action) {
        MotionEvent.ACTION_DOWN -> {
            // Find which piece was touched
            // Set movingPiece variable
        }
        MotionEvent.ACTION_MOVE -> {
            // Update piece position
            // Call invalidate() to trigger redraw
        }
        MotionEvent.ACTION_UP -> {
            // Handle piece drop logic
            // Check for valid placement
        }
    }
    return true // Always consume touch events
}
```

### Canvas Drawing Pattern
```kotlin
override fun onDraw(canvas: Canvas?) {
    // 1. Clear buffer canvas
    bufferCanvas?.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
    
    // 2. Draw all pieces to buffer
    for (piece in pieces) {
        bufferCanvas?.drawBitmap(bitmap, piece.srcRect, piece.destRect, paint)
    }
    
    // 3. Draw buffer to main canvas
    bufferBitmap?.let { canvas?.drawBitmap(it, 0f, 0f, paint) }
    
    // 4. Optional: Draw debug overlays
    drawDebugRects(pieceRects, canvas)
}
```

### Coordinate Calculation Patterns
- Use `Rect.offsetTo()` for absolute positioning
- Use `Rect.offset()` for relative movement  
- Always consider piece width/height when centering on touch point
- Map between source bitmap coordinates and screen coordinates carefully

## Code Review Checklist

- [ ] Custom view properly handles all touch events and returns `true`
- [ ] Memory leaks avoided (proper bitmap recycling, buffer management)
- [ ] Performance optimized (double buffering used, minimal `onDraw` calculations)
- [ ] Edge cases handled (empty bitmaps, rotation, out-of-bounds touches)
- [ ] Code follows Kotlin conventions and nullable safety
- [ ] Comments explain complex game logic and coordinate transformations
- [ ] Debug features can be easily disabled for production builds
- [ ] Touch sensitivity appropriate for different screen densities
- [ ] Piece positioning calculations handle screen size changes properly

## Resources & References

- [Android Custom Views Documentation](https://developer.android.com/guide/topics/ui/custom-components)
- [Canvas and Drawables Guide](https://developer.android.com/guide/topics/graphics/2d-graphics)
- [Touch and Input Handling](https://developer.android.com/guide/topics/ui/ui-events)
- [Performance Best Practices](https://developer.android.com/topic/performance)