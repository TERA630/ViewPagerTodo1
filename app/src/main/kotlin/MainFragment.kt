package com.example.yoshi.viewpagertodo1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.get
import kotlinx.android.synthetic.main.fragment_main.*


class MainFragment : Fragment() {
    lateinit var model: MainViewModel

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
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, fragment_frame)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val filterStr = this.arguments!!.getString("tagString") ?: ""
        val list = if (filterStr == "all") {
            model.getItemListWithTag("")
        } else {
            model.getItemListWithTag(filterStr)
        }
        val mAdapter = MainRecyclerAdaptor(list, model)
        recycler_view.adapter = mAdapter

        mAdapter.setOnItemClickListener(object : MainRecyclerAdaptor.OnItemClickListener {
            override fun onClick(view: View, numberToCall: Int) {
                val context = MyApplication.instance?.applicationContext
                context?.let {
                    val intent = Intent(it, DetailActivity::class.java)
                    intent.putExtra("parentID", numberToCall)
                    intent.putExtra("tagString", filterStr)
                    Log.i("test", "parentID was $numberToCall")
                    //　TODO (可能なら)　Transitionを作る｡
                    startActivity(intent)
                }
            }
        }
        )
        val tabIndex = this.arguments!!.getString("tagString")
        Log.i("test", "Fragment on View Created $tabIndex")
    }
}
