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

    companion object {
        fun newInstance(tagString: String): MainFragment {
            val bundle = Bundle()
            val newFragment = MainFragment()
            bundle.putString("tagString", tagString)
            newFragment.arguments = bundle
            return newFragment
        }
    }
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        model = ViewModelProviders.of(this.activity!!).get()
        mTag = this.arguments!!.getString("tagString") ?: model.tagList[0]
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, fragment_frame)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val filterStr = mTag
        val list = if (filterStr == "all") {
            model.getItemListWithTag("")
        } else {
            model.getItemListWithTag(filterStr)
        }
        mAdapter = MainRecyclerAdaptor(list, model)
        recycler_view.adapter = mAdapter
        runAnimation(recycler_view)

        mAdapter.setOnItemClickListener(object : MainRecyclerAdaptor.OnItemClickListener {
            override fun onClick(view: View, numberToCall: Int) {
                when (view.id) {
                    R.id.editBtn -> {
                        val context = this@MainFragment.context
                                ?: throw Exception("context is null at MainFragment")
                        val intent = Intent(context, DetailActivity::class.java)
                        intent.putExtra("parentID", numberToCall)
                        intent.putExtra("tagString", filterStr)
                        intent.putExtra("comingPage", main_viewpager.currentItem)
                        model.saveItem(context)
                        startActivity(intent)
                    }
                    R.id.delBtn -> {
                        model.deleteItem(numberToCall)
                    }
                }
            }
        })

    }
    override fun onStart() {
        super.onStart()
        model.itemList.observe(this, Observer {
                val filterStr = this.arguments!!.getString("tagString") ?: ""
                val list = if (filterStr == "all") {
                    model.getItemListWithTag("")
                } else {
                    model.getItemListWithTag(filterStr)
                }
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
