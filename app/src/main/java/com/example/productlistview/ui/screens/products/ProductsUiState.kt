package com.example.productlistview.ui.screens.products

/*data class ProductsUiState(
    val id: Int=0,
    @StringRes val rateResourceId: Int=0,
    @StringRes val offPercentResourceId: Int=0,
    @StringRes val productNameResourceId: Int=0,
    @StringRes val priceResourceId: Int=0,
    @StringRes val offPriceResourceId: Int=0,
    @DrawableRes val imageResourceId: Int=0,
    val count: Int = 0,
    val isExpanded: Boolean = false
)*/
data class ProductsUiState(
    val isInitialLoading: Boolean = true, // برای لود اولیه کل صفحه
    val isLoadingNewMessage: Boolean = false, // لودینگ کوچک برای پیام‌های بعدی
    val isError: Boolean = false
)