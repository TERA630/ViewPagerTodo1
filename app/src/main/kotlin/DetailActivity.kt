package com.example.yoshi.viewpagertodo1

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.yoshi.viewpagertodo1.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail)
        this.title = " "
        binding.setLifecycleOwner(this@DetailActivity)
        val number = intent.extras?.getInt("parentID") ?: 0
        val tagSting = intent.extras?.getString(KEY_TAG_STR) ?: ""
        val tagListCSV = intent.extras?.getString(KEY_TAG_LIST) ?: ""
        val tagList = tagListCSV.split(",")

        val context = this@DetailActivity
        val itemList = loadListFromTextFile(context)
        val itemToEdit = if (number == INDEX_WHEN_TO_MAKE_NEW_ITEM) {
            val newItem = ToDoItem(title = "", tagString = tagSting, startLine = getToday())
            itemList.add(newItem)
            itemList[itemList.lastIndex]
        } else {
            itemList[number]
        }

        binding.tagTxt.setAdapter(ArrayAdapter<String>(this, R.layout.autocompletet_tag, tagList))
        binding.item = itemToEdit
        binding.rewardRate.rating = itemToEdit.reward.toFloat()

        // Set Event handler
        binding.applyBtn.setOnClickListener {
            saveListToTextFile(context, itemList)
            startMainActivity(itemToEdit.tagString)
        }
        binding.cancelBtn.setOnClickListener {
            startMainActivity(tagSting)
        }
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val monthOfYear = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val startDataSetListener = DataSetListener(binding.startDateTxt)
        val deadDataSetListener = DataSetListener(binding.deadDateTxt)

        binding.startDateTxt.setOnClickListener { v ->
            if (itemToEdit.hasStartLine) { // StartLine　On→Offにする場合
                itemToEdit.hasStartLine = false
                val backGround = getDrawable(R.drawable.frame_depress)
                v.background = backGround
            } else {
                itemToEdit.hasStartLine = true // StartLine Off→Onにする場合
                val backGround = getDrawable(R.drawable.frame_elevate)
                v.background = backGround
                val startDatePicker = DatePickerDialog(context, startDataSetListener, year, monthOfYear, dayOfMonth)
                startDatePicker.show()
            }
        }
        binding.deadDateTxt.setOnClickListener { v ->
            if (itemToEdit.hasDeadLine) {
                itemToEdit.hasDeadLine = false
                val backGround = getDrawable(R.drawable.frame_depress)
                v.background = backGround
            } else {
                itemToEdit.hasDeadLine = true
                val backGround = getDrawable(R.drawable.frame_elevate)
                v.background = backGround
                val deadDatePicker = DatePickerDialog(context, deadDataSetListener, year, monthOfYear, dayOfMonth)
                deadDatePicker.show()
            }
        }
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top)
    }

    class DataSetListener(private val _textView: TextView) : DatePickerDialog.OnDateSetListener {
        override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
            _textView.text = "$year/${month + 1}/$dayOfMonth"
        }
    }

    private fun startMainActivity(_tagString: String) {
        val intent = Intent(this@DetailActivity.applicationContext, MainActivity::class.java)
        intent.putExtra(KEY_TAG_STR, _tagString) // TODO new Item , exiting Item
        startActivity(intent, null)
    }

}
