package com.lovechatapp.chat.activity

import android.animation.ObjectAnimator
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.widget.AppCompatTextView
import android.view.View
import com.lovechatapp.chat.R
import com.lovechatapp.chat.base.BaseActivity
import com.lovechatapp.chat.databinding.ActivityDateMineBinding
import com.lovechatapp.chat.ext.setClick
import com.lovechatapp.chat.fragment.DateMineListFragment
import kotlin.math.max
import kotlin.math.min

/**
 * 我的约会界面
 */
class DateMineActivity : BaseActivity() {
    /**视图绑定器*/
    private lateinit var mBinding: ActivityDateMineBinding
    /**上方tab单个的宽度*/
    private var mTabItemWidth = 0
    /**加载的fragment列表*/
    private val mFragments = ArrayList<DateMineListFragment>()
    /**上方tabView列表*/
    private val mTabList = ArrayList<AppCompatTextView>()
    /**上次显示的页面下标*/
    private var mLastIndex = -1
    /**当前显示的页面下标*/
    private var mCurrIndex = 0
    override fun getContentView(): View {
        //初始化视图绑定器
        mBinding = ActivityDateMineBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun onContentAdded() {
        needHeader(true)
        title = "我的约会"
        for (i in 0 until 3) {//初始化fragment列表
            val fragment = DateMineListFragment()
            val bundle = Bundle()
            bundle.putInt("tag", i)
            fragment.arguments = bundle
            mFragments.add(fragment)
        }
        //向tabView列表添加对应的视图
        mTabList.add(mBinding.labelNotGo)
        mTabList.add(mBinding.labelGone)
        mTabList.add(mBinding.labelInactive)
        //设置数据适配器和默认显示
        mBinding.contentRv.adapter = MyFragmentStateVPAdapter(supportFragmentManager)
        mBinding.contentRv.currentItem = 0
        setListeners()
        //初始化tab显示
        switchIndex()
    }

    /**设置监听*/
    private fun setListeners() {
        //ViewPager的页码改变监听
        mBinding.contentRv.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {

            }

            override fun onPageSelected(index: Int) {
                if (index != mCurrIndex) {
                    mLastIndex = mCurrIndex
                    mCurrIndex = index
                    switchIndex()
                }
            }

            override fun onPageScrollStateChanged(p0: Int) {

            }
        })
        //上方tab的点击监听
        mTabList.forEach {
            it.setClick { view ->
                mBinding.contentRv.setCurrentItem(mTabList.indexOf(view), true)
            }
        }
    }

    /**
     * 切换TabLayout的下表和文字颜色
     */
    private fun switchIndex() {
        if (mTabItemWidth == 0) {//初始化TabLayout中每一个标签的宽度
            mTabItemWidth =
                (resources.displayMetrics.widthPixels - resources.getDimensionPixelOffset(R.dimen.dp_24)) / 3 + 2
        }
        //设置上次选择页面对应标签为未选择（0~[tab 数量-1]）
        mTabList[min(mTabList.size - 1, max(mLastIndex, 0))].isSelected = false
        //设置本次选择的页面对应标签为选择（0~[tab 数量-1]）
        mTabList[min(mTabList.size - 1, max(mCurrIndex, 0))].isSelected = true
        //给游标设置水平平移的动画并启动
        val animator =
            ObjectAnimator.ofFloat(
                mBinding.indexView,
                "translationX",
                mTabItemWidth * mCurrIndex.toFloat()
            )
        animator.duration = 200
        animator.start()
    }

    /**ViewPager数据适配器内部类*/
    private inner class MyFragmentStateVPAdapter(
        fm: FragmentManager
    ) : FragmentPagerAdapter(fm) {

        /**
         * 获取页面
         * @param position  页面的位置
         * @return 返回具体页面
         */

        override fun getItem(position: Int): Fragment {
            return mFragments[position]
        }

        /**
         * 获取adapter内存储的页面个数
         * @return
         */
        override fun getCount(): Int {
            return mFragments.size
        }
    }
}