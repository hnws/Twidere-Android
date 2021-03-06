package org.mariotaku.twidere.util;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.lang.reflect.Field;

/**
 * Created by mariotaku on 16/1/3.
 */
public class ParcelUtils {

    @Nullable
    public static <T extends Parcelable> T clone(@Nullable T object) {
        if (object == null) return null;
        final Parcel parcel = Parcel.obtain();
        try {
            object.writeToParcel(parcel, 0);
            parcel.setDataPosition(0);
            final Field creatorField = object.getClass().getDeclaredField("CREATOR");
            //noinspection unchecked
            final Parcelable.Creator<T> creator = (Parcelable.Creator<T>) creatorField.get(null);
            return creator.createFromParcel(parcel);
        } catch (NoSuchFieldException e) {
            throw new NoSuchFieldError("Missing CREATOR field");
        } catch (IllegalAccessException e) {
            throw new IllegalAccessError("Can't access CREATOR field");
        } finally {
            parcel.recycle();
        }
    }
}
