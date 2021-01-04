package ando.dialog

import android.os.Bundle
import androidx.fragment.app.DialogFragment

/**
 * Title: AndoDialogFragment
 * <p>
 * Description:
 * </p>
 * @author javakam
 * @date 2021/1/4  16:48
 */
class AndoDialogFragment : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) dismiss()
    }

}