package com.example.productlistview.ui.screens.products

data class ProductsUiState(
    val isInitialLoading: Boolean = true,
    val isLoadingNewMessage: Boolean = false,
    val isError: Boolean = false
)
