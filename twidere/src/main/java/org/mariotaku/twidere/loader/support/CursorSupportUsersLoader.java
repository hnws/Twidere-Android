/*
 * 				Twidere - Twitter client for Android
 * 
 *  Copyright (C) 2012-2014 Mariotaku Lee <mariotaku.lee@gmail.com>
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

package org.mariotaku.twidere.loader.support;

import android.content.Context;
import android.support.annotation.NonNull;

import org.mariotaku.twidere.api.twitter.Twitter;
import org.mariotaku.twidere.api.twitter.TwitterException;
import org.mariotaku.twidere.api.twitter.model.PageableResponseList;
import org.mariotaku.twidere.api.twitter.model.Paging;
import org.mariotaku.twidere.api.twitter.model.User;
import org.mariotaku.twidere.model.ParcelableUser;

import java.util.List;

public abstract class CursorSupportUsersLoader extends BaseCursorSupportUsersLoader {

    public CursorSupportUsersLoader(final Context context, final long accountId, final long cursor,
                                    final List<ParcelableUser> data, boolean fromUser) {
        super(context, accountId, cursor, data, fromUser);
    }

    @NonNull
    protected abstract PageableResponseList<User> getCursoredUsers(@NonNull Twitter twitter, Paging paging)
            throws TwitterException;

    @Override
    protected final List<User> getUsers(@NonNull final Twitter twitter) throws TwitterException {
        final Paging paging = new Paging();
        paging.count(getCount());
        if (getCursor() > 0) {
            paging.setCursor(getCursor());
        }
        final PageableResponseList<User> users = getCursoredUsers(twitter, paging);
        setCursorIds(users);
        return users;
    }

}
