package com.example.mackayirb.ui.central;

import com.example.mackayirb.R;
import com.example.mackayirb.adapter.LeakAdapter;
import com.example.mackayirb.utils.BasicResourceManager;

public class CentralLeakDetectionFragment extends CentralWithRecycleViewFragment<LeakAdapter> implements CentralMvpView {
    @Override
    public int getLayoutId() {
        return R.layout.central_leak;
    }

    @Override
    public void setRecyclerViewAdapter() {
        recyclerViewAdapter = new LeakAdapter();
        recyclerViewAdapter.setFontSize(30f);
    }

    @Override
    public void updateData() {
        recyclerViewAdapter.clear();
//        for (String s:BasicResourceManager.getResources().getStringArray(R.array.LeakAlert)) {
//            recyclerViewAdapter.add(s, BasicResourceManager.getResources().getColor(R.color.Connected_Card, BasicResourceManager.getResources().newTheme()));
//        }
        recyclerViewAdapter.add(BasicResourceManager.getResources().getStringArray(R.array.LeakAlert)[0], BasicResourceManager.getResources().getColor(R.color.Disconnected_Card, BasicResourceManager.getResources().newTheme()));
        recyclerViewAdapter.add(BasicResourceManager.getResources().getStringArray(R.array.LeakAlert)[1], BasicResourceManager.getResources().getColor(R.color.Connected_Card, BasicResourceManager.getResources().newTheme()));
        recyclerViewAdapter.notifyDataSetChanged();
    }
}
