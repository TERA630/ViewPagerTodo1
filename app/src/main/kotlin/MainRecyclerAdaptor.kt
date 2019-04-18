package com.example.yoshi.viewpagertodo1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.yoshi.viewpagertodo1.databinding.RowItemBinding
import kotlinx.android.synthetic.main.row_item.view.*
class DiffCallback(private val oldList: List<ToDoItem>,
                   private val newList: List<ToDoItem>) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return (oldList[oldItemPosition].itemID == newList[newItemPosition].itemID)
    }
    override fun getOldListSize(): Int {
        return oldList.size
    }
    override fun getNewListSize(): Int {
        return newList.size
    }
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return (oldItem.title == newItem.title) && (oldItem.tagString == newItem.tagString) &&
                (oldItem.isDeleted == newItem.isDeleted) && (oldItem.upDatetime == oldItem.upDatetime) &&
                (oldItem.hasStartLine == newItem.hasStartLine) && (oldItem.hasDeadLine == newItem.hasDeadLine) &&
                (oldItem.startLine == newItem.deadLine) && (oldItem.deadLine == newItem.deadLine) &&
                (oldItem.isDone == newItem.isDone)
    }
}
class MainRecyclerAdaptor(
        var mList: MutableList<ToDoItem>,
        private val model: MainViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var handler: OnItemClickHandler
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // 各々のセルの表示、イベントの設定
        val ivh = holder as ItemViewHolder
        ivh.mPosition = position
        ivh.mBinding.item = mList[position]
        ivh.mBinding.periodViewer.text = buildPeriodText(mList[position])
        ivh.mBinding.itemTitle.setOnCheckedChangeListener { _, boolean ->
            mList[position].isDone = boolean
            val indexOfRawList = model.getIndexOfItemFromId(mList[position].itemID)
            model.getAllItemNonNull()[indexOfRawList].isDone = boolean
            notifyItemChanged(position)
        }
        ivh.itemView.recyclerViewMenu.setOnClickListener { v:View->
            val indexOfRawList = model.getIndexOfItemFromId(mList[position].itemID)
            handler.onMenuClicked(v, indexOfRawList)
            notifyItemChanged(position)
        }
    }
    override fun getItemCount(): Int = mList.size

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // 各々のセル固有のデータ
        var mPosition: Int = 0
        val mBinding: RowItemBinding = RowItemBinding.bind(itemView)
    }
    interface OnItemClickHandler {
        fun onMenuClicked(view: View, _numberToDeal: Int)
    }

    fun setListOfAdapter(list: MutableList<ToDoItem>) {
        this.mList = list
    }
    fun setOnItemClickHandler(_handler: MainRecyclerAdaptor.OnItemClickHandler) {
        this.handler = _handler
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val rowView = LayoutInflater.from(parent.context).inflate(R.layout.row_item, parent, false)
        return ItemViewHolder(rowView)
    }
}