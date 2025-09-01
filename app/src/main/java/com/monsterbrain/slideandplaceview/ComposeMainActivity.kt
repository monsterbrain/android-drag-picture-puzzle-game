package com.monsterbrain.slideandplaceview

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Activity demonstrating the Compose version of the drag puzzle game
 */
class ComposeMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            DragPuzzleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DragPuzzleScreen()
                }
            }
        }
    }
}

@Composable
fun DragPuzzleScreen() {
    val context = LocalContext.current
    var debugMode by remember { mutableStateOf(false) }
    
    // Load the mountain bitmap
    val bitmap = remember {
        BitmapFactory.decodeResource(context.resources, R.drawable.mountain)
    }
    
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Debug toggle button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Compose Drag Puzzle",
                style = MaterialTheme.typography.headlineSmall
            )
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Debug Mode")
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = debugMode,
                    onCheckedChange = { debugMode = it }
                )
            }
        }
        
        // The puzzle game
        DragPuzzleCompose(
            bitmap = bitmap,
            modifier = Modifier.weight(1f),
            debugMode = debugMode
        )
        
        // Instructions
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Text(
                text = "Drag the puzzle pieces to rearrange them. " +
                        "This is a Jetpack Compose version of the original View-based implementation.",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun DragPuzzleTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF6200EE),
            secondary = Color(0xFF03DAC6)
        ),
        content = content
    )
}

@Preview(showBackground = true)
@Composable
fun DragPuzzleScreenPreview() {
    DragPuzzleTheme {
        DragPuzzleScreen()
    }
}