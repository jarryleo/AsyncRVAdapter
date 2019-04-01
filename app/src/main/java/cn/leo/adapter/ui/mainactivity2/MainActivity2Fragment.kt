package cn.leo.adapter.ui.mainactivity2

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.leo.adapter.R
import cn.leo.adapter.StatePager
import kotlinx.android.synthetic.main.main_activity2_fragment.*

class MainActivity2Fragment : Fragment() {

    companion object {
        fun newInstance() = MainActivity2Fragment()
    }

    private lateinit var viewModel: MainActivity2ViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_activity2_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainActivity2ViewModel::class.java)
        // TODO: Use the ViewModel

        val build = StatePager.builder(message)
                .emptyViewLayout(R.layout.pager_empty)
                .build()

        build.showEmpty()
    }

}
