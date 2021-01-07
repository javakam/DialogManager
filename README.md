# DialogManager

## 导入

```
repositories {
  maven { url "https://dl.bintray.com/javakam/AndoDialog" }
}

implementation 'ando.dialog:core:1.0.0'
implementation 'ando.dialog:usage:1.0.0'
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
> 两种方案: 一是在`styles.xml`配置属性`<item name="android:windowBackground">@drawable/ando_dialog_shape_bg_gray</item>`;
二是在`Dialog`创建之前通过`Window`进行设置`window.setBackgroundDrawableResource(R.drawable.rectangle_dialog_margin);`

`rectangle_dialog_margin.xml`
```xml
<!--Drawable距离View右边边缘的距离-->
<?xml version="1.0" encoding="utf-8"?>
<inset xmlns:android="http://schemas.android.com/apk/res/android"
    android:drawable="@drawable/rectangle_common_dialog"
    android:insetBottom="0dp"
    android:insetLeft="36dp"
    android:insetRight="36dp"
    android:insetTop="0dp" />
```
`rectangle_common_dialog.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <corners android:radius="20dp" />
</shape>
```
🍎 上面两种方式本质上是相同的, 就是给`Dialog`的`window`加上个`background`

## `Dialog`显示宽高与`dp`设置不匹配问题
> 要在`setContentView`外包一层`FrameLayout`防止宽高设置无效问题

🍎 `LoadingDialog`样式的弹窗提供了两种实现方案,一种是`animated-rotate`/`rotate`直接配置动画方式,另一种是`android.view.animation.AnimationUtils`;
其中的布局文件需要包一层`FragmeLayout`

```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:gravity="center">

    <LinearLayout
        android:layout_width="@dimen/dimen_ando_dialog_loading_width"
        android:layout_height="@dimen/dimen_ando_dialog_loading_height"
        android:layout_gravity="center"
        android:gravity="center"
        android:orientation="vertical"
        tools:background="@android:color/holo_blue_dark"
        tools:ignore="UselessParent">

        <ProgressBar
            android:id="@id/progressbar_ando_dialog_loading"
            style="@style/AndoLoadingDialogProgressBarStyle"
            android:layout_gravity="center_horizontal"
            android:visibility="gone"
            tools:visibility="gone" />

        <ImageView
            android:id="@id/iv_ando_dialog_loading"
            style="@style/AndoLoadingDialogImageViewStyle"
            android:visibility="gone"
            tools:ignore="ContentDescription"
            tools:visibility="visible" />

        <TextView
            android:id="@id/tv_ando_dialog_loading_text"
            style="@style/AndoLoadingDialogTextStyle"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:singleLine="true" />
    </LinearLayout>

</FrameLayout>
```

> DialogFragment源码中加载视图用的是 Dialog.setContentView(View)