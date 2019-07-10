package com.kuky.demo.wan.android.ui.main

import android.os.Bundle
import android.view.View
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseFragment
import com.kuky.demo.wan.android.base.BaseFragmentPagerAdapter
import com.kuky.demo.wan.android.data.MainRepository
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.databinding.FragmentMainBinding
import com.kuky.demo.wan.android.ui.home.HomeFragment
import com.kuky.demo.wan.android.ui.hotproject.HotProjectFragment
import com.kuky.demo.wan.android.ui.projectcategory.ProjectCategoryFragment
import com.kuky.demo.wan.android.ui.system.KnowledgeSystemFragment
import com.kuky.demo.wan.android.ui.websitedetail.WebsiteDetailFragment
import com.kuky.demo.wan.android.ui.wxchapter.WxChapterFragment
import com.kuky.demo.wan.android.utils.GalleryTransformer
import com.kuky.demo.wan.android.utils.ScreenUtils
import com.youth.banner.listener.OnBannerListener
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.user_profile_header.view.*

/**
 * @author kuky.
 * @description 主页面 fragment 持有者
 */
class MainFragment : BaseFragment<FragmentMainBinding>() {

    private val mAdapter: BaseFragmentPagerAdapter by lazy {
        BaseFragmentPagerAdapter(
            childFragmentManager, arrayListOf(
                HomeFragment(),
                HotProjectFragment(),
                KnowledgeSystemFragment(),
                WxChapterFragment(),
                ProjectCategoryFragment()
            )
        )
    }

    private val mViewModel: MainViewModel by lazy {
        ViewModelProviders
            .of(this, MainModelFactory(MainRepository()))
            .get(MainViewModel::class.java)
    }

    override fun getLayoutId(): Int = R.layout.fragment_main

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding.holder = this@MainFragment
        mBinding.viewModel = mViewModel
        mBinding.adapter = mAdapter
        mBinding.listener = OnBannerListener { position ->
            mViewModel.banners.value?.let {
                WebsiteDetailFragment.viewDetail(
                    mNavController,
                    R.id.action_mainFragment_to_websiteDetailFragment,
                    it[position].url
                )
            }
        }

        mViewModel.hasLogin.value = PreferencesHelper.hasLogin(requireContext())

        main_page.offscreenPageLimit = mAdapter.count
        main_page.setPageTransformer(true, GalleryTransformer())

        mViewModel.getBanners()

        mViewModel.hasLogin.observe(this, Observer<Boolean> {
            val header = user_profile_drawer.getHeaderView(0)

            user_profile_drawer.menu.findItem(R.id.user_collections).isVisible = it
            user_profile_drawer.menu.findItem(R.id.login_out).isVisible = it

            header.user_name.text =
                if (it) PreferencesHelper.fetchUserName(requireContext())
                else requireContext().getString(R.string.click_to_login)

            if (!it) header.user_name.setOnClickListener {

            }
        })

        Glide.with(requireContext())
            .load(R.drawable.ic_avatar)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(ScreenUtils.dip2px(requireContext(), 80f))))
            .into(user_profile_drawer.getHeaderView(0).avatar)

        user_profile_drawer.setNavigationItemSelectedListener { menu ->
            when (menu.itemId) {
                R.id.favourite_article -> {
                }
                R.id.favourite_website -> {
                }
                R.id.about -> {
                }
                R.id.version -> {
                }
                R.id.login_out -> {
                }
            }
            true
        }
    }

    fun openSettings(view: View) = drawer.openDrawer(GravityCompat.START)

    fun searchArticles(view: View) = mNavController.navigate(R.id.action_mainFragment_to_searchFragment)
}