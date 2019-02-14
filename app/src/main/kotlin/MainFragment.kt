package com.example.yoshi.viewpagertodo1

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.get
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_main.*

//　Fragmentのイベント処理の責務

class MainFragment : Fragment() {
    private lateinit var model: MainViewModel
    private lateinit var mAdapter: MainRecyclerAdaptor
    private var mPosition = 0
    companion object {
        // instance生成時に（今プロジェクトではViewPagerから）Fragment作成に必要な変数をセットしておく。
        fun newInstance(position: Int): MainFragment {
            val bundle = Bundle()
            val newFragment = MainFragment()
            bundle.putInt("pagePosition", position)
            newFragment.arguments = bundle
            return newFragment
        }
    }
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        model = ViewModelProviders.of(this.activity!!).get()
        mPosition = this.arguments?.getInt("pagePosition") ?: 0
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val list = model.getItemListByPosition(mPosition)
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
                        model.deleteItem(numberToCall, view.context)
                    }
                    R.id.recyclerViewMenu -> {

                        val window = SubContextWindow()
                        window.create(this@MainFragment.context!!)

                    }
                }
            }
        })

    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, fragment_frame)
    }
    // リスト更新時にアダプターへ通知する部分の設定
    override fun onStart() {
        super.onStart()
        // itemList 更新時に実行される。
        model.itemList.observe(this, Observer {
            val list = model.getItemListByPosition(mPosition)
            val diff = DiffUtil.calculateDiff((DiffCallback(mAdapter.mList, list)), false)
            mAdapter.setListOfAdapter(list)
            diff.dispatchUpdatesTo(mAdapter)
        })
    }
    private fun runAnimation(recyclerView: RecyclerView) {
        val controller: LayoutAnimationController = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_falldown)
        recyclerView.layoutAnimation = controller
        recyclerView.adapter?.notifyDataSetChanged()
                ?: throw Exception("error in animation giving adaptor")
        recyclerView.scheduleLayoutAnimation()
    }
}
