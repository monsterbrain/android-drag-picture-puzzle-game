# Usage Examples - Jetpack Compose Drag Puzzle

This document provides examples of how to use the `DragPuzzleCompose` composable with different image sources.

## Basic Usage

### Using a Resource Drawable
```kotlin
@Composable
fun MyPuzzleScreen() {
    val context = LocalContext.current
    val bitmap = remember {
        BitmapFactory.decodeResource(context.resources, R.drawable.my_image)
    }
    
    DragPuzzleCompose(
        bitmap = bitmap,
        modifier = Modifier.fillMaxSize(),
        debugMode = false
    )
}
```

### Using an Asset File
```kotlin
@Composable
fun AssetPuzzleScreen() {
    val context = LocalContext.current
    val bitmap = remember {
        context.assets.open("puzzle_images/nature.jpg").use { inputStream ->
            BitmapFactory.decodeStream(inputStream)
        }
    }
    
    DragPuzzleCompose(
        bitmap = bitmap,
        modifier = Modifier.fillMaxSize()
    )
}
```

### Using a File from Storage
```kotlin
@Composable
fun FilePuzzleScreen(imagePath: String) {
    val bitmap = remember(imagePath) {
        BitmapFactory.decodeFile(imagePath)
    }
    
    if (bitmap != null) {
        DragPuzzleCompose(
            bitmap = bitmap,
            modifier = Modifier.fillMaxSize()
        )
    } else {
        Text("Failed to load image")
    }
}
```

## Advanced Usage

### With Image Picker
```kotlin
@Composable
fun InteractivePuzzleScreen() {
    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            // Load bitmap from URI
            selectedBitmap = loadBitmapFromUri(context, it)
        }
    }
    
    Column {
        Button(onClick = { launcher.launch("image/*") }) {
            Text("Select Image")
        }
        
        selectedBitmap?.let { bitmap ->
            DragPuzzleCompose(
                bitmap = bitmap,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
```

### With Different Difficulty Levels
```kotlin
@Composable
fun ConfigurablePuzzle(
    bitmap: Bitmap?,
    gridSize: Int = 3
) {
    // Note: Current implementation is hardcoded to 3x3
    // This example shows how it could be extended
    
    var debugMode by remember { mutableStateOf(false) }
    
    Column {
        Row {
            Text("Grid Size: ${gridSize}x${gridSize}")
            Switch(
                checked = debugMode,
                onCheckedChange = { debugMode = it }
            )
        }
        
        bitmap?.let {
            DragPuzzleCompose(
                bitmap = it,
                modifier = Modifier.fillMaxSize(),
                debugMode = debugMode
            )
        }
    }
}
```

### With State Persistence
```kotlin
@Composable
fun PersistentPuzzleScreen() {
    val context = LocalContext.current
    var gameState by rememberSaveable { mutableStateOf("") }
    
    val bitmap = remember {
        BitmapFactory.decodeResource(context.resources, R.drawable.mountain)
    }
    
    // Save/restore puzzle state
    LaunchedEffect(gameState) {
        // Load saved state if available
    }
    
    DragPuzzleCompose(
        bitmap = bitmap,
        modifier = Modifier.fillMaxSize()
        // Could add onStateChange callback for persistence
    )
}
```

## Error Handling

### Safe Bitmap Loading
```kotlin
@Composable
fun SafePuzzleScreen(imageResource: Int) {
    val context = LocalContext.current
    val bitmap = remember {
        try {
            BitmapFactory.decodeResource(context.resources, imageResource)
        } catch (e: Exception) {
            null
        }
    }
    
    if (bitmap != null) {
        DragPuzzleCompose(
            bitmap = bitmap,
            modifier = Modifier.fillMaxSize()
        )
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Failed to load puzzle image")
        }
    }
}
```

### Memory Management
```kotlin
@Composable
fun MemoryEfficientPuzzle(imageUri: Uri?) {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current
    
    LaunchedEffect(imageUri) {
        imageUri?.let { uri ->
            // Load bitmap in background
            withContext(Dispatchers.IO) {
                try {
                    val options = BitmapFactory.Options().apply {
                        inSampleSize = 2 // Reduce memory usage
                        inPreferredConfig = Bitmap.Config.RGB_565
                    }
                    bitmap = BitmapFactory.decodeStream(
                        context.contentResolver.openInputStream(uri),
                        null,
                        options
                    )
                } catch (e: Exception) {
                    bitmap = null
                }
            }
        }
    }
    
    // Clean up bitmap when composable is disposed
    DisposableEffect(bitmap) {
        onDispose {
            bitmap?.recycle()
        }
    }
    
    bitmap?.let {
        DragPuzzleCompose(
            bitmap = it,
            modifier = Modifier.fillMaxSize()
        )
    }
}
```

## Custom Theming

### Themed Puzzle Screen
```kotlin
@Composable
fun ThemedPuzzleScreen() {
    val darkTheme = isSystemInDarkTheme()
    
    MaterialTheme(
        colorScheme = if (darkTheme) darkColorScheme() else lightColorScheme()
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val bitmap = remember {
                BitmapFactory.decodeResource(
                    LocalContext.current.resources,
                    R.drawable.mountain
                )
            }
            
            DragPuzzleCompose(
                bitmap = bitmap,
                modifier = Modifier.fillMaxSize(),
                debugMode = false
            )
        }
    }
}
```

## Integration Tips

1. **Image Quality**: Use high-resolution images for better visual quality
2. **Memory**: Consider image dimensions and memory usage for large bitmaps
3. **Performance**: Load images on background threads for large files
4. **Accessibility**: Add content descriptions for accessibility
5. **Error Handling**: Always handle bitmap loading failures gracefully

## Supported Image Formats

The `DragPuzzleCompose` supports any bitmap that can be loaded by Android's `BitmapFactory`:

- **JPEG** (.jpg, .jpeg)
- **PNG** (.png) 
- **WebP** (.webp)
- **BMP** (.bmp)
- **GIF** (.gif) - static images only

## Performance Considerations

- **Large Images**: Consider downscaling very large images
- **Memory Usage**: Monitor bitmap memory consumption
- **Smooth Animation**: The Compose implementation handles recomposition efficiently
- **Touch Responsiveness**: Gesture handling is optimized for smooth dragging