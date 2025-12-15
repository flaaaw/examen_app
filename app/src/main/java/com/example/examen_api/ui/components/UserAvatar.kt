package com.example.examen_api.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest

@Composable
fun UserAvatar(
    name: String,
    image: String? = null,
    size: Dp = 40.dp,
    fontSize: Int = 18
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        if (image != null) {
            val imageUrl = image.let { url ->
                if (url.contains("localhost") || url.contains("127.0.0.1")) {
                    url.replace("localhost", "10.0.2.2").replace("127.0.0.1", "10.0.2.2")
                } else {
                    url
                }
            }
            
            // Debug: Log the URL being loaded
            android.util.Log.d("UserAvatar", "Loading image: $imageUrl")
            
            AsyncImage(
                model = ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .listener(
                        onError = { _, result ->
                            android.util.Log.e("UserAvatar", "Failed to load image: $imageUrl - Error: ${result.throwable.message}")
                        }
                    )
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(
                text = name.firstOrNull()?.toString()?.uppercase() ?: "#",
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = fontSize.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
