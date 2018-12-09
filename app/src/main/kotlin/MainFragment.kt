package com.example.yoshi.viewpagertodo1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.get
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_main.*

// Viewmodelに依存
//

class MainFragment : Fragment() {
    private lateinit var model: MainViewModel
    lateinit var mAdapter: MainRecyclerAdaptor
    lateinit var mTag:String
    private var mPosition = 0

    companion object {
        fun newInstance(tagString: String, position: Int): MainFragment {
            val bundle = Bundle()
            val newFragment = MainFragment()
            bundle.putString("tagString", tagString)
            bundle.putInt("pagePosition", position)
            newFragment.arguments = bundle
            return newFragment
        }
    }
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        model = ViewModelProviders.of(this.activity!!).get()
        mTag = this.arguments?.getString("tagString") ?: model.tagList[0]
        mPosition = this.arguments?.getInt("pagePosition") ?: 0
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val filterStr = mTag
        val list = model.getItemListWithTag(filterStr)
        mAdapter = MainRecyclerAdaptor(list, model)
        recycler_view.adapter = mAdapter
        runAnimation(recycler_view)

        mAdapter.setOnItemClickListener(object : MainRecyclerAdaptor.OnItemClickListener {
            override fun onClick(view: View, numberToCall: Int) {
                when (view.id) {
                    R.id.editBtn -> {
                        (this@MainFragment.activity as MainActivity).startDetailActivity(mPosition, numberToCall)
                    }
                    R.id.delBtn -> {
                        model.deleteItem(numberToCall)
                    }
                }
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, fragment_frame)
    }
    override fun onStart() {
        super.onStart()
        // itemList 更新時に実行される。
        model.itemList.observe(this, Observer {
            val filterStr = this.arguments!!.getString("tagString") ?: ""
            val list = model.getItemListWithTag(filterStr)
            mAdapter.setListOfAdapter(list)
            mAdapter.notifyDataSetChanged()
        })
    }
    private fun runAnimation(recyclerView: RecyclerView) {
        val controller: LayoutAnimationController = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_falldown)
        recyclerView.layoutAnimation = controller
        recyclerView.adapter?.let { it.notifyDataSetChanged() }
                ?: throw Exception("error in animation giving adaptor")
        recyclerView.scheduleLayoutAnimation()
    }
}
