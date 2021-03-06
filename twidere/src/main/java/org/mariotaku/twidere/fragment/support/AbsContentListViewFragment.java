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

package org.mariotaku.twidere.fragment.support;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.mariotaku.twidere.R;
import org.mariotaku.twidere.activity.iface.IControlBarActivity;
import org.mariotaku.twidere.activity.iface.IControlBarActivity.ControlBarOffsetListener;
import org.mariotaku.twidere.fragment.iface.RefreshScrollTopInterface;
import org.mariotaku.twidere.util.ContentListScrollListener.ContentListSupport;
import org.mariotaku.twidere.util.ThemeUtils;
import org.mariotaku.twidere.util.TwidereColorUtils;
import org.mariotaku.twidere.util.Utils;

/**
 * Created by mariotaku on 15/4/16.
 */
public abstract class AbsContentListViewFragment<A extends ListAdapter> extends BaseSupportFragment
        implements OnRefreshListener, RefreshScrollTopInterface, ControlBarOffsetListener,
        ContentListSupport {

    private View mProgressContainer;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ListView mListView;
    private View mErrorContainer;
    private ImageView mErrorIconView;
    private TextView mErrorTextView;

    private A mAdapter;

    // Data fields
    private Rect mSystemWindowsInsets = new Rect();

    @Override
    public void onControlBarOffsetChanged(IControlBarActivity activity, float offset) {
        updateRefreshProgressOffset();
    }

    @Override
    public void onRefresh() {
        triggerRefresh();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        updateRefreshProgressOffset();
    }

    @Override
    public boolean scrollToStart() {
        mListView.setSelectionFromTop(0, 0);
//        mListView.stopScroll();
        setControlVisible(true);
        return true;
    }

    @Override
    public void setControlVisible(boolean visible) {
        final FragmentActivity activity = getActivity();
        if (activity instanceof IControlBarActivity) {
            ((IControlBarActivity) activity).setControlBarVisibleAnimate(visible);
        }
    }

    @Override
    public A getAdapter() {
        return mAdapter;
    }

    @Override
    public abstract boolean isRefreshing();

    public void setRefreshing(final boolean refreshing) {
        final boolean currentRefreshing = mSwipeRefreshLayout.isRefreshing();
        if (!currentRefreshing) {
            updateRefreshProgressOffset();
        }
        if (refreshing == currentRefreshing) return;
        mSwipeRefreshLayout.setRefreshing(refreshing);
    }

    @Override
    public void onLoadMoreContents(boolean fromStart) {
        setRefreshEnabled(false);
    }

    public final ListView getListView() {
        return mListView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof IControlBarActivity) {
            ((IControlBarActivity) activity).registerControlBarOffsetListener(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content_listview, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final View view = getView();
        assert view != null;
        final Context context = view.getContext();
        final boolean compact = Utils.isCompactCards(context);
        final int backgroundColor = ThemeUtils.getThemeBackgroundColor(context);
        final int colorRes = TwidereColorUtils.getContrastYIQ(backgroundColor,
                R.color.bg_refresh_progress_color_light, R.color.bg_refresh_progress_color_dark);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(colorRes);
        mAdapter = onCreateAdapter(context, compact);
        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    updateRefreshProgressOffset();
                }
                return false;
            }
        });
        mListView.setAdapter(mAdapter);

    }

    @Override
    public void onBaseViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onBaseViewCreated(view, savedInstanceState);
        mProgressContainer = view.findViewById(R.id.progress_container);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        mListView = (ListView) view.findViewById(R.id.list_view);
        mErrorContainer = view.findViewById(R.id.error_container);
        mErrorIconView = (ImageView) view.findViewById(R.id.error_icon);
        mErrorTextView = (TextView) view.findViewById(R.id.error_text);
    }

    @Override
    public void onDetach() {
        final FragmentActivity activity = getActivity();
        if (activity instanceof IControlBarActivity) {
            ((IControlBarActivity) activity).unregisterControlBarOffsetListener(this);
        }
        super.onDetach();
    }

    @Override
    protected void fitSystemWindows(Rect insets) {
        mListView.setPadding(insets.left, insets.top, insets.right, insets.bottom);
        mErrorContainer.setPadding(insets.left, insets.top, insets.right, insets.bottom);
        mProgressContainer.setPadding(insets.left, insets.top, insets.right, insets.bottom);
        mSystemWindowsInsets.set(insets);
        updateRefreshProgressOffset();
    }

    public void setRefreshEnabled(boolean enabled) {
        mSwipeRefreshLayout.setEnabled(enabled);
    }

    @Override
    public boolean triggerRefresh() {
        return false;
    }

    @NonNull
    protected abstract A onCreateAdapter(Context context, boolean compact);

    protected final void showContent() {
        mErrorContainer.setVisibility(View.GONE);
        mProgressContainer.setVisibility(View.GONE);
        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
    }

    protected final void showProgress() {
        mErrorContainer.setVisibility(View.GONE);
        mProgressContainer.setVisibility(View.VISIBLE);
        mSwipeRefreshLayout.setVisibility(View.GONE);
    }

    protected final void showError(int icon, CharSequence text) {
        mErrorContainer.setVisibility(View.VISIBLE);
        mProgressContainer.setVisibility(View.GONE);
        mSwipeRefreshLayout.setVisibility(View.GONE);
        mErrorIconView.setImageResource(icon);
        mErrorTextView.setText(text);
    }

    protected final void showEmpty(int icon, CharSequence text) {
        mErrorContainer.setVisibility(View.VISIBLE);
        mProgressContainer.setVisibility(View.GONE);
        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
        mErrorIconView.setImageResource(icon);
        mErrorTextView.setText(text);
    }

    protected void updateRefreshProgressOffset() {
        final FragmentActivity activity = getActivity();
        if (!(activity instanceof IControlBarActivity) || mSystemWindowsInsets.top == 0 || mSwipeRefreshLayout == null
                || isRefreshing()) {
            return;
        }
        final float density = getResources().getDisplayMetrics().density;
        final int progressCircleDiameter = mSwipeRefreshLayout.getProgressCircleDiameter();
        final IControlBarActivity control = (IControlBarActivity) activity;
        final int controlBarOffsetPixels = Math.round(control.getControlBarHeight() * (1 - control.getControlBarOffset()));
        final int swipeStart = (mSystemWindowsInsets.top - controlBarOffsetPixels) - progressCircleDiameter;
        // 64: SwipeRefreshLayout.DEFAULT_CIRCLE_TARGET
        final int swipeDistance = Math.round(64 * density);
        mSwipeRefreshLayout.setProgressViewOffset(false, swipeStart, swipeStart + swipeDistance);
    }

    @Override
    public int[] findLastVisibleItemPositions() {
        return new int[]{mListView.getLastVisiblePosition()};
    }

    @Override
    public int[] findFirstVisibleItemPositions() {
        return new int[]{mListView.getFirstVisiblePosition()};
    }

    @Override
    public int getItemCount() {
        return mListView.getCount();
    }
}
