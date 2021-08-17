package com.fmt.compose.news.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fmt.compose.news.ext.State
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

open class BaseViewModel : ViewModel() {

    val stateLiveData = MutableLiveData<State>().apply {
        value = State.Loading
    }

    fun launch(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch {
            kotlin.runCatching {
                block()
            }.onSuccess {
                stateLiveData.value = State.Success
            }.onFailure {
                stateLiveData.value = State.Error(it.message)
            }
        }
    }
}