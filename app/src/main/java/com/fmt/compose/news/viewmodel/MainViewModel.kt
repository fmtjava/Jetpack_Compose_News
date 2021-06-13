package com.fmt.compose.news.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

/**
 *防止Activity因内存不足被回收后，底部Tab选中和显示的Tab页面不一致问题
 */
class MainViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    private val HOME_PAGE_SELECTED_INDEX = "home_page_selected_index"

    private val mSelectLiveData = MutableLiveData<Int>()

    fun getSelectedIndex(): LiveData<Int> {
        if (mSelectLiveData.value == null) {
            val index = savedStateHandle.get<Int>(HOME_PAGE_SELECTED_INDEX) ?: 0
            mSelectLiveData.postValue(index)
        }
        return mSelectLiveData
    }

    fun saveSelectIndex(selectIndex: Int) {
        savedStateHandle.set(HOME_PAGE_SELECTED_INDEX, selectIndex)
        mSelectLiveData.value = selectIndex
    }

}