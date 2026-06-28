package com.example.productlistview.ui.screens.products

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.productlistview.ui.screens.products.ProductsUiState
import com.example.productlistview.R
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.example.productlistview.data.model.chat.InternetProductsItemState
import kotlinx.coroutines.delay
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.productlistview.data.model.chat.ChatMessage


@Composable
fun ProductsScreen(contentPadding: PaddingValues,
                   productsViewModel: ProductsViewModel,
                   //retryAction: () -> Unit
                   onCheckoutClick: () -> Unit
) {

    /*LaunchedEffect(Unit) {
        productsViewModel.searchProducts("")
    }*/
    val chatMessages by productsViewModel.chatMessages.collectAsState()
    //var searchText by remember { mutableStateOf("") }
    val internetUiState by productsViewModel.internetUiState.collectAsState()
    //val keyboardController = LocalSoftwareKeyboardController.current

    Column(modifier = Modifier.fillMaxSize().padding(top = contentPadding.calculateTopPadding()),) {
       /* // Search Bar Setup
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("Search Products") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    productsViewModel.searchProducts(searchText)
                    keyboardController?.hide() // FIX 2: Closes keyboard automatically on search click
                }
            ),
            singleLine = true
        )*/
        ChatSection(
            messages = chatMessages,
            internetUiState = internetUiState,
            productsViewModel = productsViewModel,
            //retryAction = retryAction,
            onSendMessage = { query ->
                productsViewModel.sendMessageToAi(query)
            },

            modifier = Modifier.weight(1f),
            onCheckoutClick = onCheckoutClick
        )

        // 4. Render the state below the search bar
        /*Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            when (val state = internetUiState) {
                is InternetUiState.Loading -> LoadingScreen()
                is InternetUiState.Error -> ErrorScreen(//retryAction = retryAction
                )
                is InternetUiState.Success -> ResultScreen(
                    products = state.items,
                    productsViewModel = productsViewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }*/
    }
}

    /*when (val state = internetUiState) {
        is InternetUiState.Loading -> LoadingScreen(modifier = Modifier.fillMaxWidth())
        is InternetUiState.Error -> ErrorScreen(retryAction,modifier = Modifier.fillMaxWidth())
        is InternetUiState.Success -> ResultScreen(
                products = state.items,
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                contentPadding = contentPadding,
                productsViewModel = productsViewModel
        )
    }
}*/

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Image(
        modifier = modifier.size(200.dp),
        painter = painterResource(R.drawable.loading_img),
        contentDescription = "Loadeing"
    )
}

