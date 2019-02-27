package com.example.yoshi.viewpagertodo1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.yoshi.viewpagertodo1.databinding.RowItemBinding
import kotlinx.android.synthetic.main.row_item.view.*


class DiffCallback(private val oldList: List<FilteredToDoItem>,
                   private val newList: List<FilteredToDoItem>) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return (oldList[oldItemPosition].unFilter == newList[newItemPosition].unFilter)
    }
    override fun getOldListSize(): Int {
        return oldList.size
    }
    override fun getNewListSize(): Int {
        return newList.size
    }
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition].item
        val newItem = newList[newItemPosition].item
        return (oldItem.title == newItem.title) && (oldItem.tagString == newItem.tagString) &&
                (oldItem.hasStartLine == newItem.hasStartLine) && (oldItem.hasDeadLine == newItem.hasDeadLine) &&
                (oldItem.startLine == newItem.deadLine) && (oldItem.deadLine == newItem.deadLine) &&
                (oldItem.isDone == newItem.isDone)
    }
}

class MainRecyclerAdaptor(
        var mList: MutableList<FilteredToDoItem>,
        private val model: MainViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var handler: OnItemClickHandler

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // 各々のセルの表示、イベントの設定
        val ivh = holder as ItemViewHolder
        ivh.mPosition = position

        ivh.mBinding.item = mList[position].item
        ivh.mBinding.periodViewer.text = buildPeriodText(mList[position].item)

        ivh.mBinding.itemTitle.setOnCheckedChangeListener { _, boolean ->
            mList[position].item.isDone = boolean
            val indexOfRawList = mList[position].unFilter
            model.rawItemList[indexOfRawList].isDone = boolean
            notifyItemChanged(position)
        }
        ivh.itemView.recyclerViewMenu.setOnClickListener { v:View->
            handler.onClick(v, mList[position].unFilter)
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
        fun onClick(view: View, _numberToDeal: Int)
    }
    fun setListOfAdapter(list : MutableList<FilteredToDoItem>){
        this.mList = list
    }

    fun setOnItemClickHandler(listener: MainRecyclerAdaptor.OnItemClickHandler) {
        this.handler = handler
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val rowView = LayoutInflater.from(parent.context).inflate(R.layout.row_item, parent, false)
        return ItemViewHolder(rowView)
    }

}
