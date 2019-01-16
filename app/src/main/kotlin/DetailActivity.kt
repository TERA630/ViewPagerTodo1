package com.example.yoshi.viewpagertodo1

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.yoshi.viewpagertodo1.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var itemList: MutableList<ToDoItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail)
        this.title = " "
        binding.setLifecycleOwner(this@DetailActivity)
        val number = intent.extras?.getInt("parentID") ?: 0
        val tagSting = intent.extras?.getString(KEY_TAG_STR) ?: ""
        itemList = loadListFromTextFile(this@DetailActivity)
        val itemToEdit = if (number == INDEX_WHEN_TO_MAKE_NEW_ITEM) {
            // アイテムの新規作成
            val newItem = ToDoItem(title = "", tagString = tagSting, startLine = getToday())
            itemList.add(newItem)
            itemList[itemList.lastIndex]
        } else {    //　アイテムの更新
            itemList[number]
        }
        val tagListCSV = intent.extras?.getString(KEY_TAG_LIST) ?: ""
        // make view from data
        binding.item = itemToEdit
        bindTagList(binding,tagListCSV)
        setupViewRelatedToOtherItems(binding, itemToEdit)
        setupViewRelatedToCalender(binding, itemToEdit)

        // Set Event handler
        binding.applyBtn.setOnClickListener {
            // 編集したアイテムの保存
            updateItemsRelated(itemToEdit)
            saveListToTextFile(this@DetailActivity.applicationContext, itemList)
            startMainActivity(itemToEdit.tagString)
        }
        binding.cancelBtn.setOnClickListener {
            startMainActivity(tagSting)
        }

        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top)
    }

    class DataSetListener(private val _textView: TextView) : DatePickerDialog.OnDateSetListener {
        override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
            _textView.text = _textView.context.getString(R.string.date_format, year, month + 1, dayOfMonth)
        }
    }

    private fun setupViewRelatedToOtherItems(binding: ActivityDetailBinding, itemToEdit: ToDoItem) {

        binding.succeedingTxt.setText(if (itemToEdit.succeeding == EMPTY_ITEM) {
            ""
        } else itemToEdit.succeeding
        )
        binding.precedingTxt.setText(if (itemToEdit.succeeding == EMPTY_ITEM) {
            ""
        } else itemToEdit.preceding
        )
        binding.rewardRate.rating = itemToEdit.reward.toFloat()
        val itemTitleList = List(itemList.size) { index -> itemList[index].title }

        val itemTitleListAdapter = ArrayAdapter<String>(this, R.layout.autocompletet_tag, itemTitleList)
        binding.precedingTxt.setAdapter(itemTitleListAdapter)
        binding.succeedingTxt.setAdapter(itemTitleListAdapter)
    }

    private fun bindTagList(binding: ActivityDetailBinding, tagListCSV: String) {
        val tagList = tagListCSV.split(",")
        binding.tagTxt.setAdapter(ArrayAdapter<String>(this, R.layout.autocompletet_tag, tagList))
    }
    private fun setupViewRelatedToCalender(binding: ActivityDetailBinding, itemToEdit: ToDoItem) {
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
                val startDatePicker = DatePickerDialog(this.baseContext, startDataSetListener, year, monthOfYear, dayOfMonth)
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
                val deadDatePicker = DatePickerDialog(this.baseContext, deadDataSetListener, year, monthOfYear, dayOfMonth)
                deadDatePicker.show()
            }
        }
    }
    private fun startMainActivity(_tagString: String) {
        val intent = Intent(this@DetailActivity.applicationContext, MainActivity::class.java)
        intent.putExtra(KEY_TAG_STR, _tagString) // TODO new Item , exiting Item
        startActivity(intent, null)
    }
    private fun updateItemsRelated(_item:ToDoItem){
        if(_item.succeeding != EMPTY_ITEM) {
            for (i in itemList.indices) {
                if (itemList[i].title == _item.succeeding) itemList[i].preceding = _item.title
            }
        }
        if(_item.preceding != EMPTY_ITEM) {
            for (i in itemList.indices) {
                if (itemList[i].title == _item.preceding) itemList[i].succeeding = _item.title
            }
        }

    }
}
