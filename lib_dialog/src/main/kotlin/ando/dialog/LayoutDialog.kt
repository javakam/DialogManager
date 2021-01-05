package ando.dialog

import android.view.View

interface LayoutDialog {
    fun initView(view: View?){}
    fun initData(){}
    fun getLayoutId(): Int=-1
}