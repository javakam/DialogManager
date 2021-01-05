# DialogManager


## `Dialog`设置`Window`
> 需要在`setOnShowListener`中设置`window`属性才会生效

```kotlin
.setOnShowListener {
    //对话框显示后再设置窗体才有效果
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
<?xml version="1.0" encoding="utf-8"?><!--Drawable距离View右边边缘的距离-->
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

🍎 `LoadingDialog`样式的弹窗提供了两种实现方案,一种是`animated-rotate`,另一种是`android.view.animation.AnimationUtils`;
其中的布局文件需要包一层`FragmeLayout`

```kotlin
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:gravity="center">

    <LinearLayout
        android:layout_width="150dp"
        android:layout_height="106dp"
        android:layout_gravity="center"
        android:gravity="center"
        android:orientation="vertical"
        android:paddingLeft="21dp"
        android:paddingTop="10dp"
        android:paddingRight="21dp"
        android:paddingBottom="10dp"
        tools:background="@android:color/holo_blue_dark"
        tools:ignore="UselessParent">

        <ProgressBar
            android:id="@+id/progressBar1"
            android:layout_width="35dp"
            android:layout_height="35dp"
            android:layout_gravity="center_horizontal"
            android:indeterminateBehavior="repeat"
            android:indeterminateDrawable="@drawable/ando_dialog_loading"
            android:indeterminateDuration="20"
            android:indeterminateOnly="true"
            android:indeterminateTint="@color/color_ando_dialog_white" />

        <TextView
            android:id="@+id/tipTextView"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginTop="15dp"
            android:text="@string/str_ando_dialog_loading"
            android:textColor="@android:color/white"
            android:textSize="14sp" />
    </LinearLayout>

</FrameLayout>
```
