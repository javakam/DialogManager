package ando.dialog.sample

import ando.dialog.bottomsheet.ModalBottomSheetDialogFragment
import ando.dialog.bottomsheet.ModalBottomSheetItem
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialog

class ModalActivity : AppCompatActivity() {

    var dismissibleDialog: ModalBottomSheetDialogFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modal)

        findViewById<View>(R.id.buttonWithHeader).setOnClickListener {
            dismissibleDialog = ModalBottomSheetDialogFragment.Builder()
                .setTitle("Title of modal")
                .addItem(this, R.menu.options)
                .setShowClose(false)
                .setListener(listener)
                .build()

            dismissibleDialog?.show(supportFragmentManager, "WithHeader")
        }

        findViewById<View>(R.id.buttonWithoutHeader).setOnClickListener {
            dismissibleDialog = ModalBottomSheetDialogFragment.Builder()
                .addItem(this, R.menu.options)
                .setShowClose(false)
                .setListener(listener)
                .build()

            dismissibleDialog?.show(supportFragmentManager, "WithoutHeader")
        }

        findViewById<View>(R.id.buttonGrid).setOnClickListener {
            ModalBottomSheetDialogFragment.Builder()
                .setTitle("Grid bottom layout")
                .addItem(this, R.menu.options)
                .setColumns(3)
                .setListener(listener)
                .show(supportFragmentManager, "GridLayout")
        }

        val list = mutableListOf(
            ModalBottomSheetItem(1, "QQ", ContextCompat.getDrawable(this, R.drawable.umeng_socialize_qq)),
            ModalBottomSheetItem(2, "QQ空间", ContextCompat.getDrawable(this, R.drawable.umeng_socialize_qzone)),
            ModalBottomSheetItem(3, "微信", ContextCompat.getDrawable(this, R.drawable.umeng_socialize_wechat)),
            ModalBottomSheetItem(4, "朋友圈", ContextCompat.getDrawable(this, R.drawable.umeng_socialize_wxcircle)),
        )
        findViewById<View>(R.id.buttonCustomLayout).setOnClickListener {
            ModalBottomSheetDialogFragment.Builder()
                .setTitle("Custom title and item layouts")
                .setTitleLayout(R.layout.layout_bottom_sheet_fragment_header)
                .addItem(list)
                .setItemLayout(R.layout.layout_bottom_sheet_fragment_item)
                .setColumns(3)
                .setListener(listener)
                .show(supportFragmentManager, "CustomHeader")
        }

        findViewById<View>(R.id.buttonScrollableList).setOnClickListener {
            dismissibleDialog = ModalBottomSheetDialogFragment.Builder()
                .setTitle("Scrolling layout")
                .addItem(this, R.menu.options)
                .addItem(this, R.menu.options)
                .addItem(this, R.menu.options)
                .setItemDirection(false)
                .setDraggable(true)
                .setTopRounded(false)
                .setListener(listener)
                .setCallback(object : ModalBottomSheetDialogFragment.OnDialogCreatedCallback {
                    override fun onDialogCreated(dialog: BottomSheetDialog) {
                        dialog.behavior.setPeekHeight(350, true)
                    }
                })
                .build()

            dismissibleDialog?.show(supportFragmentManager, "ScrollLayout")
        }

        findViewById<View>(R.id.buttonRounded).setOnClickListener {
            ModalBottomSheetDialogFragment.Builder()
                .setTitle("Rounded layout")
                .setTopRounded(true)
                .setItemDirection(false)
                .addItem(this, R.menu.options)
                .setListener(listener)
                .show(supportFragmentManager, "RoundedLayout")
        }

        findViewById<View>(R.id.buttonShare).setOnClickListener {
            dismissibleDialog = ModalBottomSheetDialogFragment.Builder()
                //.setTitle("分享")
                //.setTitleLayout(R.layout.layout_bottom_sheet_fragment_header)
                .addItem(this, R.menu.options)
                .setItemDirection(true)
                .setColumns(3)
                .setDraggable(true)
                .setListener(object : ModalBottomSheetDialogFragment.OnItemClickListener {
                    override fun onItemSelected(item: ModalBottomSheetItem) {
                        Toast.makeText(applicationContext, "Inner clicked on: " + item.title, Toast.LENGTH_SHORT).show()
                    }
                })
                .build()

            dismissibleDialog?.show(supportFragmentManager, "Share")
        }
    }

    private val listener = object : ModalBottomSheetDialogFragment.OnItemClickListener {
        override fun onItemSelected(item: ModalBottomSheetItem) {
            Toast.makeText(applicationContext, "clicked on: " + item.title, Toast.LENGTH_SHORT).show()
            //dismissibleDialog.dismiss();
        }
    }

}