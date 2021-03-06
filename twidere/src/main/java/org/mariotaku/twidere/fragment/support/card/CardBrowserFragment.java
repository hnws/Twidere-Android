/*
 * Twidere - Twitter client for Android
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

package org.mariotaku.twidere.fragment.support.card;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.webkit.WebSettings;
import android.webkit.WebView;

import org.mariotaku.twidere.fragment.support.SupportBrowserFragment;

/**
 * Created by mariotaku on 15/1/6.
 */
public class CardBrowserFragment extends SupportBrowserFragment {
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final WebView view = getWebView();
        final WebSettings settings = view.getSettings();
        settings.setBuiltInZoomControls(false);
    }

    public static CardBrowserFragment show(@NonNull String uri) {
        final Bundle args = new Bundle();
        args.putString(EXTRA_URI, uri);
        final CardBrowserFragment fragment = new CardBrowserFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