@Composable
fun ErrorScreen(
    //retryAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(), // Added fillMaxSize to center it on screen
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_connection_error),
            contentDescription = "Connection Error"
        )
        Text(
            text = "Failed to load",
            modifier = Modifier.padding(16.dp)
        )
        /*Button(onClick = retryAction) {
            Text("Retry")
        }*/
    }
}
@Composable
fun ResultScreen(
    productsViewModel: ProductsViewModel,
    products: Map<String, List<InternetProductsItemState>>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
    ) {
        products.forEach { (categoryName, productsInCategory) ->

            item(key = categoryName) {
                Text(
                    text = categoryName,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            item {
                ProductList(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    products = productsInCategory,
                    onCountChange = { id, count -> productsViewModel.updateInternetCount(id, count) },
                    onExpandToggle = { id -> productsViewModel.toggleInternetExpand(id) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}


@Composable
fun ChatSection(
    messages: List<ChatMessage>,
    internetUiState: ProductsUiState,
    productsViewModel: ProductsViewModel,
    onSendMessage: (String) -> Unit,
    onCheckoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val selectedProducts = remember(messages) {
        messages.flatMap { it.products.values.flatten().filter { it.count > 0 } }
    }
    val hasExpandedItems = selectedProducts.isNotEmpty()

    // 🟢 جایگزینی Scaffold سوم با یک Column ساده و هوشمند
    Column(
        modifier = modifier
            .fillMaxSize()
            //.navigationBarsPadding()
            // 🟢 این دستور به کامپوز می‌گوید با باز شدن کیبورد، کل این ستون را به نرمی بالا بکش
            .imePadding()
    ) {
        // ۱. لیست اصلی چت
        LazyColumn(
            modifier = Modifier
                .weight(1f) // 🟢 وزن ۱ باعث می‌شود چت کل فضای خالی را بگیرد و باکس متن را به پایین هل دهد
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            reverseLayout = false
        ) {
            // رندر پیام‌ها
            items(messages, key = { it.id }) { message ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (message.text.isNotBlank() || message.isStreaming || message.statusMessage != null) {
                        ChatBubble(message = message)
                    }
                    if (message.products.isNotEmpty()) {
                        message.products.forEach { (categoryName, productsInCategory) ->
                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                                Text(
                                    text = categoryName,
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                            ProductList(
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                products = productsInCategory,
                                onCountChange = { id, count -> productsViewModel.updateInternetCount(id, count) },
                                onExpandToggle = { id -> productsViewModel.toggleInternetExpand(id) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            // لودینگ اولیه
            if (internetUiState.isInitialLoading && messages.isNotEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                }
            }

            // ارور شبکه
            if (internetUiState.isError) {
                item {
                    ErrorScreen(modifier = Modifier.fillMaxWidth())
                }
            }
        }

        // ۲. باکس ورودی متن (چسبیده به انتهای صفحه)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = 5.dp,
            color = MaterialTheme.colorScheme.surface,
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        // 🟢 پدینگ اضافی نویگیشن‌بار حذف شد تا فاصله سفید پایین از بین برود
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AnimatedVisibility(
                        visible = hasExpandedItems,
                        enter = fadeIn() + expandHorizontally(),
                        exit = fadeOut() + shrinkHorizontally()
                    ) {
                        Button(
                            onClick = { onCheckoutClick() },
                            modifier = Modifier.padding(end = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                            ),
                            shape = RoundedCornerShape(24.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Text(
                                text = "سبد خرید (${selectedProducts.sumOf { it.count }})",
                                style = MaterialTheme.typography.labelLarge,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }

                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text(if (hasExpandedItems) "ادامه خرید..." else "چی لازم داری؟ بنویس برام...") },
                        shape = RoundedCornerShape(24.dp),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                onSendMessage(inputText.trim())
                                inputText = ""
                                keyboardController?.hide()
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send Message"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val alignment = if (message.isUser) Alignment.End else Alignment.Start
    val backgroundColor = if (message.isUser) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val textColor = if (message.isUser) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    val bubbleShape = if (message.isUser) {
        RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp)
    } else {
        RoundedCornerShape(20.dp, 20.dp, 20.dp, 4.dp)
    }

    val infiniteTransition = rememberInfiniteTransition(label = "cursor_blink")
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursor_alpha"
    )

    val finalAnnotatedText = buildAnnotatedString {
        // 🟢 اصلاح اصلی: استفاده از متد مناسب برای تزریق یک AnnotatedString دیگر
        val parsedMarkdown = parseMarkdownToAnnotatedString(message.text)
        append(parsedMarkdown)

        // نمایش نشانگر چشمک‌زن
        if (message.isStreaming && message.text.isNotEmpty()) {
            pushStyle(MaterialTheme.typography.bodyLarge.toSpanStyle().copy(
                color = textColor.copy(alpha = cursorAlpha)
            ))
            append(" ▌")
            pop()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = alignment
    ) {
        if (message.statusMessage != null && !message.isUser) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 4.dp)) {
                CircularProgressIndicator(modifier = Modifier.size(12.dp), strokeWidth = 2.dp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = message.statusMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.width(50.dp)
                )
            }
        }
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(bubbleShape)
                .background(backgroundColor)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(
                text = finalAnnotatedText,
                color = textColor,
                style = MaterialTheme.typography.bodyLarge.copy(
                    textDirection = if (message.isUser) TextDirection.Content else TextDirection.Rtl
                )
            )
        }
    }
}
@Composable
fun ProductList(
    products: List<InternetProductsItemState>,
    contentPadding: PaddingValues,
    onCountChange: (Int, Int) -> Unit,
    onExpandToggle: (Int) -> Unit,
    modifier: Modifier = Modifier
    ) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val dynamicCardWidth = screenWidth * 0.45f

        LazyRow(
            modifier = modifier,
            contentPadding = contentPadding
        ) {
            items(products, key = { it.product.id }) { item ->
                ProductCard(
                    item = item,
                    //uiState = ,
                    onCountChange = { newCount -> onCountChange(item.product.id, newCount) },
                    onExpandToggle = { onExpandToggle(item.product.id) },
                    modifier = Modifier
                        .padding(8.dp)
                        .width(dynamicCardWidth) // Apply it dynamically here!
                )
            }
        }
    }
@Composable
fun ProductCard(
    item: InternetProductsItemState,
    onCountChange: (Int) -> Unit,
    onExpandToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color by animateColorAsState(
        targetValue = if (item.isExpanded) MaterialTheme.colorScheme.tertiaryContainer else Color.White,
    )

    Card(
        modifier = modifier,
        onClick = onExpandToggle,
        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 30.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
                .background(color = color)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context = LocalContext.current)
                        .data(item.product.image)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    error = painterResource(R.drawable.ic_broken_image),
                    placeholder = painterResource(R.drawable.loading_img),
                    modifier = Modifier.fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(10.dp))
                )
                if (item.product.llmGuide != null) {
                    val badgeText = when (item.product.llmGuide) {
                        "recommended" -> "پیشنهادی"
                        "best quality", "best_quality" -> "بهترین کیفیت"
                        else -> null
                    }

                    if (badgeText != null) {
                        Surface(
                            modifier = Modifier
                                .padding(8.dp)
                                .align(Alignment.TopStart), // تنظیم دقیق در بالا چپ تصویر
                            color = if (item.product.llmGuide == "recommended") Color(0xFF4CAF50) else Color(0xFFFF9800), // سبز برای پیشنهادی، نارنجی برای بهترین
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = badgeText,
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

            }



            // 1. Create a state to control whether the marquee is running
            var runMarquee by remember(item.product.id) { mutableStateOf(true) }

// 2. Automatically turn off the marquee after it has had time to run once
            LaunchedEffect(item.product.id) {
                // 1200ms initial delay + 3500ms scrolling time = 4.7 seconds total
                delay(4700)
                runMarquee = false
            }

            val productTitle = listOfNotNull(
                item.product.productName,
                item.product.brand
            ).filter { it.isNotBlank() }
                .joinToString(" ")

            Text(
                text = productTitle.ifBlank { "Unknown Product" },
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    // 3. ONLY apply marquee if it's running AND the card is collapsed
                    .then(
                        if (runMarquee && !item.isExpanded) {
                            Modifier.basicMarquee(
                                iterations = 1,
                                initialDelayMillis = 1200,
                                velocity = 40.dp
                            )
                        } else {
                            Modifier // Empty modifier when stopped or expanded
                        }
                    ),
                style = MaterialTheme.typography.titleMedium,
                maxLines = if (item.isExpanded) 3 else 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.product.priceAfterOff?.toString() ?: "0",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Surface(
                    color = Color.Red,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${item.product.offPercent ?: 0}%",
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp), // Matched horizontal padding to 16.dp for perfect alignment
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = item.product.price?.toString() ?: "0",
                    modifier = Modifier.weight(1f),
                    // FIXED: Changed headlineSmall to bodyMedium so long strikethrough prices don't crowd out the button
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Gray,
                        textDecoration = TextDecoration.LineThrough
                    ),
                    maxLines = 1
                )
                ProductButton(
                    expanded = item.isExpanded,
                    onClick = onExpandToggle
                )
            }

            if (item.isExpanded) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center, // Centered counter controls layout cleanly
                    modifier = Modifier
                        .fillMaxWidth() // Fills card width evenly when expanded
                        .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
                ) {
                    IconButton(
                        onClick = {
                            if (item.count > 1) {
                                onCountChange(item.count - 1)
                            } else if (item.count == 1) {
                                onCountChange(0)
                                onExpandToggle()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (item.count > 1) Icons.Default.Remove else Icons.Default.Delete,
                            contentDescription = "Decrease",
                            tint = Color.Red
                        )
                    }

                    Text(
                        text = item.count.toString(),
                        // FIXED: Replaced hardcoded 24.sp with Material style for reliable system font scaling
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Red,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    IconButton(
                        onClick = { onCountChange(item.count + 1) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase",
                            tint = Color.Red
                        )
                    }
                }
            }
        }
    }
}
@Composable
private fun ProductButton(
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary
        )

    }


}


fun parseMarkdownToAnnotatedString(text: String): AnnotatedString {
    return buildAnnotatedString {
        // این ریجکس بخش‌های بولد (**...**) و ایتالیک (*...*) را تفکیک می‌کند
        val regex = Regex("(\\*\\*.*?\\*\\*|\\*.*?\\*)")
        val matches = regex.findAll(text)

        var currentIndex = 0

        for (match in matches) {
            // متن‌های معمولی قبل از رسیدن به ستاره‌ها
            append(text.substring(currentIndex, match.range.first))

            val matchValue = match.value
            when {
                // اگر متن با دو ستاره شروع شده باشد -> Bold
                matchValue.startsWith("**") -> {
                    val innerText = matchValue.removeSurrounding("**")
                    pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                    append(innerText)
                    pop()
                }
                // اگر متن با یک ستاره شروع شده باشد -> Italic
                matchValue.startsWith("*") -> {
                    val innerText = matchValue.removeSurrounding("*")
                    pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                    append(innerText)
                    pop()
                }
            }
            currentIndex = match.range.last + 1
        }

        // اضافه کردن باقی‌مانده متن بعد از آخرین ستاره
        if (currentIndex < text.length) {
            append(text.substring(currentIndex))
        }
    }
}

/*@Preview(showBackground = true)
@Composable
fun PhotosGridScreenPreview() {
    ProductListViewTheme {
        val mockData = List(10) { InternetPhoto("$it", "") }
        InternetList(mockData)
    }
}*/
