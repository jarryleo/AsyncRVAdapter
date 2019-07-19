package cn.leo.adapter

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import cn.leo.adapter.ui.mainactivity2.MainActivity2Fragment

class MainActivity2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity2_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, MainActivity2Fragment.newInstance())
                    .commitNow()
        }
        test()
    }

    private fun test() {
        val adapter = TestRVAdapter()

        adapter.edit(1){
            it.content="12345"
        }
    }

}
