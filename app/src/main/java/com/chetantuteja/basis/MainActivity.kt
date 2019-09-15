package com.chetantuteja.basis

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.chetantuteja.basis.adapter.DataRecyclerAdapter
import com.chetantuteja.basis.datamodels.Data
import com.chetantuteja.basis.datamodels.Feed
import com.chetantuteja.basis.util.CustomConverterFactory
import com.littlemango.stacklayoutmanager.StackLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class MainActivity : AppCompatActivity() {

    companion object{
        private const val TAG = "MainActivity"
        private const val BASE_URL = "https://git.io"
    }

    //Variables
    private val mAdapter = DataRecyclerAdapter()
    private var currentPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupRecyclerView()
    }

    //Sets up RecyclerView
    private fun setupRecyclerView(){
        val orientation = StackLayoutManager.ScrollOrientation.RIGHT_TO_LEFT   //Can be set to any one among R-L or L-R or T-B or B-T
        val manager = StackLayoutManager(orientation)
        mainRV.layoutManager = manager

        //Helps save position in case of orientation change(No need to implement in case of Viewmodels)
        manager.setItemChangedListener(object: StackLayoutManager.ItemChangedListener{
            override fun onItemChanged(position: Int) {
                currentPosition = position
            }

        })


        loadJSONData()
    }

    //Fetches JSON Data from the API
    private fun loadJSONData(){
        if(!isNetworkAvailable()){
            popupDialog()
            return
        }
        val thread = Thread{
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okhttp3.OkHttpClient())
                .addConverterFactory(CustomConverterFactory())
                .build()

            val feedAPI = retrofit.create(FeedAPI::class.java)
            val feed = feedAPI.getFeed()
            feed.enqueue(object: Callback<Feed> {
                override fun onFailure(call: Call<Feed>, t: Throwable) {
                    Log.e(TAG, "onFailure: Unable to get data " + t.message.toString())
                    Toast.makeText(this@MainActivity, "An Error Occurred.", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<Feed>, response: Response<Feed>) {
                    //Log.d(TAG, "onResponse:  Feed: " + response.body()!!.data[0].id)
                    Log.d(TAG, "onResponse:  Server Response: $response")
                    setupPostFeed(response)
                }

            })
        }
        thread.start()
    }

    //Submits Data to Recycler View
    private fun setupPostFeed(response: Response<Feed>){
        val resBody = response.body()
        if (resBody != null) {
            mainRV.adapter = mAdapter
            mAdapter.submitList(resBody.data)
            mainRV.smoothScrollToPosition(currentPosition)
        }
    }

    //Reset the stack to first position
    fun resetStackClick(view: View){
        if(isNetworkAvailable() && mAdapter.itemCount != 0){
            mainRV.smoothScrollToPosition(0)
        } else {
            loadJSONData()
        }
    }

    //Checks if Internet Connection is available for fetching data.
    private fun isNetworkAvailable(): Boolean {
        val cManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeInfo = cManager.activeNetwork
        if (activeInfo != null) {
            val cap = cManager.getNetworkCapabilities(activeInfo)
            if (cap != null) {
                cap.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                return true
            }
        }
        return false
    }

    //Pops up Error Dialog
    private fun popupDialog(){
        MaterialDialog(this@MainActivity).show {
            title(R.string.error)
            message(R.string.no_internet)
            positiveButton(R.string.ok_btn){ dialog ->
                dialog.dismiss()
            }
            cornerRadius(16f)
        }
    }

    //Override Methods
    override fun onResume() {
        super.onResume()
        loadJSONData()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("currentPosition",currentPosition)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        if(savedInstanceState.containsKey("currentPosition")){
            currentPosition = savedInstanceState.getInt("currentPosition")
            loadJSONData()
        }
    }
}