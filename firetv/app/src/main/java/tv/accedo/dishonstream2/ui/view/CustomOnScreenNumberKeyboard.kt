package tv.accedo.dishonstream2.ui.view

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.util.SparseArray
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputConnection
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.databinding.ViewKeyboardNumberLayoutBinding
import tv.accedo.dishonstream2.extensions.copy
import tv.accedo.dishonstream2.ui.theme.ThemeManager

class CustomOnScreenNumberKeyboard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    LinearLayout(context, attrs, defStyleAttr), View.OnClickListener, KoinComponent {

    val binding: ViewKeyboardNumberLayoutBinding =
        ViewKeyboardNumberLayoutBinding.inflate(LayoutInflater.from(context), this, true)
    private val themeManager: ThemeManager by inject()

    // This will map the button resource id to the String value that we want to input when that button is clicked.
    private var keyValues = SparseArray<String>()

    var inputConnection: InputConnection? = null

    init {
        init(context, attrs)
        updateBorderColorBasedOnTheme()
    }

    fun getFocasable(): AppCompatTextView {
        return binding.key1
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        binding.key1.setOnClickListener(this)
        binding.key2.setOnClickListener(this)
        binding.key3.setOnClickListener(this)
        binding.key4.setOnClickListener(this)
        binding.key5.setOnClickListener(this)
        binding.key6.setOnClickListener(this)
        binding.key7.setOnClickListener(this)
        binding.key8.setOnClickListener(this)
        binding.key9.setOnClickListener(this)
        binding.key0.setOnClickListener(this)
        binding.keyDelete.setOnClickListener(this)

        // map buttons IDs to input strings

        keyValues.put(binding.key1.id, context.getString(R.string.key_1))
        keyValues.put(binding.key2.id, context.getString(R.string.key_2))
        keyValues.put(binding.key3.id, context.getString(R.string.key_3))
        keyValues.put(binding.key4.id, context.getString(R.string.key_4))
        keyValues.put(binding.key5.id, context.getString(R.string.key_5))
        keyValues.put(binding.key6.id, context.getString(R.string.key_6))
        keyValues.put(binding.key7.id, context.getString(R.string.key_7))
        keyValues.put(binding.key8.id, context.getString(R.string.key_8))
        keyValues.put(binding.key9.id, context.getString(R.string.key_9))
        keyValues.put(binding.key0.id, context.getString(R.string.key_0))
    }

    private fun updateBorderColorBasedOnTheme() {
        themeManager.getSettingOptionsBackgroundDrawable()?.let {
            with(binding) {
                binding.key1.background = it
                binding.key2.background = it.copy()
                binding.key3.background = it.copy()
                binding.key4.background = it.copy()
                binding.key5.background = it.copy()
                binding.key6.background = it.copy()
                binding.key7.background = it.copy()
                binding.key8.background = it.copy()
                binding.key9.background = it.copy()
                binding.key0.background = it.copy()
                binding.keyDelete.background = it.copy()
            }
        }
    }

    override fun onClick(v: View) {
        if (v.id == binding.keyDelete.id) {
            val selectedText = inputConnection!!.getSelectedText(0)
            if (TextUtils.isEmpty(selectedText)) {
                // no selection, so delete previous character
                inputConnection?.deleteSurroundingText(1, 0)
            } else {
                // delete the selection
                inputConnection?.commitText("", 1)
            }
        } else {
            val value = keyValues[v.id]
            inputConnection?.commitText(value, 1)
        }
    }


    fun setZoomed(enabled: Boolean) {
        binding.key0.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 27f else 22f)
        binding.key1.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 27f else 22f)
        binding.key2.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 27f else 22f)
        binding.key3.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 27f else 22f)
        binding.key4.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 27f else 22f)
        binding.key5.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 27f else 22f)
        binding.key6.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 27f else 22f)
        binding.key7.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 27f else 22f)
        binding.key8.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 27f else 22f)
        binding.key9.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (enabled) 27f else 22f)

        binding.keyDelete.layoutParams.height =
            resources.getDimension(if (enabled) R.dimen._40dp else R.dimen._40dp).toInt()
        binding.keyDelete.layoutParams.width =
            resources.getDimension(if (enabled) R.dimen._35dp else R.dimen._35dp).toInt()
    }
}