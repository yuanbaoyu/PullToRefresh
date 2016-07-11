package com.xinghai.pulltorefresh.extras.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.FrameLayout;

import com.xinghai.pulltorefresh.PullToRefreshBase;
import com.xinghai.pulltorefresh.internal.EmptyViewMethodAccessor;

/**
 * Created by yuanbaoyu on 2016/6/6.
 * @author yuanbaoyu
 * 支持设置emptyview
 */
public class PullToRefreshRecyclerViewEmptySupport extends PullToRefreshBase<RecyclerViewEmptySupport> {

    public PullToRefreshRecyclerViewEmptySupport(Context context) {
        super(context);
    }

    public PullToRefreshRecyclerViewEmptySupport(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullToRefreshRecyclerViewEmptySupport(Context context, Mode mode) {
        super(context, mode);
    }

    public PullToRefreshRecyclerViewEmptySupport(Context context, Mode mode, AnimationStyle animStyle) {
        super(context, mode, animStyle);
    }

    @Override
    public final Orientation getPullToRefreshScrollDirection() {
        return Orientation.VERTICAL;
    }

    @Override
    protected RecyclerViewEmptySupport createRefreshableView(Context context,
                                                 AttributeSet attrs) {
        RecyclerViewEmptySupport recyclerView = new RecyclerViewEmptySupport(context, attrs);
        return recyclerView;
    }

    @Override
    protected boolean isReadyForPullStart() {
        return isFirstItemVisible();
    }

    @Override
    protected boolean isReadyForPullEnd() {
        return isLastItemVisible();
    }

    /**
     * @Description:
     *
     * @return boolean:
     */
    private boolean isFirstItemVisible() {
        final RecyclerView.Adapter<?> adapter = getRefreshableView().getAdapter();

        if (null == adapter || adapter.getItemCount() == 0) {
            if (DEBUG) {
                Log.d(LOG_TAG, "isFirstItemVisible. Empty View.");
            }
            return true;

        } else {

            if (getFirstVisiblePosition() == 0) {
                return mRefreshableView.getChildAt(0).getTop() >= mRefreshableView
                        .getTop();
            }
        }

        return false;
    }

    /**
     * @Description:
     *
     * @return int: 位置
     */
    private int getFirstVisiblePosition() {
        View firstVisibleChild = mRefreshableView.getChildAt(0);
        return firstVisibleChild != null ? mRefreshableView
                .getChildAdapterPosition(firstVisibleChild) : -1;
    }

    /**
     * @Description:
     *
     * @return boolean:
     * @version 1.0
     * @date 2015-9-23
     * @Author zhou.wenkai
     */
    private boolean isLastItemVisible() {
        final RecyclerView.Adapter<?> adapter = getRefreshableView().getAdapter();

        if (null == adapter || adapter.getItemCount() == 0) {
            if (DEBUG) {
                Log.d(LOG_TAG, "isLastItemVisible. Empty View.");
            }
            return false;

        } else {

            int lastVisiblePosition = getLastVisiblePosition();
            if(lastVisiblePosition >= mRefreshableView.getAdapter().getItemCount()-1) {
                return mRefreshableView.getChildAt(
                        mRefreshableView.getChildCount() - 1).getBottom() <= mRefreshableView
                        .getBottom();
            }
        }

        return false;
    }

    /**
     * @Description:
     *
     */
    private int getLastVisiblePosition() {
        View lastVisibleChild = mRefreshableView.getChildAt(mRefreshableView
                .getChildCount() - 1);
        return lastVisibleChild != null ? mRefreshableView
                .getChildAdapterPosition(lastVisibleChild) : -1;
    }

    /**
     * Pass-through method for {@link PullToRefreshBase#getRefreshableView()
     * getRefreshableView()}.
     * {@link AdapterView#setAdapter(Adapter)}
     * setAdapter(adapter)}. This is just for convenience!
     *
     * @param adapter - Adapter to set
     */
    public void setAdapter(RecyclerView.Adapter adapter) {
        ((RecyclerViewEmptySupport) mRefreshableView).setAdapter(adapter);
    }

    /**
     * Sets the Empty View to be used by the Adapter View.
     *
     * We need it handle it ourselves so that we can Pull-to-Refresh when the
     * Empty View is shown.
     *
     * Please note, you do <strong>not</strong> usually need to call this method
     * yourself. Calling setEmptyView on the AdapterView will automatically call
     * this method and set everything up. This includes when the Android
     * Framework automatically sets the Empty View based on it's ID.
     *
     * @param newEmptyView - Empty View to be used
     */
    public final void setEmptyView(View newEmptyView) {
        FrameLayout refreshableViewWrapper = getRefreshableViewWrapper();

        if (null != newEmptyView) {
            // New view needs to be clickable so that Android recognizes it as a
            // target for Touch Events
            newEmptyView.setClickable(true);

            if(null != tagView && tagView.getParent() != null)
                ((ViewGroup)tagView.getParent()).removeView(tagView);

            ViewParent newEmptyViewParent = newEmptyView.getParent();
            if (null != newEmptyViewParent && newEmptyViewParent instanceof ViewGroup) {
                ((ViewGroup) newEmptyViewParent).removeView(newEmptyView);
            }

            // We need to convert any LayoutParams so that it works in our
            // FrameLayout
            FrameLayout.LayoutParams lp = convertEmptyViewLayoutParams(newEmptyView.getLayoutParams());
            if (null != lp) {
                refreshableViewWrapper.addView(newEmptyView, lp);
            } else {
                refreshableViewWrapper.addView(newEmptyView);
            }

            tagView = newEmptyView;
        }

        if (mRefreshableView instanceof EmptyViewMethodAccessor) {
            ((EmptyViewMethodAccessor) mRefreshableView).setEmptyViewInternal(newEmptyView);
        } else {
            mRefreshableView.setEmptyView(newEmptyView);
        }
        mEmptyView = newEmptyView;
    }

    private View tagView;
    private View mEmptyView;

    private static FrameLayout.LayoutParams convertEmptyViewLayoutParams(ViewGroup.LayoutParams lp) {
        FrameLayout.LayoutParams newLp = null;

        if (null != lp) {
            newLp = new FrameLayout.LayoutParams(lp);

            if (lp instanceof LayoutParams) {
                newLp.gravity = ((LayoutParams) lp).gravity;
            } else {
                newLp.gravity = Gravity.CENTER;
            }
        }

        return newLp;
    }

    public void autoRefresh(){
        this.postDelayed(new Runnable() {

            @Override
            public void run() {
                setRefreshing(true);
            }
        }, 100);
    }


}
