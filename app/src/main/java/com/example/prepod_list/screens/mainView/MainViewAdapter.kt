package com.example.prepod_list.screens.mainView

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.prepod_list.R
import com.example.prepod_list.databinding.FragmentMainViewBinding
import com.example.prepod_list.databinding.PrepodItemLoyoutBinding
import com.example.prepod_list.model.PrepodItem
import java.io.File
import java.io.FileOutputStream

class MainViewAdapter: RecyclerView.Adapter<MainViewAdapter.ViewHolder>(),View.OnClickListener {
    var dataSet : List<PrepodItem> = emptyList()
    lateinit var context : Context
    lateinit var binding : PrepodItemLoyoutBinding
    lateinit var SearchBinding : FragmentMainViewBinding
    lateinit var img : ImageView
    lateinit var image : ImageView
    var cnt = 0
    lateinit var preferences: SharedPreferences
    var connection = false
    var search_str = ""
    lateinit var dialog : Dialog
    class ViewHolder(val binding:PrepodItemLoyoutBinding) : RecyclerView.ViewHolder(binding.root) {}
    override fun onClick(p: View) {
        val prepod = p.tag as PrepodItem

        dialog = Dialog(context)
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.fragment_dialog_fragment)
        var ok = dialog.findViewById<Button>(R.id.ok)
        image = dialog.findViewById<ImageView>(R.id.img)

        Glide.with(context).load(prepod.photoLink).circleCrop()
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_foreground)
            .into(image)

        var Surname = dialog.findViewById<TextView>(R.id.surname)
        Surname.text = prepod.lastName
        var Name = dialog.findViewById<TextView>(R.id.name)
        Name.text = prepod.firstName
        var FTVO = dialog.findViewById<TextView>(R.id.fathername)
        FTVO.text = prepod.middleName
        var RNK = dialog.findViewById<TextView>(R.id.rank)
        RNK.text = prepod.rank
        var DPT = dialog.findViewById<TextView>(R.id.dept)
        var str = ""
        for(n in 0..prepod.academicDepartment.size-1){
            str+=prepod.academicDepartment[n]
            if(n!=prepod.academicDepartment.size-1)
                str+=", "
        }
        DPT.text = str
        ok.setOnClickListener{
            dialog.hide()
        }

        dialog.show()

        //Navigation.findNavController(p).navigate(R.id.action_mainViewFragment_to_dialog_fragment)
        android.os.Handler().postDelayed({
        if(connection) {
            val bitmap = image.drawable.toBitmap()

            cnt = preferences.getInt(COUNTER_PREFS, 0) * 6
            preferences.edit().putStringSet((++cnt).toString(), prepod.academicDepartment.toSet())
                .apply()
            preferences.edit().putString((++cnt).toString(), prepod.firstName).apply()
            preferences.edit().putString((++cnt).toString(), prepod.lastName).apply()
            preferences.edit().putString((++cnt).toString(), prepod.middleName).apply()

            val imagesFolder = File(context.dataDir, "images")
            imagesFolder.mkdirs()
            if(File(imagesFolder,(cnt+1).toString()).exists())
                File(imagesFolder,(cnt+1).toString()).delete()
            val file = File(imagesFolder, (++cnt).toString()+".png")
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.flush()
            stream.close()

            preferences.edit()
                .putString(cnt.toString(), file.absolutePath.toString()).apply()

            if(prepod.rank!=null)preferences.edit().putString((++cnt).toString(), prepod.rank).apply()
            else preferences.edit().putString((++cnt).toString()," ").apply()


            preferences.edit().putInt(COUNTER_PREFS, cnt / 6).apply()
            Log.d("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAa",file.absolutePath)
        }
        }, 100)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val inflater = LayoutInflater.from(parent.context)
        binding = PrepodItemLoyoutBinding.inflate(inflater,parent,false)

        binding.root.setOnClickListener(this)

        return ViewHolder(binding)
    }
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        var a :List<String>

        viewHolder.binding.name.text = dataSet[position].firstName
        viewHolder.binding.surname.text = dataSet[position].lastName
        viewHolder.binding.Fatherstvo.text = dataSet[position].middleName
        viewHolder.binding.rank.text = dataSet[position].rank
        var str = ""
        for(n in 0..dataSet[position].academicDepartment.size-1){
            str+=dataSet[position].academicDepartment[n]
            if(n!=dataSet[position].academicDepartment.size-1)
                str+=", "
        }
        viewHolder.binding.dept.text = str

        img = viewHolder.binding.imaga
        viewHolder.itemView.tag = dataSet[position]
        context = binding.root.context
        //viewHolder.Dept.text=str;

        if(connection)
            Glide.with(viewHolder.binding.imaga).load(dataSet[position].photoLink).circleCrop()
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_foreground)
            .into(viewHolder.binding.imaga)
        else{
            Glide.with(viewHolder.binding.imaga).load(dataSet[position].photoLink).circleCrop()
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(viewHolder.binding.imaga)
            Log.d("AAAAAAAAAAAAAAAAAAAAAAAAA",dataSet[position].photoLink)
        }

    }
    fun setList(list:List<PrepodItem>){
        var Buf_List = mutableListOf<PrepodItem>()
        for (n in 0..list.size-1){
            var a = list[n].firstName.lowercase()
            var b = list[n].lastName.lowercase()
            if(search_str!= ""&&!a.startsWith(search_str.lowercase())
                &&!b.startsWith(search_str.lowercase()))
                continue
            else
                Buf_List.add(list[n])
        }
        dataSet=Buf_List.toList()
        notifyDataSetChanged()
    }
    override fun getItemCount() = dataSet.size
}