package com.chetantuteja.basis

import com.chetantuteja.basis.datamodels.Feed
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.GET

interface FeedAPI {

    @GET("/fjaqJ")
    fun getFeed(): Observable<Feed>
}