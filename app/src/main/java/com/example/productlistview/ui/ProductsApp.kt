package com.example.productlistview.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.productlistview.R
import com.example.productlistview.ui.screens.introduce.IntroduceScreen
import com.example.productlistview.ui.screens.invoice.InvoiceScreen
import com.example.productlistview.ui.screens.products.ProductsScreen
import com.example.productlistview.ui.screens.products.ProductsViewModel

enum class AllScreens() {
    IntroduceScreen,
    ProductsScreen,
    InvoiceScreen
}

@Composable
fun ProductApp(
    navController: NavHostController = rememberNavController()
    ){
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = AllScreens.valueOf(
        backStackEntry?.destination?.route ?: AllScreens.IntroduceScreen.name)
    val productsViewModel: ProductsViewModel = viewModel(factory = ProductsViewModel.Factory)

    Scaffold(
        topBar = {
            OKTopAppBar(currentScreen)
        },
        contentWindowInsets = WindowInsets.systemBars
    ){innerPadding ->
        Surface(modifier = Modifier.fillMaxSize()) {
            NavHost(navController = navController,
                startDestination = AllScreens.IntroduceScreen.name,
                modifier = Modifier){
                composable(route = AllScreens.IntroduceScreen.name) {
                    IntroduceScreen(
                        onStartButtonClicked = {
                            navController.navigate(AllScreens.ProductsScreen.name)
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                    )
                }
                composable(route = AllScreens.ProductsScreen.name) {
                    ProductsScreen(
                        contentPadding = innerPadding,
                        productsViewModel = productsViewModel,
                        onCheckoutClick = {
                            navController.navigate(AllScreens.InvoiceScreen.name)
                        }
                    )
                }
                composable(route = AllScreens.InvoiceScreen.name) {
                    InvoiceScreen(
                        contentPadding = innerPadding,
                        productsViewModel = productsViewModel,
                        onBackClick = {
                            navController.popBackStack()
                        }
                    )
                }

            }
        }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OKTopAppBar(currentScreen: AllScreens, modifier: Modifier = Modifier) {
    if (currentScreen == AllScreens.ProductsScreen || currentScreen == AllScreens.InvoiceScreen) {
        CenterAlignedTopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        modifier = Modifier
                            .size(100.dp)
                            .padding(6.dp),
                        painter = painterResource(R.drawable.logo),
                        contentDescription = null
                    )
                    Text(
                        text = if (currentScreen == AllScreens.InvoiceScreen) "صورتحساب" else "OK Market",
                        style = MaterialTheme.typography.displaySmall
                    )
                }
            },
            modifier = modifier
        )
    }
}
