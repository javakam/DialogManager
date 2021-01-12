# DialogManager
- [**GitHub**](https://github.com/javakam/DialogManager)👉<https://github.com/javakam/DialogManager>

- [**Blog**](https://juejin.cn/post/6916791502161051656/)👉<https://juejin.cn/post/6916791502161051656/>

## 预览(Preview)
<img src="https://raw.githubusercontent.com/javakam/DialogManager/master/screenshot/func.gif" width="310" height="620"/>

## 导入(Import)
> 🍎很轻量, 目前只有五个类

```
repositories {
  maven { url "https://dl.bintray.com/javakam/AndoDialog" }
}

implementation 'ando.dialog:core:1.0.3'
implementation 'ando.dialog:usage:1.0.3'
```

## 用法(Usage)
```kotlin
fun showLoadingDialog() {
      //建议设置一个主题样式
      DialogManager.with(this, R.style.AndoLoadingDialog)
          //useDialogFragment()表示由`DialogFragment`实现, useDialog()表示由`Dialog`实现, 默认为`DialogFragment`实现
          .useDialogFragment()
          //设置显示布局             
          .setContentView(R.layout.layout_ando_dialog_loading) { v ->
              v.findViewById<View>(R.id.progressbar_ando_dialog_loading).visibility = View.VISIBLE
          }
          .setTitle("Title")//在配置了 `<item name="android:windowNoTitle">false</item>` 后才会生效
          .setWidth(200)//设置宽
          .setHeight(200)//设置高
          .setSize(200, 200)//设置宽高
          .setAnimationId(R.style.AndoBottomDialogAnimation)//设置动画
          .setCancelable(true)
          .setCanceledOnTouchOutside(true)
          .setDimAmount(0.7F) //设置背景透明度, 0 ~ 1 之间,0为透明,1为不透明. 只要该值不是 -1, 就会应用该值
          .setDimmedBehind(false) //设置背景透明, false透明, true不透明
          .addOnGlobalLayoutListener { width, height -> }//获取显示后的真实宽高
          .setOnCancelListener {} //取消监听
          .setOnDismissListener {}//关闭监听
          .setOnKeyListener { dialog, keyCode, event -> true }//按键监听
          .setOnShowListener {}//显示监听
          .apply {
              //显示之前配置,如:
              //dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
          }
          .show()
          .apply {
              //显示之后配置, 效果和`setOnShowListener`相同
          }
          
      //Dialog是否正在显示
      DialogManager.isShowing()
      
      //Dialog显示后动态改变一些参数
      changeDialogSize()
}

/**
 * 动态改变显示中的Dialog位置/宽高/动画等
 */
fun changeDialogSize() {
    View.postDelayed({
        //改变弹窗宽高(Change the width and height of the dialog)
        DialogManager.setWidth(280)
        DialogManager.setHeight(280)
        DialogManager.applySize()
        
        //控制背景亮度(Control background brightness)
        DialogManager.setDimAmount(0.3F)
        DialogManager.applyDimAmount()
        
        //or 直接移除背景变暗(Directly remove the background darkening)
        //DialogManager.dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }, 1500)
```

## 同时支持`Dialog`和`DialogFragment`(Support both `Dialog` and `Dialog Fragment`)
- Dialog: useDialog() ; DialogFragment: useDialogFragment()

## 控制背景变暗(Control the darkening of the background)
- Window.addFlags/clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

## `Dialog.Window`设置(`Dialog.Window` settings)
### 1. `show()`之前设置(Set before `Dialog.show`)

- Dialog/Window.requestWindowFeature(Window.FEATURE_LEFT_ICON)

### 2. `show()`之后设置(Set after `Dialog.show`)
- `Window`相关属性(WindowManager.LayoutParams), 如动态改变`Dialog`的位置、宽高、动画、背景等

`Window` related properties (WindowManager.LayoutParams), such as dynamically changing the position, width, height, animation, background, etc. of `Dialog`

- `setFeatureXXX` 相关方法, 如 `setFeatureDrawable/setFeatureDrawableResource/setFeatureDrawableUri/setFeatureDrawableAlpha`

`setFeatureXXX` related methods, such as `setFeatureDrawable/setFeatureDrawableResource/setFeatureDrawableUri/setFeatureDrawableAlpha`

- `setFeatureXXX` 方法必须在`Dialog.show`之前设置`requestWindowFeature`才能生效,
否则出现BUG:java.lang.RuntimeException: The feature has not been requested

The `setFeatureXXX` method must set `requestWindowFeature` before `Dialog.show` to take effect,
otherwise a BUG:java.lang.RuntimeException: The feature has not been requested

- 🍎 通常在`show`执行后或者`setOnShowListener`中设置`window`属性

Usually set the `window` property after `show` is executed or in `set On Show Listener`

```kotlin
setOnShowListener {
    //对`Window`的设置需要在`Dialog`显示后才有效果
    //The setting of `Window` needs to be effective after `Dialog` is displayed
    val attributes = DialogManager.getDialog().window?.attributes
    attributes?.apply {
        width = 800
        height = 500
        gravity = Gravity.CENTER //居中显示 (Center display)
        dimAmount = 0.5f         //背景透明度 (Background transparency)  0 ~ 1
    }
    DialogManager.getDialog().window?.attributes = attributes
}
```

## `Dialog`设置动画(`Dialog` set animation)
### 1. 准备配置文件
`anim_ando_dialog_bottom_in.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<translate xmlns:android="http://schemas.android.com/apk/res/android"
    android:duration="@integer/integer_ando_dialog_bottom_translate_duration"
    android:fromXDelta="0"
    android:fromYDelta="100%"
    android:toXDelta="0"
    android:toYDelta="0" />
```
`anim_ando_dialog_bottom_out.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<translate xmlns:android="http://schemas.android.com/apk/res/android"
    android:duration="@integer/integer_ando_dialog_bottom_translate_duration"
    android:fromXDelta="0"
    android:fromYDelta="0"
    android:toXDelta="0"
    android:toYDelta="100%" />
```
`styles.xml`
```xml
<style name="AndoBottomDialogAnimation">
    <item name="android:windowEnterAnimation">@anim/anim_ando_dialog_bottom_in</item>
    <item name="android:windowExitAnimation">@anim/anim_ando_dialog_bottom_out</item>
</style>
```
### 2. 设置动画(Set animation)
```kotlin
Dialog.window.setWindowAnimations(R.style.AndoBottomDialogAnimation)
```
> 注意: `Dialog`设置动画在`new BottomDialog(context, R.style.AndoBottomDialogAnimation)`时设置是无效的, 必须在`show`之后再对`Window`
设置动画(`setWindowAnimations`)才能生效。经实际测试发现 API 5.0~11都是如此。

## `Dialog`设置圆角(`Dialog` set rounded corners)
> 在`Dialog`的`setContentView`之后(即`show`之后)设置`window.setBackgroundDrawableResource(R.drawable.rectangle_ando_dialog_bottom)`

`rectangle_ando_dialog_bottom.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <corners
        android:topLeftRadius="10dp"
        android:topRightRadius="10dp" />

    <solid android:color="@android:color/white" />
</shape>
```
就是给`Window`加上个`background`

## `Dialog`设置宽高(`Dialog` set width and height)
> 要在`setContentView`外包一层`FrameLayout`防止宽高设置无效问题

🍎 `LoadingDialog`样式的弹窗提供了两种实现方案,一种是`animated-rotate`/`rotate`直接配置动画方式,另一种是`android.view.animation.AnimationUtils`;
其中的布局文件需要包一层`FragmeLayout`

```xml
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:gravity="center">

    <LinearLayout
        android:layout_width="200dp"
        android:layout_height="150dp"
        android:layout_gravity="center"
        android:orientation="vertical">

        ...
    </LinearLayout>
</FrameLayout>
```

> `Dialog`的`setContentView(layoutId)`和`setContentView(view)`是不一样的, 如果使用的是`layoutId:Int`则不需要外面套一层`FrameLayout`,
但如果是用的`view:View`, 则必须在自定义布局的最外层在套一层其它布局,如`FrameLayout`。前者用的是`LayoutInflater.inflate(layoutId,mContentParent,true)`
而后者用的是`mContentParent.addView(view)`即`LayoutInflater.inflate(view,null,false)` . 我们看下`inflate`方法的特性:

### inflate(view, null);/inflate(resource, null, true/false);
只创建view，view没有LayoutParams值，然后直接返回view.
xml布局中最外层的layout_width、layout_height将失效

### inflate(resource, root);/inflate(resource, root, true);
创建view, 然后执行root.addView(view, params), 最后返回root

> 综上所述, `Dialog`宽高无效问题, 本质上就是`LayoutInflater.inflate`不同方法之间差异的问题.
其中的`mContentParent:ViewGroup`由`PhoneWindow.installDecor()`创建. 详见: `PhoneWindow.setContentView`

### `DialogManager`中已处理该问题(This issue has been dealt with in `Dialog Manager`)

```kotlin
fun setContentView(
    layoutId: Int,
    block: ((Dialog?, View) -> Unit)? = null
): DialogManager {
    FrameLayout(mContext ?: return this).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        contentView = LayoutInflater.from(mContext).inflate(layoutId, this, true)
        ...
    }
    return this
}
```

## 总结(Summary)

1. DialogFragment源码中加载视图用的是 Dialog.setContentView(View)

2. 如果要改变`Window`属性, 可以在`onStart`中处理。因为`DialogFragment.onStart`中执行了`Dialog.show()`

## 感谢(Thanks)
`Android源码在线阅读` <https://www.androidos.net.cn>

`Android Dialog - Rounded Corners and Transparency` <https://stackoverflow.com/questions/16861310/android-dialog-rounded-corners-and-transparency>

`LayoutInflater的正确使用姿势` <https://www.jianshu.com/p/74bb29077690>

`LayoutInflater中inflate方法的区别` <https://blog.csdn.net/u012702547/article/details/52628453>

## 遇到的BUG(BUG encountered)
- java.lang.RuntimeException: The feature has not been requested

Fixed: `see above`

- android.util.AndroidRuntimeException: requestFeature() must be called before adding content

Fixed: `setContentView(...)`之前设置即可

- `java.lang.IllegalStateException: Fragment xxx not associated with a fragment manager.`

- `java.lang.IllegalArgumentException: View not attached to window manager`

Fixed: <https://stackoverflow.com/questions/2224676/android-view-not-attached-to-window-manager>

- WindowManager: android.view.WindowLeaked: Activity ando.dialog.sample.MainActivity
has leaked window DecorView@54f9439[MainActivity] that was originally added here

如果只是处理`Dialog`在`Acticity.onConfigurationChanged`出现的问题
(If you just deal with the problem of `Dialog` in `Activity.onConfigurationChanged`)

```kotlin
//若`AndroidManifest.xml`中已经配置了`android:configChanges="orientation|screenSize|screenLayout|smallestScreenSize"`则不需要设置该项
Acticity/Context.registerComponentCallbacks(object : ComponentCallbacks {
    override fun onConfigurationChanged(newConfig: Configuration) {
        dialog?.dismiss()
    }
    override fun onLowMemory() {
    }
})
```
在`onDestroy`中也加上销毁

```kotlin
override fun onDestroy() {
    super.onDestroy()
    DialogManager.dismiss()
}
```

- java.lang.IllegalStateException: This ViewTreeObserver is not alive, call getViewTreeObserver() again

Fixed:

```kotlin
fun addOnGlobalLayoutListener(onGlobalLayout: (width: Int, height: Int) -> Unit): DialogManager {
    contentView?.viewTreeObserver?.addOnGlobalLayoutListener(object :
        ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            contentView?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
            onGlobalLayout.invoke(contentView?.width ?: 0, contentView?.height ?: 0)
        }
    })
    return this
}
```
