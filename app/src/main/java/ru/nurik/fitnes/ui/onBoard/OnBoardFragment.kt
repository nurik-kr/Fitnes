package ru.nurik.fitnes.ui.onBoard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.view_onboard.*
import ru.nurik.fitnes.R

class OnBoardFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.view_onboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        val data = arguments?.get(DATA_ID) as DataOnBoard

//        val spanText = SpannableString(data.title)
        //spanText.setSpan(ForegroundColorSpan(Color.GREEN), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        tv1.text = data.textOne
        tv2.text = data.textTwo
        image.setImageResource(data.image)
    }

    companion object {
        const val DATA_ID = "DATA_ID"

        fun getInstance(data: DataOnBoard): OnBoardFragment {
            val fragment = OnBoardFragment()
            val bundle = Bundle()
            bundle.putParcelable(DATA_ID, data)
            fragment.arguments = bundle
            return fragment
        }
    }


}