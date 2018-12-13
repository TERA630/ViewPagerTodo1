package com.example.yoshi.viewpagertodo1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.yoshi.viewpagertodo1.databinding.RowItemBinding
import kotlinx.android.synthetic.main.row_item.view.*

private val ITEM_CALLBACK = object : DiffUtil.ItemCallback<FilteredToDoItem>() {

    override fun areItemsTheSame(oldItem: FilteredToDoItem, newItem: FilteredToDoItem): Boolean {
        return (oldItem.unFilter == newItem.unFilter)
    }
    override fun areContentsTheSame(oldItem: FilteredToDoItem, newItem: FilteredToDoItem): Boolean {
        return (oldItem.item.title == newItem.item.title) && (oldItem.item.tagString == newItem.item.tagString) &&
                (oldItem.item.hasStartLine == newItem.item.hasStartLine) && (oldItem.item.hasDeadLine == newItem.item.hasDeadLine) &&
                (oldItem.item.startLine == newItem.item.deadLine) && (oldItem.item.deadLine == newItem.item.deadLine) &&
                (oldItem.item.isDone == newItem.item.isDone)
    }
}

class MainRecyclerAdaptor(
        private var mList: MutableList<FilteredToDoItem>,
        private val model: MainViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var listener: OnItemClickListener

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val ivh = holder as ItemViewHolder
        ivh.mBinding.item = mList[position].item
        ivh.mBinding.periodViewer.text = buildPeriodTextFromItem(mList[position].item)
        if(mList[position].item.succeeding == EMPTY_ITEM) ivh.mBinding.openChildButton.visibility = View.GONE

        ivh.mBinding.itemTitle.setOnCheckedChangeListener { _, boolean ->
            mList[position].item.isDone = boolean
            val indexOfRawList = mList[position].unFilter
            model.getItemList()[indexOfRawList].item.isDone = boolean
            notifyItemChanged(position)
        }
        ivh.itemView.editBtn.setOnClickListener { v: View ->
            listener.onClick(v, mList[position].unFilter)
        }
        ivh.itemView.delBtn.setOnClickListener { v: View ->
            listener.onClick(v, mList[position].unFilter)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val rowView = LayoutInflater.from(parent.context).inflate(R.layout.row_item, parent, false)
        return ItemViewHolder(rowView)
    }

    override fun getItemCount(): Int = mList.size
    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mBinding = RowItemBinding.bind(itemView)
    }
    interface OnItemClickListener {
        fun onClick(view: View, numberToCall: Int)
    }
    fun setListOfAdapter(list : MutableList<FilteredToDoItem>){
        this.mList = list
    }
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
}
