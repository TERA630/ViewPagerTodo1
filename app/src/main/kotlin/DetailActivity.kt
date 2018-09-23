package com.example.yoshi.viewpagertodo1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.yoshi.viewpagertodo1.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail)

        val number = intent.extras?.getInt("parentID") ?: 0
        val tagSting = intent.extras?.getString("tagString") ?: ""
        val context = MyApplication.instance?.applicationContext
                ?: throw Exception("context is null at Detail activity")
        val repository = Repository()
        val itemList = repository.loadListFromPreference(context)
        val index = when (number) {
            null -> 0
            in 0..itemList.lastIndex -> {
                Log.i("test", "$number is coming in  to detail activity")
                number
            }
            else -> {
                val newItem = ToDoItem(title = "新しいアイテム", tagString = tagSting, reward = 1.0f, startLine = getToday())
                itemList.add(newItem)
                Log.i("test", "Item number ${itemList.size} was added: ")
                itemList.size
            }
        }
        binding.item = itemList[number]

        binding.applyBtn.setOnClickListener {
            val intent = Intent(context, MainActivity::class.java)
            repository.saveListToPreference(itemList, context)
            Log.i("test", "item save and detail to Main")
            startActivity(intent, null)
        }

        binding.cancelBtn.setOnClickListener { it ->
            val context = MyApplication.instance?.applicationContext
            context?.let {
                repository.saveListToPreference(itemList, context)
                val intent = Intent(it, MainActivity::class.java)
                Log.i("test", "detail to Main")
                startActivity(intent, null)
            }
        }
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top)

    }
}
