package com.example.yoshi.viewpagertodo1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.get
import kotlinx.android.synthetic.main.fragment_main.*

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
        mTag = this.arguments!!.getString("tagString") ?: "all"
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, fragment_frame)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val filterStr = mTag
        main_fab.setOnClickListener { v->

        val intent = Intent(context, DetailActivity::class.java)
        intent.putExtra("parentID", -1)
        intent.putExtra("tagString", filterStr)
        Log.i("test", "newItem made")
        val context = this@MainFragment.context
                ?: throw Exception("context is null at MainFragment")
            model.saveItemListToPreference(context)
        startActivity(intent)
        }
        val list = if (filterStr == "all") {
            model.getItemListWithTag("")
        } else {
            model.getItemListWithTag(filterStr)
        }
        mAdapter = MainRecyclerAdaptor(list, model)
        recycler_view.adapter = mAdapter
        mAdapter.setOnItemClickListener(object : MainRecyclerAdaptor.OnItemClickListener {
            override fun onClick(view: View, numberToCall: Int) {
                when (view.id) {
                    R.id.editBtn -> {
                        val context = this@MainFragment.context
                                ?: throw Exception("context is null at MainFragment")
                        val intent = Intent(context, DetailActivity::class.java)
                        intent.putExtra("parentID", numberToCall)
                        intent.putExtra("tagString", filterStr)
                        Log.i("test", "parentID was $numberToCall")
                        model.saveItemListToPreference(context)
                        startActivity(intent)
                    }
                    R.id.delBtn -> {
                        model.deleteItem(numberToCall)
                        Log.i("test", "$numberToCall was deleted")
                    }
                }
            }


        })

    }

    override fun onStart() {
        super.onStart()
        model.itemList.observe(this, Observer {
            Log.i("test", "${this.javaClass}@${this.hashCode()} listened the change")

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
}
