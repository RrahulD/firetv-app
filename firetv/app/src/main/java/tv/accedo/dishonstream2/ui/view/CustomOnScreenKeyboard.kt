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
import androidx.appcompat.widget.AppCompatButton
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import tv.accedo.dishonstream2.R
import tv.accedo.dishonstream2.databinding.ViewKeyboardLayoutBinding
import tv.accedo.dishonstream2.extensions.copy
import tv.accedo.dishonstream2.ui.theme.ThemeManager


@OptIn(KoinApiExtension::class)
class CustomOnScreenKeyboard @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayout(context, attrs, defStyleAttr), View.OnClickListener, KoinComponent {

    val binding: ViewKeyboardLayoutBinding = ViewKeyboardLayoutBinding.inflate(LayoutInflater.from(context), this, true)
    private val themeManager: ThemeManager by inject()

    // This will map the button resource id to the String value that we want to input when that button is clicked.
    private var keyValues = SparseArray<String>()

    var inputConnection: InputConnection? = null

    init {
        init(context, attrs)
        updateBorderColorBasedOnTheme()
    }

    private fun updateBorderColorBasedOnTheme() {
        themeManager.getSettingOptionsBackgroundDrawable()?.let {
            with(binding) {
                binding.keyA.background = it
                binding.keyB.background = it.copy()
                binding.keyC.background = it.copy()
                binding.keyD.background = it.copy()
                binding.keyE.background = it.copy()
                binding.keyF.background = it.copy()
                binding.keyG.background = it.copy()
                binding.keyH.background = it.copy()
                binding.keyI.background = it.copy()
                binding.keyJ.background = it.copy()
                binding.keyK.background = it.copy()
                binding.keyL.background = it.copy()
                binding.keyM.background = it.copy()
                binding.keyN.background = it.copy()
                binding.keyO.background = it.copy()
                binding.keyP.background = it.copy()
                binding.keyQ.background = it.copy()
                binding.keyR.background = it.copy()
                binding.keyS.background = it.copy()
                binding.keyT.background = it.copy()
                binding.keyU.background = it.copy()
                binding.keyV.background = it.copy()
                binding.keyW.background = it.copy()
                binding.keyX.background = it.copy()
                binding.keyY.background = it.copy()
                binding.keyZ.background = it.copy()
                binding.key0.background = it.copy()
                binding.key1.background = it.copy()
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
                binding.keySpace.background = it.copy()
            }
        }
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        binding.keyA.setOnClickListener(this)
        binding.keyB.setOnClickListener(this)
        binding.keyC.setOnClickListener(this)
        binding.keyD.setOnClickListener(this)
        binding.keyE.setOnClickListener(this)
        binding.keyF.setOnClickListener(this)
        binding.keyG.setOnClickListener(this)
        binding.keyH.setOnClickListener(this)
        binding.keyI.setOnClickListener(this)
        binding.keyJ.setOnClickListener(this)
        binding.keyK.setOnClickListener(this)
        binding.keyL.setOnClickListener(this)
        binding.keyM.setOnClickListener(this)
        binding.keyN.setOnClickListener(this)
        binding.keyO.setOnClickListener(this)
        binding.keyP.setOnClickListener(this)
        binding.keyQ.setOnClickListener(this)
        binding.keyR.setOnClickListener(this)
        binding.keyS.setOnClickListener(this)
        binding.keyT.setOnClickListener(this)
        binding.keyU.setOnClickListener(this)
        binding.keyV.setOnClickListener(this)
        binding.keyW.setOnClickListener(this)
        binding.keyX.setOnClickListener(this)
        binding.keyY.setOnClickListener(this)
        binding.keyZ.setOnClickListener(this)
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
        binding.keySpace.setOnClickListener(this)

        // map buttons IDs to input strings
        keyValues.put(binding.keyA.id, context.getString(R.string.key_a))
        keyValues.put(binding.keyB.id, context.getString(R.string.key_b))
        keyValues.put(binding.keyC.id, context.getString(R.string.key_c))
        keyValues.put(binding.keyD.id, context.getString(R.string.key_d))
        keyValues.put(binding.keyE.id, context.getString(R.string.key_e))
        keyValues.put(binding.keyF.id, context.getString(R.string.key_f))
        keyValues.put(binding.keyG.id, context.getString(R.string.key_g))
        keyValues.put(binding.keyH.id, context.getString(R.string.key_h))
        keyValues.put(binding.keyI.id, context.getString(R.string.key_i))
        keyValues.put(binding.keyJ.id, context.getString(R.string.key_j))
        keyValues.put(binding.keyK.id, context.getString(R.string.key_k))
        keyValues.put(binding.keyL.id, context.getString(R.string.key_l))
        keyValues.put(binding.keyM.id, context.getString(R.string.key_m))
        keyValues.put(binding.keyN.id, context.getString(R.string.key_n))
        keyValues.put(binding.keyO.id, context.getString(R.string.key_o))
        keyValues.put(binding.keyP.id, context.getString(R.string.key_p))
        keyValues.put(binding.keyQ.id, context.getString(R.string.key_q))
        keyValues.put(binding.keyR.id, context.getString(R.string.key_r))
        keyValues.put(binding.keyS.id, context.getString(R.string.key_s))
        keyValues.put(binding.keyT.id, context.getString(R.string.key_t))
        keyValues.put(binding.keyU.id, context.getString(R.string.key_u))
        keyValues.put(binding.keyV.id, context.getString(R.string.key_v))
        keyValues.put(binding.keyW.id, context.getString(R.string.key_w))
        keyValues.put(binding.keyX.id, context.getString(R.string.key_x))
        keyValues.put(binding.keyY.id, context.getString(R.string.key_y))
        keyValues.put(binding.keyZ.id, context.getString(R.string.key_z))
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
        keyValues.put(binding.keySpace.id, " ")
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

    fun setZoom() {
        setBtnZoomedProperties(binding.keyA)
        setBtnZoomedProperties(binding.keyB)
        setBtnZoomedProperties(binding.keyC)
        setBtnZoomedProperties(binding.keyD)
        setBtnZoomedProperties(binding.keyE)
        setBtnZoomedProperties(binding.keyF)
        setBtnZoomedProperties(binding.keyG)
        setBtnZoomedProperties(binding.keyH)
        setBtnZoomedProperties(binding.keyI)
        setBtnZoomedProperties(binding.keyJ)
        setBtnZoomedProperties(binding.keyK)
        setBtnZoomedProperties(binding.keyL)
        setBtnZoomedProperties(binding.keyM)
        setBtnZoomedProperties(binding.keyN)
        setBtnZoomedProperties(binding.keyO)
        setBtnZoomedProperties(binding.keyP)
        setBtnZoomedProperties(binding.keyQ)
        setBtnZoomedProperties(binding.keyR)
        setBtnZoomedProperties(binding.keyS)
        setBtnZoomedProperties(binding.keyT)
        setBtnZoomedProperties(binding.keyU)
        setBtnZoomedProperties(binding.keyV)
        setBtnZoomedProperties(binding.keyW)
        setBtnZoomedProperties(binding.keyX)
        setBtnZoomedProperties(binding.keyY)
        setBtnZoomedProperties(binding.keyZ)
        setBtnZoomedProperties(binding.key0)
        setBtnZoomedProperties(binding.key1)
        setBtnZoomedProperties(binding.key2)
        setBtnZoomedProperties(binding.key3)
        setBtnZoomedProperties(binding.key4)
        setBtnZoomedProperties(binding.key5)
        setBtnZoomedProperties(binding.key6)
        setBtnZoomedProperties(binding.key7)
        setBtnZoomedProperties(binding.key8)
        setBtnZoomedProperties(binding.key9)
        binding.keySpace.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
        binding.bottomParent.layoutParams.height = resources.getDimension(R.dimen._40dp).toInt()
    }

    private fun setBtnZoomedProperties(textView: AppCompatButton) {
        textView.layoutParams.width = resources.getDimension(R.dimen._40dp).toInt()
        textView.layoutParams.height = resources.getDimension(R.dimen._45dp).toInt()
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28f)

    }
}