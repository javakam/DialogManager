package ando.dialog.bottomsheet;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * # ModalBottomSheetItem
 *
 * @author javakam
 * @date 2021/4/20  9:36
 */
public class ModalBottomSheetItem implements Parcelable {

    private Integer id;
    private String title;
    private Drawable icon;

    public ModalBottomSheetItem(Integer id, String title, Drawable icon) {
        this.id = id;
        this.title = title;
        this.icon = icon;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.title);
    }

    protected ModalBottomSheetItem(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.title = in.readString();
    }

    public static final Creator<ModalBottomSheetItem> CREATOR = new Creator<ModalBottomSheetItem>() {
        @Override
        public ModalBottomSheetItem createFromParcel(Parcel source) {
            return new ModalBottomSheetItem(source);
        }

        @Override
        public ModalBottomSheetItem[] newArray(int size) {
            return new ModalBottomSheetItem[size];
        }
    };

}