package com.example.productlistview.ui.screens.invoice

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.productlistview.ui.screens.products.ProductsViewModel

@Composable
fun InvoiceScreen(
    contentPadding: PaddingValues,
    productsViewModel: ProductsViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val messages by productsViewModel.chatMessages.collectAsState()

    val selectedProducts = remember(messages) {
        messages.flatMap { it.products.values.flatten().filter { it.count > 0 } }
    }

    val totalPrice = remember(selectedProducts) {
        selectedProducts.sumOf { it.count * (it.product.priceAfterOff ?: 0) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp)
    ) {
        Text(text = "لیست خرید شما", style = MaterialTheme.typography.headlineMedium)

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(selectedProducts) { item ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Text(text = item.product.productName.toString(), modifier = Modifier.weight(1f))
                    Text(text = "${item.count} عدد")
                }
            }
        }

        Text(text = "جمع کل: $totalPrice تومان")

        Button(onClick = onBackClick) {
            Text("بازگشت به چت")
        }
    }
}
