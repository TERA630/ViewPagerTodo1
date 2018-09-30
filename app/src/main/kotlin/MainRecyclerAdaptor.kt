package com.example.yoshi.viewpagertodo1

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.yoshi.viewpagertodo1.R.id.itemTitle
import com.example.yoshi.viewpagertodo1.databinding.RowItemBinding
import kotlinx.android.synthetic.main.row_item.view.*

class MainRecyclerAdaptor(private var mList: MutableList<FilteredToDoItem>, private val model: MainViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var listener: OnItemClickListener
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        if (mList.isEmpty()) {
            Log.i("test", "Filtered list was empty ")
            val alteredItem = FilteredToDoItem(model.getItemList().size, ToDoItem("new Item",
                    1.0f, false, false, true, model.currentDateStr, false, model.filterSpinnerStrList[1]))
            mList.add(alteredItem)
        }
        super.onAttachedToRecyclerView(recyclerView)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val ivh = holder as ItemViewHolder
//        ivh.itemView.itemTitle.text = mList[position].item.title
        ivh.mBinding.item = mList[position].item
        ivh.mBinding.periodViewer.text
        val stringBuilder = StringBuilder(if (mList[position].item.hasStartLine) {
            mList[position].item.startLine + "～"
        } else {
            "～"
        })
        if (mList[position].item.hasDeadLine) {
            stringBuilder.append(mList[position].item.deadLine)
        } else { stringBuilder.append(mList[position].item.isDone.toString())}

        ivh.mBinding.periodViewer.text = stringBuilder.toString()
 //     ivh.itemView.itemTitle.isChecked = mList[position].item.isDone
        ivh.mBinding.itemTitle.setOnCheckedChangeListener { v, boolean ->
            Log.i("test","$itemTitle check changed.to $boolean.")
            mList[position].item.isDone = boolean
            notifyItemChanged(position)
            model.getItemList()[mList[position].unFilter].isDone = boolean
        }
        ivh.itemView.editBtn.setOnClickListener { v: View ->
            listener.onClick(v, mList[position].unFilter)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val rowView = LayoutInflater.from(parent.context).inflate(R.layout.row_item, parent, false)
        return ItemViewHolder(rowView)
    }

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
