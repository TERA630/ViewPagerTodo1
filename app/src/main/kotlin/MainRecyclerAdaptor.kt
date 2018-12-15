package com.example.yoshi.viewpagertodo1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.yoshi.viewpagertodo1.databinding.RowItemBinding
import kotlinx.android.synthetic.main.row_item.view.*


class DiffCallback(val oldList: List<FilteredToDoItem>, val newList: List<FilteredToDoItem>) : DiffUtil.Callback() {
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
        private var mList: MutableList<FilteredToDoItem>,
        private val model: MainViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var listener: OnItemClickListener

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val ivh = holder as ItemViewHolder
        ivh.mBinding.item = mList[position].item
        ivh.mBinding.periodViewer.text = buildPeriodTextFromItem(mList[position].item)
        if(mList[position].item.succeeding == EMPTY_ITEM) ivh.mBinding.openChildToggle.visibility = View.GONE

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
            notifyItemRemoved(position)
        }
        ivh.itemView.openChildToggle.setOnClickListener{ v ->
            if(ivh.isItemOpened) {
                ivh.isItemOpened = false
                ivh.mBinding.periodViewer.visibility = View.GONE
                notifyItemChanged(position)
            } else {
                ivh.isItemOpened = true
                ivh.mBinding.childViewer.adapter = ArrayAdapter(v.context,R.layout.list_plaintext,model.findSucceedingItems(mList[position].item.title))
                ivh.mBinding.periodViewer.visibility = View.VISIBLE
                notifyItemChanged(position)
            }

        }

    }

    override fun getItemCount(): Int = mList.size
    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mBinding = RowItemBinding.bind(itemView)
        var isItemOpened:Boolean = false
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val rowView = LayoutInflater.from(parent.context).inflate(R.layout.row_item, parent, false)
        return ItemViewHolder(rowView)
    }

}
