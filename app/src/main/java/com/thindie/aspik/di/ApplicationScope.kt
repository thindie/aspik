package com.thindie.aspik.di

import com.thindie.aspik.Application
import com.thindie.aspik.feature.home.HomeFlow
import com.thindie.aspik.feature.home.input.data.InputRepositoryImpl
import com.thindie.aspik.feature.home.input.domain.InputRepository
import com.thindie.aspik.feature.spiks.SpiksFlow
import com.thindie.aspik.feature.spiks.data.ReactiveStorage
import com.thindie.aspik.feature.spiks.data.SpiksRepositoryImpl
import com.thindie.aspik.feature.spiks.domain.SpiksRepository
import okhttp3.OkHttpClient

class ApplicationScope private constructor() {
  private val client = OkHttpClient()
  private var inputRepository: InputRepository? = null
  private var spiksRepository: SpiksRepository? = null
  private lateinit var applicationContext: Application

  private lateinit var storage: ReactiveStorage

  fun inject(homeFlow: HomeFlow) {
    if (inputRepository == null) {
      inputRepository = InputRepositoryImpl(applicationContext, client)
    }
    homeFlow.inputRepository = this.inputRepository!!
  }

  fun inject(spiksFlow: SpiksFlow) {
    if (spiksRepository == null) {
      spiksRepository = SpiksRepositoryImpl(storage)
    }
    spiksFlow.spiksRepository = this.spiksRepository!!

    if (inputRepository == null) {
      inputRepository = InputRepositoryImpl(applicationContext, client)
    }
    spiksFlow.inputRepository = this.inputRepository!!
  }

  companion object {
    fun configure(application: Application): ApplicationScope {
      return ApplicationScope()
        .apply {
          applicationContext = application
          storage = ReactiveStorage(application)
        }
    }
  }
}
