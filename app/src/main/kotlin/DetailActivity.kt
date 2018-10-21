package com.example.yoshi.viewpagertodo1

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.yoshi.viewpagertodo1.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail)
        binding.setLifecycleOwner(this@DetailActivity)

        val number = intent.extras?.getInt("parentID") ?: 0
        val tagSting = intent.extras?.getString("tagString") ?: ""
        val context = this@DetailActivity
        val repository = Repository()
        val itemList = repository.loadListFromPreference(context)
        val index = when (number) {
                    in 0..itemList.lastIndex -> { number }
            else -> {
                val newItem = ToDoItem(title = "", tagString = tagSting, reward = 1.0f, startLine = getToday())
                itemList.add(newItem)
                Log.i("test", "Item number ${itemList.size} was added: ")
                itemList.lastIndex
            }
        }
        binding.item = itemList[index]
        val dr = context.getDrawable(R.drawable.elevating_square)
        binding.startDateTxt.background = dr

        binding.startDateTxt.setOnClickListener { v ->
            if (itemList[index].hasStartLine) {
                itemList[index].hasStartLine = false
                val backGround = getDrawable(R.drawable.depressing_square)
                v.background = backGround
            } else {
                itemList[index].hasStartLine = true
                val backGround = getDrawable(R.drawable.elevating_square)
                v.background = backGround
            }
        }


        // Set Event handler
        binding.applyBtn.setOnClickListener {
            val intent = Intent(context, MainActivity::class.java)
            repository.saveListToPreference(itemList, context)
            Log.i("test", "item save and detail to Main")
            startActivity(intent, null)
        }

        binding.cancelBtn.setOnClickListener {
                val intent = Intent(context, MainActivity::class.java)
                Log.i("test", "detail to Main")
                startActivity(intent, null)
        }
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val monthOfYear = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val startDataSetListener = DataSetListener(binding.startDateTxt)
        val deadDataSetListener = DataSetListener(binding.deadDateTxt)

        binding.startCal.setOnClickListener {
            val startDatePicker = DatePickerDialog(context, startDataSetListener, year, monthOfYear, dayOfMonth)
            startDatePicker.show()
        }
        binding.deadCal.setOnClickListener {
            val deadDatePicker = DatePickerDialog(context, deadDataSetListener, year, monthOfYear, dayOfMonth)
            deadDatePicker.show()
        }

        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top)
    }

    class DataSetListener(private val _textView: TextView) : DatePickerDialog.OnDateSetListener {
        override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
            _textView.text = "$year/${month + 1}/$dayOfMonth"
        }
    }

}

