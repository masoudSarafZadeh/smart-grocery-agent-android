package com.example.productlistview.ui.screens.products


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.productlistview.ProductsApplication
import com.example.productlistview.data.repository.ProductsPhotosRepository
import com.example.productlistview.data.model.chat.ChatMessage
import com.example.productlistview.data.model.response.InternetProducts
import com.example.productlistview.data.model.chat.InternetProductsItemState
import com.example.productlistview.ui.screens.products.ProductsUiState
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

sealed interface AiStreamEvent {
    data class StatusUpdate(val status: String) : AiStreamEvent // برای "در حال جستجو..."
    data class TokenReceived(val token: String) : AiStreamEvent // برای استریم کلمات
    data class DataReceived(val rawDbData: Map<String, List<InternetProducts>>) : AiStreamEvent // داده‌های دیتابیس
    data class Error(val message: String) : AiStreamEvent
    object Done : AiStreamEvent
}
class ProductsViewModel(private val productsPhotosRepository: ProductsPhotosRepository , private val supabaseClient: SupabaseClient) : ViewModel() {
    //private val _uiState = MutableStateFlow<List<ProductsUiState>>(Datasource().loadProducts())
    //val uiState: StateFlow<List<ProductsUiState>> = _uiState.asStateFlow()

    private val _internetUiState = MutableStateFlow(ProductsUiState())
    val internetUiState: StateFlow<ProductsUiState> = _internetUiState.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()
    private val chatSessionThreadId: String = UUID.randomUUID().toString()

    /*var instruments by mutableStateOf<List<InternetPhoto>>(listOf())
        private set*/

    /*init {
        getProductsPhotos()
    }*/




    /*fun updateCount(index: Int, newCount: Int) {
        _uiState.update { currentList ->
            currentList.map { product ->
                // If this is the product we clicked, copy it with the new count
                if (product.id == index) {
                    product.copy(count = newCount)
                } else {
                    product
                }
            }
        }
    }

    fun toggleExpand(productId: Int) {
        _uiState.update { currentList ->
            currentList.map { product ->
                if (product.id == productId) {
                    // Toggle the boolean and reset count if collapsing
                    val newExpandState = !product.isExpanded
                    product.copy(
                        isExpanded = newExpandState,
                        count = if (newExpandState) 1 else 0
                    )
                } else {
                    product
                }
            }
        }
    }*/

    fun updateInternetCount(photoId: Int, newCount: Int) {
        _chatMessages.update { messages ->
            messages.map { msg ->
                if (msg.products.isNotEmpty()) {
                    // ۱. ایجاد مپ جدید با تغییر کانت کالا
                    val updatedMap = msg.products.mapValues { entry ->
                        entry.value.map { item ->
                            if (item.product.id == photoId) {
                                item.copy(count = newCount)
                            } else {
                                item
                            }
                        }
                    }
                    // ۲. 🟢 ریترن کردن یک کپی کاملاً جدید از کل استیت به همراه مپ آپدیت شده
                    msg.copy(products = updatedMap)
                } else {
                    msg
                }
            }
        }
    }
    fun toggleInternetExpand(photoId: Int) {
        _chatMessages.update { messages ->
            messages.map { msg->
                if (msg.products.isNotEmpty()) {
                    val updatedMap = msg.products.mapValues { entry ->
                        entry.value.map { item ->
                            if (item.product.id == photoId) {
                                val newExpandState = !item.isExpanded
                                item.copy(
                                    isExpanded = newExpandState,
                                    count = if (newExpandState) 1 else 0
                                )
                            } else item
                        }
                    }
                    msg.copy(products = updatedMap)
                }else{
                    msg
                }
            }

        }
    }

