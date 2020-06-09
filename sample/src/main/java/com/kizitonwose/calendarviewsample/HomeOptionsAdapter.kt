package com.kizitonwose.calendarviewsample

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.home_options_item_view.*

data class ExampleItem(@StringRes val titleRes: Int, @StringRes val subtitleRes: Int, val createView: () -> BaseFragment)

// これもただのリサイクらviewだわ
// これはコンストラクタ
class HomeOptionsAdapter(val onClick: (ExampleItem) -> Unit) :
    RecyclerView.Adapter<HomeOptionsAdapter.HomeOptionsViewHolder>() { // これはクラス内のクラスを継承している。そして、ここでは、ジェネリクスとして、viewHolderを渡している。

    // これはただのデータクラス。コンストラクタに関数を引き渡している。これはBaaeFragmentをreturnする関数がすべて
    val examples = listOf(
        ExampleItem(R.string.example_1_title, R.string.example_1_subtitle) { Example1Fragment() },
        ExampleItem(R.string.example_2_title, R.string.example_2_subtitle) { Example2Fragment() },
        ExampleItem(R.string.example_3_title, R.string.example_3_subtitle) { Example3Fragment() },
        ExampleItem(R.string.example_4_title, R.string.example_4_subtitle) { Example4Fragment() },
        ExampleItem(R.string.example_5_title, R.string.example_5_subtitle) { Example5Fragment() },
        ExampleItem(R.string.example_6_title, R.string.example_6_subtitle) { Example6Fragment() },
        ExampleItem(R.string.example_7_title, R.string.example_7_subtitle) { Example7Fragment() }
    )

    // この3つはobstractになっているんだろう。

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeOptionsViewHolder { // 戻り値の型がHomeOptionsViewHolderということ
        return HomeOptionsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.home_options_item_view, parent, false)
        )
    }

    override fun onBindViewHolder(viewHolder: HomeOptionsViewHolder, position: Int) {
        viewHolder.bind(examples[position])
    }

    override fun getItemCount(): Int = examples.size

    // ここでクラスを生成している。
    inner class HomeOptionsViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

        init {
            itemView.setOnClickListener {
                onClick(examples[adapterPosition])// ここにクリックされた時に挙動を書いてある。クリックしたらこの関数を実行するみたいだな、
            }
        }

        fun bind(item: ExampleItem) {
            val context = itemView.context

            itemOptionTitle.text = if (item.titleRes != 0) context.getString(item.titleRes) else null
            itemOptionTitle.isVisible = itemOptionTitle.text.isNotBlank()

            itemOptionSubtitle.text = if (item.subtitleRes != 0) context.getString(item.subtitleRes) else null
            itemOptionSubtitle.isVisible = itemOptionSubtitle.text.isNotBlank()
        }
    }

}