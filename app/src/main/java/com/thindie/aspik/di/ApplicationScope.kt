package com.thindie.aspik.di

import com.thindie.aspik.Application
import com.thindie.aspik.feature.home.HomeFlow
import com.thindie.aspik.feature.home.input.data.InputRepositoryImpl
import com.thindie.aspik.feature.home.input.domain.InputRepository

class ApplicationScope private constructor() {
  private var inputRepository: InputRepository? = null
  private lateinit var applicationContext: Application

  fun inject(homeFlow: HomeFlow) {
    if (inputRepository == null) {
      inputRepository = InputRepositoryImpl(applicationContext)
    }
    homeFlow.inputRepository = this.inputRepository!!
  }

  companion object {
    fun configure(application: Application): ApplicationScope {
      return ApplicationScope()
        .apply {
          applicationContext = application
        }
    }
  }
}