    /*fun sendMessageToAiStreaming(userQuery: String) {
        if (userQuery.isBlank()) return

        viewModelScope.launch {
            // 1. Add the user's message to the board
            _chatMessages.update { currentList ->
                currentList + ChatMessage(text = userQuery, isUser = true)
            }

            // 2. Insert a temporary blank placeholder message for the AI response
            val aiMessageId = java.util.UUID.randomUUID().toString()
            _chatMessages.update { currentList ->
                currentList + ChatMessage(id = aiMessageId, text = "Thinking...", isUser = false)
            }

            try {
                var streamingAccumulator = ""

                // 3. Collect tokens live from the cloud container
                productsPhotosRepository.askAiAgentStream(userQuery).collect { chunk ->
                    // Clean or parse your chunk formatting here if your LangGraph returns JSON packets
                    streamingAccumulator += chunk

                    // 4. Continually swap out the placeholder text with the newest accumulated string
                    _chatMessages.update { currentList ->
                        currentList.map { message ->
                            if (message.id == aiMessageId) {
                                message.copy(text = streamingAccumulator)
                            } else {
                                message
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("StreamingError", "Failed to collect live stream", e)
                _chatMessages.update { currentList ->
                    currentList.map { message ->
                        if (message.id == aiMessageId) {
                            message.copy(text = "Error connecting to service.")
                        } else {
                            message
                        }
                    }
                }
            }
        }
    }*/
    // 💡 ۲. تابع جدید برای ارسال پیام کاربر به سرور هوش مصنوعی و دریافت پاسخ دوگانه
    fun sendMessageToAi(userQuery: String) {
        if (userQuery.isBlank()) return

        viewModelScope.launch {
            val aiMessageId = UUID.randomUUID().toString()
            _chatMessages.update { current ->
                current + listOf(
                    ChatMessage(text = userQuery, isUser = true),
                    ChatMessage(id = aiMessageId, text = "", isUser = false, isStreaming = true, statusMessage = "در حال ارتباط...")
                )
            }

            _internetUiState.update { currentState ->
                currentState.copy(
                    isInitialLoading = true, // اگر بار اول است لودینگ اصلی
                    isLoadingNewMessage = false, // اگر کالا داریم، فقط پرچم فرعی
                    isError = false
                )
            }
            //delay(1500)
            productsPhotosRepository.askAiAgentStream(userQuery, chatSessionThreadId)
                .collect { event ->
                    when (event) {
                        is AiStreamEvent.StatusUpdate -> {
                            updateAiMessage(aiMessageId) { it.copy(statusMessage = event.status) }
                        }
                        is AiStreamEvent.TokenReceived -> {
                            _internetUiState.update { currentState ->
                                currentState.copy(
                                    isInitialLoading = false,
                                    isLoadingNewMessage = false
                                )
                            }
                            // اتصال کلمات جدید به انتهای پیام
                            updateAiMessage(aiMessageId) {
                                it.copy(text = it.text + event.token, statusMessage = null)
                            }
                        }
                        is AiStreamEvent.DataReceived -> {
                            // تبدیل دیتای خام به State کلاس‌ها و آپدیت محصولات
                            val uiDbProductStates = event.rawDbData.mapValues { entry ->
                                entry.value.map { InternetProductsItemState(product = it) }
                            }
                            updateAiMessage(aiMessageId) {
                                it.copy(products = uiDbProductStates)
                            }
                            _internetUiState.update { currentState ->
                                currentState.copy(
                                    isInitialLoading = false,
                                    isError = false
                                )
                            }
                        }
                        is AiStreamEvent.Error -> {
                            updateAiMessage(aiMessageId) { it.copy(text = "خطا: ${event.message}", isStreaming = false, statusMessage = null) }
                            _internetUiState.update { it.copy(isError = true, isInitialLoading = false, isLoadingNewMessage = false) }
                        }
                        is AiStreamEvent.Done -> {
                            updateAiMessage(aiMessageId) { it.copy(isStreaming = false, statusMessage = null) }
                            _internetUiState.update { it.copy(isInitialLoading = false, isLoadingNewMessage = false) }
                        }
                    }
                }

            /*try {
                val response: LangServeResponse = productsPhotosRepository.askAiAgent(userQuery,
                    chatThreadId = chatSessionThreadId)
                //val response = getMockAiResponse()
                // نمونه داده شبیه‌سازی شده بر اساس خروجی واقعی پایتون شما:
                val outputMessages = response.output.messages
                val llmResponseText = outputMessages.lastOrNull { it.type == "ai" }?.textContent
                    ?: "خطا در دریافت پاسخ متنی."

                // 3. Extract the database map safely
                val productsFromDb = response.output.rawDbData ?: emptyMap()
                productsFromDb.forEach { (category, productList) ->
                    productList.forEach { product ->
                        Log.d("AiImageDebug", "📦 Product: ${product.productName} -> URL: ${product.image}")
                    }
                }

                val uiDbProductStates: Map<String, List<InternetProductsItemState>> = productsFromDb.mapValues { entry ->
                    entry.value.map { product ->
                        InternetProductsItemState(product = product)
                    }
                }
                // ج) اضافه کردن پاسخ متنی هوش مصنوعی به چت روم
                _chatMessages.update { currentMessages ->
                    currentMessages + ChatMessage(text = llmResponseText, isUser = false)
                }

                // هـ) به روز رسانی لیست محصولات در پایین صفحه چت
                _internetUiState.update { currentState ->
                    currentState.copy(
                        items = uiDbProductStates,
                        isInitialLoading = false,
                        isLoadingNewMessage = false,
                        isError = false
                    )
                }

            } catch (e: Exception) {
                Log.e("AiChatError", "تعامل با پایتون با خطا مواجه شد", e)
                _internetUiState.update { it.copy(isError = true, isInitialLoading = false, isLoadingNewMessage = false) }

                // پیام خطای سیستم به کاربر در چت باکس
                _chatMessages.update { currentMessages ->
                    currentMessages + ChatMessage(text = "خطا در برقراری ارتباط با دستیار فروشگاه.", isUser = false)
                }
            }*/
        }
    }
    private fun updateAiMessage(messageId: String, update: (ChatMessage) -> ChatMessage) {
        _chatMessages.update { messages ->
            messages.map { msg ->
                if (msg.id == messageId) update(msg) else msg
            }
        }
    }

