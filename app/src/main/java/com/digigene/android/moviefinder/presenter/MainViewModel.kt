package com.digigene.android.moviefinder.presenter

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.digigene.android.moviefinder.SchedulersWrapper
import com.digigene.android.moviefinder.model.MainModel
import io.reactivex.observers.DisposableSingleObserver
import retrofit2.HttpException

class MainViewModel(val mainModel: MainModel) {
    private val resultListObservable = MutableLiveData<List<String>>()
    private val resultListErrorObservable = MutableLiveData<HttpException>()
    private val itemObservable = MutableLiveData<MainModel.ResultEntity>()
    fun getResultListObservable(): LiveData<List<String>> = resultListObservable
    fun getResultListErrorObservable(): LiveData<HttpException> = resultListErrorObservable
    fun getItemObservable(): LiveData<MainModel.ResultEntity> = itemObservable
    private lateinit var entityList: List<MainModel.ResultEntity>
    private val schedulersWrapper = SchedulersWrapper()

    fun findAddress(address: String) {
        mainModel.fetchAddress(address)!!.subscribeOn(schedulersWrapper.io()).observeOn(schedulersWrapper.main()).subscribeWith(object : DisposableSingleObserver<List<MainModel.ResultEntity>?>() {
            override fun onSuccess(t: List<MainModel.ResultEntity>) {
                entityList = t
                resultListObservable.postValue(fetchItemTextFrom(t))
            }

            override fun onError(e: Throwable) {
                resultListErrorObservable.postValue(e as HttpException)
            }
        })
    }

    private fun fetchItemTextFrom(it: List<MainModel.ResultEntity>): ArrayList<String> {
        val li = arrayListOf<String>()
        for (resultEntity in it) {
            li.add("${resultEntity.year}: ${resultEntity.title}")
        }
        return li
    }

    fun doOnItemClick(position: Int) {
        itemObservable.value = entityList[position]
    }
}