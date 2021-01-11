# DialogManager

## 导入

```
repositories {
  maven { url "https://dl.bintray.com/javakam/AndoDialog" }
}

implementation 'ando.dialog:core:1.0.2'
implementation 'ando.dialog:usage:1.0.2'
```

## 同时支持`Dialog`和`DialogFragment`
- Dialog: useDialog() ; DialogFragment: useDialogFragment()

## 开启/关闭背景变暗
- 开启/关闭背景变暗 Window.addFlags/clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

## Dialog使用注意
### 1. `Dialog.show`之前设置
- Dialog/Window.requestWindowFeature(Window.FEATURE_LEFT_ICON)

### 2. `Dialog.show`之后设置
- Window相关属性(WindowManager.LayoutParams), 如动态改变Dialog的宽高、动画等

- `setFeatureXXX` 相关方法, 如 setFeatureDrawable/setFeatureDrawableResource/setFeatureDrawableUri/setFeatureDrawableAlpha

- `setFeatureXXX` 方法必须在`Dialog.show`之前设置`requestWindowFeature`才能生效,
否则出现BUG:java.lang.RuntimeException: The feature has not been requested

## `Dialog`设置`Window`
> 需要在`setOnShowListener`中设置`window`属性才会生效

```kotlin
.setOnShowListener {
    //对Dialog.Window的设置需要在显示后才有效果 ╮(╯▽╰)╭
    val attributes = DialogManager.getDialog().window?.attributes
    attributes?.apply {
        width = 800
        height = 500
        gravity = Gravity.CENTER //居中显示
        dimAmount = 0.5f         //背景透明度  取值范围 0 ~ 1
    }
    DialogManager.getDialog().window?.attributes = attributes
}
```

## `Dialog`配置圆角样式问题
> 在`Dialog`的`setContentView`之后设置`window.setBackgroundDrawableResource(R.drawable.rectangle_ando_dialog_bottom)`

`rectangle_ando_dialog_bottom.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <corners
        android:topLeftRadius="@dimen/dimen_ando_dialog_bottom_top_radius"
        android:topRightRadius="@dimen/dimen_ando_dialog_bottom_top_radius" />

    <solid android:color="@color/color_ando_dialog_white" />
</shape>
```
🍎 上面两种方式本质上是相同的, 就是给`Dialog`的`window`加上个`background`

## `Dialog`动画
### 准备配置文件
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
### 设置`Dialog`动画
```kotlin
Dialog.window.setWindowAnimations(R.style.AndoBottomDialogAnimation)
```
> 注意: `Dialog`设置动画在`new BottomDialog(context, R.style.AndoBottomDialogAnimation)`是无效的, 经实际测试发现 API 5.0~11
都是如此。

可见通过构造器传入的`themeResId`只是应用在了`ContextThemeWrapper(context, themeResId)`上, 而不是直接应用在`Window`
属性上面

## `Dialog`显示宽高与`dp`设置不匹配问题
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

### `DialogManager`中已处理该问题
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

## 总结

1. DialogFragment源码中加载视图用的是 Dialog.setContentView(View)

2. 如果要改变`Window`属性, 可以在`onStart`中处理。因为`DialogFragment.onStart`中执行了`Dialog.show()`

## Thanks
`Android源码在线阅读` <https://www.androidos.net.cn>

`Android Dialog - Rounded Corners and Transparency` <https://stackoverflow.com/questions/16861310/android-dialog-rounded-corners-and-transparency>

`LayoutInflater的正确使用姿势` <https://www.jianshu.com/p/74bb29077690>

`LayoutInflater中inflate方法的区别` <https://blog.csdn.net/u012702547/article/details/52628453>

## Bug Fix
- android.util.AndroidRuntimeException: requestFeature() must be called before adding content

`setContentView(...)`之前设置即可

- java.lang.IllegalStateException: Fragment BaseDialogFragment{d53478e (a87e9bdb-56b6-46f3-ab1b-3f0d71cdd024)} not associated with a fragment manager.

- `java.lang.IllegalArgumentException: View not attached to window manager`

<https://stackoverflow.com/questions/2224676/android-view-not-attached-to-window-manager>


- WindowManager: android.view.WindowLeaked: Activity ando.dialog.sample.MainActivity
has leaked window DecorView@54f9439[MainActivity] that was originally added here

如果只是处理`Dialog`在`Acticity.onConfigurationChanged`出现的问题
(If you just deal with the problem of `Dialog` in `Activity.onConfigurationChanged`)

```kotlin
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
