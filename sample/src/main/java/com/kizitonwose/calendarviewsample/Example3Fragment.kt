package com.kizitonwose.calendarviewsample


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.calendar_day_legend.view.*
import kotlinx.android.synthetic.main.example_3_calendar_day.view.*
import kotlinx.android.synthetic.main.example_3_event_item_view.*
import kotlinx.android.synthetic.main.example_3_fragment.*
import kotlinx.android.synthetic.main.home_activity.*
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

private val Context.inputMethodManager // ソフトキーボードからアプリケーションに情報を受け渡すためのAPI
    // このget()は謎。多分systemServie
    get() = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

// これはdata class。データを保持するためだけのもの。
data class Event(val id: String, val text: String, val date: LocalDate)

// このクラス自体が、RecyclerView.Adapterを継承していて、その引数が、Example3EventsViewHolder なのか。
class Example3EventsAdapter(val onClick: (Event) -> Unit) : // Unit はvoidの意味。ここで渡したOnclickは、戻り値を必要としない。このOnclickはデータを受け取る

    // これはAdapter。<> には内部クラスを渡している。
    //  Example3EventsViewHolder　ViewHolderを元に拡張しているクラスになっている。これは間違いなさそう。ではこの（）はなんだ。多分コンストラクタになる。
    // このAdapterは、ViewHolderを継承した新しいクラスなのだろうか

    // これはViewの作成・表示されるViewとデータの結びつけを行う。
    // ここで引数に渡しているViewHolderとはなんだ？
    // あくまでこれは型引数であって、純粋な引数でないのか。
    // Adapterは型引数に、ViewHolderを extendsしたクラスを要求している
    RecyclerView.Adapter<Example3EventsAdapter.Example3EventsViewHolder>() {

        val events = mutableListOf<Event>() // eventを格納するList。ここにイベントを打ち込む

        // このViewGroupが、1つのレコードみたいなもんで、これを構成するパーツが、いくつかあって、それぞれにtypeがつく　
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Example3EventsViewHolder {
            return Example3EventsViewHolder(parent.inflate(R.layout.example_3_event_item_view))
        }

        // このViewHolderに、、、eventをセットしている
        override fun onBindViewHolder(viewHolder: Example3EventsViewHolder, position: Int) {
            viewHolder.bind(events[position]) // ここで表示位置を指定
        }

        override fun getItemCount(): Int = events.size //配列の数を取得

         // これもクラス。constructorとしてViewを受け取っていて、
         // ここでViewへの参照を持っておく。これにより毎回Viewを参照する必要がなくなる
        inner class Example3EventsViewHolder(override val containerView: View) :
            // このViewHolderを継承しているとおもったら、2つ継承している？？
            // このカンマはなんだ？？1つはcontainerView（引数）をコンストラクタとしているのがわかる。しかしもうひとつは？？コンストラクタがなにので、
            RecyclerView.ViewHolder(containerView), LayoutContainer { // このLayoutXContainerでは、Viewのアクセスをキャッシュしてくれるinterface
             //と思ったけどやっぱこれクラスでなく、関数だな。継承の文法で、メソッドとinterfaceが実装されている謎
             // まぁクラスと言いはるからクラスとしてやるか。そして、これは複数のinterfaceを継承している形 : https://blog.y-yuki.net/entry/2019/05/20/100000

            init { // ここはinitializer。インスタンスが生成されるときに実行される。
                itemView.setOnClickListener {
                    onClick(events[adapterPosition]) // => ここでonClickの引数には、eventが渡されている。
                    // adapterPosition これはなんだ。多分アクションがあったときの位置
                }
            }

             // これはイベントをわたして、テキストを取得するやつ
            fun bind(event: Event) {
                itemEventText.text = event.text
            }
        }

}
// これ確か継承だったよな
// Flagmentについて調べて見るか。
// このクラスが呼ばれて、最終的にFragmentを返しているみたい。
// これ。BaseFragmentを継承している。そしてinterfaceも。

class Example3Fragment : BaseFragment(), HasBackButton { // ここでFlagmentクラスと、HasBackButton　interfaceを継承している

