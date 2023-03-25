package ru.kozlovss.workingcontacts.presentation.mywall.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.kozlovss.workingcontacts.R

class MyPostsListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_posts_list, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() = MyPostsListFragment()
    }
}