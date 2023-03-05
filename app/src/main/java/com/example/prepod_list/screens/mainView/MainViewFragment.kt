package com.example.prepod_list.screens.mainView

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.SearchView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.prepod_list.R
import com.example.prepod_list.model.PrepodItem
import com.example.prepod_list.model.prepListen
import kotlinx.coroutines.delay


const val APP_PREFS = "APP_PREFERENCES"
const val COUNTER_PREFS = "COUNTER_PREF_VALUE"
const val LIST_PREFS = "PREPOD_LIST_PREF"
class MainViewFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var adapter:MainViewAdapter
    lateinit var sercher:SearchView
    var connection = false
    var cnt =0
    lateinit var preferences: SharedPreferences
    var prepodSaveList = mutableListOf<PrepodItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewModel = ViewModelProvider(this).get(StartViewModel::class.java)
        val v = inflater.inflate(R.layout.fragment_main_view, container, false)
        //recyclerView = v
        recyclerView = v.findViewById(R.id.rv_main)
        adapter = MainViewAdapter()
        sercher = v.findViewById(R.id.serch)
        preferences = this.requireActivity().getSharedPreferences(APP_PREFS,Context.MODE_PRIVATE)

        //preferences.edit().clear().apply()

        cnt = preferences.getInt(COUNTER_PREFS,0)
        adapter.preferences = preferences

        val cm = this.requireContext()!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        if (wifiInfo != null && wifiInfo.isConnected) {
            connection = true
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        if (wifiInfo != null && wifiInfo.isConnected) {
            connection = true
        }
        wifiInfo = cm.activeNetworkInfo
        if (wifiInfo != null && wifiInfo.isConnected) {
            connection = true
        }
        adapter.connection = connection

        sercher.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextChange(p0: String?): Boolean {
                if(p0!=null)adapter.search_str = p0!!
                android.os.Handler().postDelayed({
                    ListGetter(adapter,viewModel)
                },2000)
                return false
            }

            override fun onQueryTextSubmit(p0: String?): Boolean {
                ListGetter(adapter,viewModel)
                return false
            }
        })
        recyclerView.adapter = adapter
        ListGetter(adapter,viewModel)

        return v
    }
    private fun ListGetter(adapter: MainViewAdapter,viewModel:StartViewModel){
        if(connection) {
            viewModel.getPrepod()
            viewModel.PrepodList.observe(viewLifecycleOwner, { list ->
                list.body()?.let { adapter.setList(it) }
            })
        }
        else{
            val dialog = AlertDialog.Builder(this.requireContext())
                .setCancelable(true)
                .setTitle("No Connection")
                .setMessage("You are not connected to the internet. Prepods that already viewed are showed")
                .create()
            dialog.show()
            var index = cnt*6
            while (index>0){

                //Log.d("AAAAAAAAAAAAAAAAAAAAAAAAAAAa",preferences.getString((cnt*6-(--index)).toString(),"").toString())
                val prit = PrepodItem(preferences.getStringSet((cnt*6-(--index)).toString(), mutableSetOf<String>())!!.toList()
                    ,"","","",
                    preferences.getString((cnt*6-(--index)).toString(),"").toString(),
                    0,preferences.getString((cnt*6-(--index)).toString(),"").toString(),
                    preferences.getString((cnt*6-(--index)).toString(),"").toString(),
                    preferences.getString((cnt*6-(--index)).toString(),"").toString(),
                    preferences.getString((cnt*6-(--index)).toString(),"").toString(),"")

                prepodSaveList.add(prit)
            }
            adapter.setList(prepodSaveList.toList())
        }
    }
    private val prepodActionListener: prepListen = {
        adapter.dataSet = it
    }
}
