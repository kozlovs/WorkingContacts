package ru.kozlovss.workingcontacts.presentation.mywall.adapter.vp

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class VpAdapter(fa: FragmentActivity, private val list: List<Fragment>) : FragmentStateAdapter(fa) {
    override fun getItemCount() = list.size

    override fun createFragment(position: Int) = list[position]
}