    /*fun getProductsPhotos() {
        viewModelScope.launch {
            internetUiState = InternetUiState.Loading
            internetUiState = try {
                val result = productsPhotosRepository.getProductsPhotos()
                InternetUiState.Success(result)
            } catch (e: IOException) {
                InternetUiState.Error
            }catch (e: HttpException) {
                InternetUiState.Error
            }
        }
    }*/
    /*fun getSupabasePhotos(searchQuery: String? = null) {
        viewModelScope.launch {
            _internetUiState.value = InternetUiState.Loading
            try {
                val result = supabaseClient.from("goods")
                    .select {
                        // Apply filtering conditionally if searchQuery is not null/empty
                        if (!searchQuery.isNullOrBlank()) {
                            filter {
                                // Looks for matches anywhere in the product name
                                ilike("product_name", "%$searchQuery%")
                            }
                        }
                        // Keep things fast by limiting results
                        limit(20)
                    }.decodeList<InternetProducts>()
                val uiProductStates = result.map { product ->
                    InternetProductsItemState(product = product)
                }
                _internetUiState.value = InternetUiState.Success(uiProductStates)
            } catch (e: Exception) {
                // This catches Ktor network failures, Supabase API errors, and serialization crashes
                Log.e("SupabaseError", "Failed to fetch photos", e)
                _internetUiState.value = InternetUiState.Error
            }
        }
    }

    // Add this function to handle dynamic UI searches
    fun searchProducts(query: String) {
        getSupabasePhotos(query)
    }*/

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as ProductsApplication)
                val productsPhotosRepository = application.container.productsPhotosRepository
                val supabaseClient = application.container.supabaseClient
                ProductsViewModel(productsPhotosRepository = productsPhotosRepository , supabaseClient = supabaseClient)
            }
        }
    }

}


