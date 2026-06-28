package com.example.productlistview

import android.app.Application
import com.example.productlistview.data.di.AppContainer
import com.example.productlistview.data.di.DefaultAppContainer


class ProductsApplication : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
}

