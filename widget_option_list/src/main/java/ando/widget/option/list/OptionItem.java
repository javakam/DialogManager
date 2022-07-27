package ando.widget.option.list;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import java.util.Objects;

/**
 * @author javakam
 * @date 2021/4/20  9:36
 */
public class OptionItem implements Parcelable {

    private Integer id;
    private String title;
    private Drawable icon;
    private boolean isChecked = false;

    public OptionItem(Integer id, String title, @Nullable Drawable icon) {
        this.id = id;
        this.title = title;
        this.icon = icon;
    }

    public OptionItem(Integer id, String title, @Nullable Drawable icon, boolean isChecked) {
        this.id = id;
        this.title = title;
        this.icon = icon;
        this.isChecked = isChecked;
    }

    @Override
    public String toString() {
        return "OptionItem{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", isChecked=" + isChecked +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OptionItem)) {
            return false;
        }
        OptionItem that = (OptionItem) o;
        return Objects.equals(id, that.id) && Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title);
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

    @Nullable
    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(@Nullable Drawable icon) {
        this.icon = icon;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.title);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            dest.writeBoolean(this.isChecked);
        }
    }

    protected OptionItem(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.title = in.readString();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.isChecked = in.readBoolean();
        }
    }

    public static final Creator<OptionItem> CREATOR = new Creator<OptionItem>() {
        @Override
        public OptionItem createFromParcel(Parcel source) {
            return new OptionItem(source);
        }

        @Override
        public OptionItem[] newArray(int size) {
            return new OptionItem[size];
        }
    };

}