/*fun getMockAiResponse(): AiResponse {
    val mockOilList = listOf(
        InternetProducts(
            id = 60,
            productName = "روغن مایع سرخ کردنی بدون پالم",
            brand = "بهار الماس",
            weight = 810,
            price = 79000,
            priceAfterOff = 79000,
            offPercent = 0,
            llmGuide = "recommended",
            image = "https://opukeehrojxzniygycpw.supabase.co/storage/v1/object/sign/pictures/baharalmas_810.png"
        ),
        InternetProducts(
            id = 53,
            productName = "روغن مایع سرخ کردنی کنجدو کانولا و ذرت تصفیه شده",
            brand = "داتیس",
            weight = 1800,
            price = 956900,
            priceAfterOff = 956900,
            offPercent = 0,
            llmGuide = null,
            image = "https://opukeehrojxzniygycpw.supabase.co/storage/v1/object/sign/pictures/datis_konjed_1500.png"
        ),
        InternetProducts(
            id = 54,
            productName = "روغن مایع کنجد تصفیه شده",
            brand = "داتیس",
            weight = 900,
            price = 858200,
            priceAfterOff = 858200,
            offPercent = 0,
            llmGuide = "best quality",
            image = "https://opukeehrojxzniygycpw.supabase.co/storage/v1/object/sign/pictures/datis_konjed_900.png"
        ),
        InternetProducts(
            id = 56,
            productName = "روغن مایع سرخ کردنی کنجد و کانولا و آفتابگردان تصفیه شده",
            brand = "داتیس",
            weight = 1800,
            price = 820900,
            priceAfterOff = 820900,
            offPercent = 0,
            llmGuide = null,
            image = "https://opukeehrojxzniygycpw.supabase.co/storage/v1/object/sign/pictures/datis_konjed_1500.png"
        ),
        InternetProducts(
            id = 55,
            productName = "روغن مایع سرخ کردنی زیتون",
            brand = "امگانو",
            weight = 1800,
            price = 669900,
            priceAfterOff = 596211,
            offPercent = 11,
            llmGuide = null,
            image = "https://opukeehrojxzniygycpw.supabase.co/storage/v1/object/sign/pictures/omegano_zeyton_1800.png"
        )
    )

    val mockTomatoPasteList = listOf(
        InternetProducts(
            id = 6,
            productName = "رب گوجه قوطی",
            brand = "خرم",
            weight = 800,
            price = 99800,
            priceAfterOff = 71856,
            offPercent = 28,
            llmGuide = "recommended",
            image = "https://opukeehrojxzniygycpw.supabase.co/storage/v1/object/sign/pictures/khoram.png"
        ),
        InternetProducts(
            id = 16,
            productName = "رب گوجه شیشه ای",
            brand = "سحر",
            weight = 1550,
            price = 248000,
            priceAfterOff = 230640,
            offPercent = 7,
            llmGuide = null,
            image = "https://opukeehrojxzniygycpw.supabase.co/storage/v1/object/sign/pictures/sahar_1550_sh.png"
        ),
        InternetProducts(
            id = 19,
            productName = "رب گوجه شیشه ای",
            brand = "خوشبخت",
            weight = 1500,
            price = 181000,
            priceAfterOff = 175570,
            offPercent = 3,
            llmGuide = null,
            image = "https://opukeehrojxzniygycpw.supabase.co/storage/v1/object/sign/pictures/khoshbakht_1500_sh.png"
        ),
        InternetProducts(
            id = 10,
            productName = "رب گوجه شیشه ای",
            brand = "سیبون",
            weight = 1500,
            price = 188000,
            priceAfterOff = 165440,
            offPercent = 12,
            llmGuide = null,
            image = "https://opukeehrojxzniygycpw.supabase.co/storage/v1/object/sign/pictures/sibon_1500_sh.png"
        ),
        InternetProducts(
            id = 22,
            productName = "رب گوجه شیشه ای",
            brand = "مکنزی",
            weight = 1500,
            price = 181000,
            priceAfterOff = 162900,
            offPercent = 10,
            llmGuide = null,
            image = "https://opukeehrojxzniygycpw.supabase.co/storage/v1/object/sign/pictures/makenzi_1500_sh.png"
        )
    )

    val mockTextResponse = "سلام! برای روغن و رب گوجه دو گزینهٔ بسیار مناسب داریم:\n\n" +
            "- **روغن مایع سرخ کردنی بدون پالم – برند بهار الماس** (وزن 810 گرم، قیمت 79 000 تومان) با ویژگی *recommended*؛ یعنی ترکیبی عالی از کیفیت خوب و قیمت مناسب، مناسب برای سرخ کردن روزانه.\n\n" +
            "- **رب گوجه قوطی – برند خرم** (وزن 800 گرم، قیمت 71 856 تومان) هم به‌صورت *recommended* برگزیده شده است؛ طعمی غنی و قیمت‌جذب‌کننده که برای آشپزی‌های خانگی عالی است.\n\n" +
            "هر دو محصول هم‌اکنون در دسترس هستند و می‌توانید به‌راحتی به سبد خرید اضافه کنید. 🍳🍅"

    return AiResponse(
        llm_response = mockTextResponse,
        products = mapOf(
            "روغن" to mockOilList,
            "رب گوجه" to mockTomatoPasteList
        )
    )
}*/