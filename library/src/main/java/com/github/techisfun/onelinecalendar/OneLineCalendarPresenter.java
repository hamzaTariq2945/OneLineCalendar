package com.github.techisfun.onelinecalendar;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author Andrea Maglie
 */
class OneLineCalendarPresenter implements OneLineCalendarContract.Presenter {

    private static final int MAX_DAYS = 365;
    private static final int MAX_SIZE = MAX_DAYS + 12;
    private List<SimpleDate> mSimpleDateList = new ArrayList<>(MAX_SIZE);
    private OneLineCalendarContract.View mView;

    OneLineCalendarPresenter(Calendar today) {
        buildSimpleDateList(today);
    }

    private void buildSimpleDateList(Calendar today) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today.getTime());

        for (int i = 0; i < MAX_DAYS; i++) {
            mSimpleDateList.add(SimpleDate.dateFrom(calendar, today));
            calendar.add(Calendar.DATE, 1);
        }
    }

    @Override
    public void dropView() {
        mView = null;
    }

    @Override
    public void takeView(OneLineCalendarContract.View view) {
        mView = view;
        populateView();
    }

    private void populateView() {
        mView.populateWithItems(mSimpleDateList);
    }

    List<SimpleDate> getSimpleDateList() {
        return mSimpleDateList;
    }

    @Override
    public RecyclerView.OnScrollListener buildOnScrollListener() {
        return new RecyclerView.OnScrollListener() {

            private LinearLayoutManager mLayoutManager = null;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (mLayoutManager == null) {
                    mLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                }

                int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();
                if (firstVisibleItemPosition == 0) {
                    return;
                }

                String text = null;
                boolean isRightScrolling = (dx >= 0);
                SimpleDate simpleDate = mSimpleDateList.get(firstVisibleItemPosition);
                if (simpleDate.getDay() == 1 ) {
                    if (isRightScrolling) {
                        text = simpleDate.getFormattedMonthName(simpleDate.getMonth());
                    } else {
                        text = simpleDate.getPreviousMonthFormatted();
                    }
                }

                if (mView != null && text != null) {
                    mView.setStickyHeaderText(text);
                }
            }
        };
    }
}
