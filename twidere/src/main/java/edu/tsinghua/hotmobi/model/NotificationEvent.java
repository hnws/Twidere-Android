/*
 *                 Twidere - Twitter client for Android
 *
 *  Copyright (C) 2012-2015 Mariotaku Lee <mariotaku.lee@gmail.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package edu.tsinghua.hotmobi.model;

import android.content.Context;
import android.media.AudioManager;
import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.bluelinelabs.logansquare.typeconverters.StringBasedTypeConverter;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;

import java.util.TimeZone;

import edu.tsinghua.hotmobi.HotMobiLogger;

/**
 * Created by mariotaku on 15/10/10.
 */
@ParcelablePlease
@JsonObject
public class NotificationEvent extends BaseEvent implements Parcelable {

    @JsonField(name = "item_id")
    long itemId;

    @JsonField(name = "item_user_id")
    long itemUserId;

    @JsonField(name = "account_id")
    long accountId;

    @JsonField(name = "type")
    String type;

    @JsonField(name = "action", typeConverter = Action.Converter.class)
    Action action;

    @JsonField(name = "ringer_mode")
    int ringerMode;

    public void setItemUserFollowing(boolean itemUserFollowing) {
        this.itemUserFollowing = itemUserFollowing;
    }

    @JsonField(name = "item_user_following")
    boolean itemUserFollowing;

    public NotificationEvent() {
    }

    public NotificationEvent(Parcel in) {
        super(in);
        NotificationEventParcelablePlease.readFromParcel(this, in);
    }

    public static NotificationEvent create(Context context, Action action, long postTime,
                                           long respondTime, String type, long accountId, long itemId,
                                           long itemUserId, boolean itemUserFollowing) {
        final NotificationEvent event = new NotificationEvent();
        event.setAction(action);
        event.setStartTime(postTime);
        event.setEndTime(respondTime);
        event.setTimeOffset(TimeZone.getDefault().getOffset(postTime));
        event.setLocation(HotMobiLogger.getCachedLatLng(context));
        event.setRingerMode(((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).getRingerMode());
        event.setType(type);
        event.setAccountId(accountId);
        event.setItemId(itemId);
        event.setItemUserId(itemUserId);
        event.setItemUserFollowing(itemUserFollowing);
        return event;
    }

    public static NotificationEvent deleted(Context context, long postTime, String type,
                                            long accountId, long itemId, long itemUserId,
                                            boolean itemUserFollowing) {
        return create(context, Action.DELETE, System.currentTimeMillis(), postTime, type, accountId,
                itemId, itemUserId, itemUserFollowing);
    }

    public static NotificationEvent open(Context context, long postTime, String type, long accountId,
                                         long itemId, long itemUserId, boolean itemUserFollowing) {
        return create(context, Action.OPEN, System.currentTimeMillis(), postTime, type, accountId,
                itemId, itemUserId, itemUserFollowing);
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getItemUserId() {
        return itemUserId;
    }

    public void setItemUserId(long itemUserId) {
        this.itemUserId = itemUserId;
    }

    public void setRingerMode(int ringerMode) {
        this.ringerMode = ringerMode;
    }

    public int getRingerMode() {
        return ringerMode;
    }

    public static boolean isSupported(String type) {
        if (type == null) return false;
        switch (type) {
            case "status":
            case "statuses":
            case "mention":
            case "mentions":
                return true;
        }
        return false;
    }

    public enum Action {
        OPEN("open"), DELETE("delete"), UNKNOWN("unknown");

        private final String value;

        Action(String value) {
            this.value = value;
        }

        public static Action parse(String action) {
            if (OPEN.value.equalsIgnoreCase(action)) {
                return OPEN;
            } else if (DELETE.value.equalsIgnoreCase(action)) {
                return DELETE;
            }
            return UNKNOWN;
        }


        public static class Converter extends StringBasedTypeConverter<Action> {

            @Override
            public Action getFromString(String string) {
                return Action.parse(string);
            }

            @Override
            public String convertToString(Action action) {
                if (action == null) return null;
                return action.value;
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        NotificationEventParcelablePlease.writeToParcel(this, dest, flags);
    }

    public static final Creator<NotificationEvent> CREATOR = new Creator<NotificationEvent>() {
        public NotificationEvent createFromParcel(Parcel source) {
            NotificationEvent target = new NotificationEvent();
            NotificationEventParcelablePlease.readFromParcel(target, source);
            return target;
        }

        public NotificationEvent[] newArray(int size) {
            return new NotificationEvent[size];
        }
    };
}
