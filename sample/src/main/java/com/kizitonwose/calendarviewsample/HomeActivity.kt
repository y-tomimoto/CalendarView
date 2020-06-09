package com.kizitonwose.calendarviewsample

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.home_activity.*


class HomeActivity : AppCompatActivity() {

    private val examplesAdapter =  HomeOptionsAdapter { ExampleItem ->
        //ここが引数になっている。
        val fragment = ExampleItem.createView() // ここにはdata内に宣言されているものが入る。
        // ここで、戻り値をエル。
        supportFragmentManager.beginTransaction() // ここでfragmentを実行している //  あー、これは返し方を定義しているのね、、、
                .run {
                    // これはクラスの型を確認している、
                    if (fragment is Example1Fragment || fragment is Example4Fragment || fragment is Example5Fragment) {
                        return@run setCustomAnimations(
                                R.anim.slide_in_up,
                                R.anim.fade_out,
                                R.anim.fade_in,
                                R.anim.slide_out_down
                        )
                    }
                    return@run setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left,
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                    )
                } 
                .add(R.id.homeContainer, fragment, fragment.javaClass.simpleName) // ここにrepもあるのね。そしてここにはtagが入る

                // 3つ目の引数はなんだ？
                .addToBackStack(fragment.javaClass.simpleName) // これで戻るボタンを押すとfragmentに戻れる。一回これなしでやってみる。今のアクティビテぃの後続としてfragmentを採用するかたちか。
                .commit() // これが設定の反映ね。
        // ここまでがtapしたときの動き。これがhome画面からtapしたときの遷移になる。
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)
        setSupportActionBar(homeToolbar)
        // このexam- はレイアウトになる。どこでinitしているのかは謎。importか？
        examplesRv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        examplesRv.adapter = examplesAdapter // ここでadaptorを採用 このときはそのままぶち込み案件
        examplesRv.addItemDecoration(DividerItemDecoration(this, RecyclerView.VERTICAL)) //  ここで枠線を採用
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean { // いまどのフラグメントにいるのかを保持している
        return when (item.itemId) {
            android.R.id.home -> onBackPressed().let { true }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