    private val eventsAdapter = Example3EventsAdapter {
        // これがイベント。タップされたらdeleteボタンをメッセージと表示する
        AlertDialog.Builder(requireContext()) // このContextは一旦無視
            .setMessage(R.string.example_3_dialog_delete_confirmation) //
            .setPositiveButton(R.string.delete) { _, _ ->
                deleteEvent(it) // これは、表示したdeleteボタンのイベントやな。 it はなにか知らん。これはプラスになる。
                // 多分ここに、イベントをタップした先のインフレータなどを作成する必要がある。
            }
            .setNegativeButton(R.string.close, null)
            .show()
    }

    // これが右下のタップ。予定入れるときのダイアログです。
    private val inputDialog by lazy {
        val editText = AppCompatEditText(requireContext())
        // ここで多分ポジションを設定している。これが変われば、画面上の位置が変わるはず
        val layout = FrameLayout(requireContext()).apply {
            // Setting the padding on the EditText only pads the input area
            // not the entire EditText so we wrap it in a FrameLayout.
            val padding = dpToPx(20, requireContext())
            setPadding(padding, padding, padding, padding)
            addView(editText, FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
        }
        AlertDialog.Builder(requireContext()) // これがダイアログになる。
            .setTitle(getString(R.string.example_3_input_dialog_title))
            .setView(layout)
            .setPositiveButton(R.string.save) { _, _ ->
                saveEvent(editText.text.toString())
                // Prepare EditText for reuse.
                editText.setText("")
            }
            .setNegativeButton(R.string.close, null)
            .create()
            .apply {
                setOnShowListener {
                    // Show the keyboard
                    editText.requestFocus()
                    context.inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                }
                setOnDismissListener {
                    // Hide the keyboard
                    context.inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                }
            }
    }

    override val titleRes: Int = R.string.example_3_title // これは入り口に表示するやつか。

    private var selectedDate: LocalDate? = null// これはなにかは知らん
    private val today = LocalDate.now() //これは本日の参考にするはず。

    // これをheaderに表示しているな。なるほど。
    private val titleSameYearFormatter = DateTimeFormatter.ofPattern("MMMM")
    private val titleFormatter = DateTimeFormatter.ofPattern("MMM yyyy")
    private val selectionFormatter = DateTimeFormatter.ofPattern("d MMM yyyy")
    private val events = mutableMapOf<LocalDate, List<Event>>()

    // コンストラクタとしてLayoutInflaterクラスを実装している。
    // container 内にLayoutInflaterで生成したpartを当てはめるよの意//これどこで読んでる？
    // containerの値は、もともともっているのでは？？
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.example_3_fragment, container, false) // このクラスが実行されたらレイアウトを返す
        // ここで返しているのはあれやな、画面した半分のところ。このなかにリストが追加される、headerも動的に変わっているのね。
        // ここが実際にinflateを返しているな。
    }

    // レイアウトを返して、これが実行される。
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState) //これはお決まりみたいなもんだろう。


        // 下半分
        // 作成したlayout内のxmlには動的にアクセスできるとかなんだろうな。
        exThreeRv.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false) // これが横スワイプとか縦スワイプを管理するやつ。
        exThreeRv.adapter = eventsAdapter // ここには、追加したイベント自体が受け渡される。
        exThreeRv.addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL)) // これで罫線を通過しているのね。

        // 上半分
        val daysOfWeek = daysOfWeekFromLocale() // これは月から日までの日付を入れるところ
        val currentMonth = YearMonth.now() // いま何月か
        // ここがCalendar部分のレイアウトか。
        // 前後10ヶ月を作成している
        exThreeCalendar.setup(currentMonth.minusMonths(10), currentMonth.plusMonths(10), daysOfWeek.first()) // 多分これはCalendarの型を生成している。
        exThreeCalendar.scrollToMonth(currentMonth) // 今月までスクロールしている

        // これは何をしてんねん
        if (savedInstanceState == null) {
            exThreeCalendar.post {
                // Show today's events initially.
                selectDate(today)// これは初期イベントを設定している様子。
                // 完成度をどのくらいにするかは考えものだな。
            }
        }

        // ViewContainerを継承している
        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay // Will be set when this container is bound.
            val textView = view.exThreeDayText
            val dotView = view.exThreeDotView //　あ、これはdayを生成しているみたいだな、1ずつ並ぶやつ

            init { // ここではviewをタップしたときなにをしている？
                // day.ownerが、、、
                    view.setOnClickListener {
                    if (day.owner == DayOwner.THIS_MONTH) { //day.viewコンテナには、dayとmanceがあって、その組み合わせを確認しているとか。
                        selectDate(day.date)
                    }
                }
            }
        }

        // このexThreeCalendarは、りさいくらviewを継承しているCalenderViewを元に作成されている。
        // これはdayviewCalendarに
        // 拡張関数
        exThreeCalendar.dayBinder = object : DayBinder<DayViewContainer> { // これは変数になる。ここに打ち込んだら、CalendarViewで処理をする感じ。
            //  てか、処理をboot strapから見ていったほうが早そうだな。
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day // これはbindを利用している、イベントテキストをわりふっているのか　？
                val textView = container.textView // これは？
                val dotView = container.dotView // これはdotの有無を割り振っている様子だな、

                textView.text = day.date.dayOfMonth.toString() // これは、、、、、

                if (day.owner == DayOwner.THIS_MONTH) {
                    textView.makeVisible()
                    when (day.date) {
                        today -> {
                            textView.setTextColorRes(R.color.example_3_white)
                            textView.setBackgroundResource(R.drawable.example_3_today_bg)
                            dotView.makeInVisible()
                        }
                        selectedDate -> {
                            textView.setTextColorRes(R.color.example_3_blue)
                            textView.setBackgroundResource(R.drawable.example_3_selected_bg)
                            dotView.makeInVisible()
                        }
                        else -> {
                            textView.setTextColorRes(R.color.example_3_black)
                            textView.background = null
                            dotView.isVisible = events[day.date].orEmpty().isNotEmpty()
                        }
                    }
                } else {
                    textView.makeInVisible()
                    dotView.makeInVisible()
                }
            }
        }

        // これは無名関数
        // スクロールした際に実行されるやつ
        exThreeCalendar.monthScrollListener = {
            requireActivity().homeToolbar.title = if (it.year == today.year) {
                titleSameYearFormatter.format(it.yearMonth)
            } else {
                titleFormatter.format(it.yearMonth)
            }

            // Select the first day of the month when
            // we scroll to a new month.
            selectDate(it.yearMonth.atDay(1)) // 最初を1に設定している
        }

        class MonthViewContainer(view: View) : ViewContainer(view) {
            val legendLayout = view.legendLayout
        }

        exThreeCalendar.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                // Setup each header day text if we have not done that already.
                if (container.legendLayout.tag == null) {
                    container.legendLayout.tag = month.yearMonth
                    container.legendLayout.children.map { it as TextView }.forEachIndexed { index, tv ->
                        tv.text = daysOfWeek[index].name.first().toString()
                        tv.setTextColorRes(R.color.example_3_black)
                    }
                }
            }
        }

        exThreeAddButton.setOnClickListener {
            inputDialog.show()
        }
    }

    // ここまでが実行される


    // ここから下は雑多な関数群。

    //==============================================================================================
    //==============================================================================================

    private fun selectDate(date: LocalDate) {
        if (selectedDate != date) {
            val oldDate = selectedDate
            selectedDate = date
            oldDate?.let { exThreeCalendar.notifyDateChanged(it) }
            exThreeCalendar.notifyDateChanged(date)
            updateAdapterForDate(date)
        }
    }

    private fun saveEvent(text: String) {
        if (text.isBlank()) {
            Toast.makeText(requireContext(), R.string.example_3_empty_input_text, Toast.LENGTH_LONG).show()
        } else {
            selectedDate?.let {
                events[it] = events[it].orEmpty().plus(Event(UUID.randomUUID().toString(), text, it))
                updateAdapterForDate(it)
            }
        }
    }

    private fun deleteEvent(event: Event) {
        val date = event.date
        events[date] = events[date].orEmpty().minus(event)
        updateAdapterForDate(date)
    }

    private fun updateAdapterForDate(date: LocalDate) {
        eventsAdapter.events.clear()
        eventsAdapter.events.addAll(events[date].orEmpty())
        eventsAdapter.notifyDataSetChanged()
        exThreeSelectedDateText.text = selectionFormatter.format(date)
    }

    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity).homeToolbar.setBackgroundColor(requireContext().getColorCompat(R.color.example_3_toolbar_color))
        requireActivity().window.statusBarColor = requireContext().getColorCompat(R.color.example_3_statusbar_color)
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).homeToolbar.setBackgroundColor(requireContext().getColorCompat(R.color.colorPrimary))
        requireActivity().window.statusBarColor = requireContext().getColorCompat(R.color.colorPrimaryDark)
    }
}
