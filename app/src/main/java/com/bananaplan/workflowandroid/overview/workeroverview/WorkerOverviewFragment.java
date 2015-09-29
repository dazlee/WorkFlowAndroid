package com.bananaplan.workflowandroid.overview.workeroverview;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.Factory;
import com.bananaplan.workflowandroid.data.Worker;
import com.bananaplan.workflowandroid.main.MainActivity;
import com.bananaplan.workflowandroid.overview.StatusFragment;
import com.bananaplan.workflowandroid.overview.TaskItemFragment;
import com.bananaplan.workflowandroid.utility.Utils;
import com.bananaplan.workflowandroid.utility.data.IconSpinnerAdapter;
import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.utility.TabManager;

import java.util.ArrayList;

/**
 * Created by Ben on 2015/8/1.
 */
public class WorkerOverviewFragment extends Fragment implements TextWatcher, AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener
        , View.OnClickListener, TabHost.OnTabChangeListener {

    public static class TAB_TAG {
        private static final String TASK_ITEMS                  = "tab_tag_task_items";
        private static final String WORKER_STATUS              = "tab_tag_worker_status";
        private static final String WORKER_ATTENDANCE_STATUS = "tab_tag_worker_attendance_status";
    }

    private Spinner mFactoriesSpinner;
    private EditText mWorkerSearchEditText;
    private ListView mWorkerListView;
    private ImageView mIvWorkerAvatar;
    private TextView mTvWorkerName;
    private TextView mTvWorkerTitle;
    private TextView mTvWorkerFactoryName;
    private TextView mTvWorkerAddress;
    private TextView mTvWorkerPhone;
    private TextView mTvEditWorker;
    private TabHost mTabHost;

    private FactorySpinnerAdapter mFactorySpinnerAdapter;
    private WorkerLisViewAdapter mWorkerLisViewAdapter;
    private Worker mSelectedWorker;

    private TabManager mTabMgr;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_worker_ov, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // findview
        mFactoriesSpinner = (Spinner) getActivity().findViewById(R.id.ov_leftpane_spinner);
        mWorkerSearchEditText = (EditText) getActivity().findViewById(R.id.ov_leftpane_search_edittext);
        mWorkerListView = (ListView) getActivity().findViewById(R.id.ov_leftpane_listview);
        mIvWorkerAvatar = (ImageView) getActivity().findViewById(R.id.worker_ov_right_pane_worker_avatar);
        mTvWorkerName = (TextView) getActivity().findViewById(R.id.worker_ov_right_pane_worker_name);
        mTvWorkerTitle = (TextView) getActivity().findViewById(R.id.worker_ov_right_pane_worker_title);
        mTvWorkerFactoryName = (TextView) getActivity().findViewById(R.id.worker_ov_right_pane_worker_factory_name);
        mTvWorkerAddress = (TextView) getActivity().findViewById(R.id.worker_ov_right_pane_worker_address);
        mTvWorkerPhone = (TextView) getActivity().findViewById(R.id.worker_ov_right_pane_worker_phone);
        getActivity().findViewById(R.id.worker_ov_right_pane_edit_worker).setOnClickListener(this);
        mTabHost = (TabHost) getActivity().findViewById(R.id.worker_ov_right_pane_tab_host);
        mTabHost.setup();
        mTabMgr = new TabManager((MainActivity) getActivity(), mTabHost, android.R.id.tabcontent);
        setupTabs();

        // factory spinner
        mFactorySpinnerAdapter = new FactorySpinnerAdapter(getFactoriesSpinnerData());
        mFactoriesSpinner.setAdapter(mFactorySpinnerAdapter);
        mFactoriesSpinner.setOnItemSelectedListener(this);

        // search worker edittext
        mWorkerSearchEditText.addTextChangedListener(this);

        // worker listview
        mWorkerLisViewAdapter = new WorkerLisViewAdapter(getWorkerListViewAdapterData());
        mWorkerListView.setAdapter(mWorkerLisViewAdapter);
        mWorkerListView.setOnItemClickListener(this);

        if (mWorkerLisViewAdapter.getCount() > 0) {
            mSelectedWorker = mWorkerLisViewAdapter.getItem(0);
            onWorkerSelected(mSelectedWorker);
        }
    }

    private void setupTabs() {
        Bundle bundle1 = new Bundle();
        TabHost.TabSpec taskItemsTabSpec = mTabHost.newTabSpec(TAB_TAG.TASK_ITEMS)
                .setIndicator(getTabTitleView(TAB_TAG.TASK_ITEMS));
        bundle1.putString(TaskItemFragment.FROM, getClass().getSimpleName());
        mTabMgr.addTab(taskItemsTabSpec, TaskItemFragment.class, bundle1);

        TabHost.TabSpec workerStatusTabSpec = mTabHost.newTabSpec(TAB_TAG.WORKER_STATUS)
                .setIndicator(getTabTitleView(TAB_TAG.WORKER_STATUS));
        Bundle bundle2 = new Bundle();
        bundle2.putString(StatusFragment.FROM, getClass().getSimpleName());
        mTabMgr.addTab(workerStatusTabSpec, StatusFragment.class, bundle2);

        TabHost.TabSpec workerAttendanceStatusTabSpec = mTabHost.newTabSpec(TAB_TAG.WORKER_ATTENDANCE_STATUS)
                .setIndicator(getTabTitleView(TAB_TAG.WORKER_ATTENDANCE_STATUS));
        mTabMgr.addTab(workerAttendanceStatusTabSpec, AttendanceStatusFragment.class, null);
    }

    private View getTabTitleView(final String tag) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.tab, null);
        int titleResId;
        switch (tag) {
            case TAB_TAG.TASK_ITEMS:
                titleResId = R.string.worker_ov_worker_tab_title_task_items;
                break;
            case TAB_TAG.WORKER_STATUS:
                titleResId = R.string.worker_ov_worker_tab_title_worker_status;
                break;
            case TAB_TAG.WORKER_ATTENDANCE_STATUS:
                titleResId = R.string.worker_ov_worker_tab_title_worker_attendance_status;
                break;
            default:
                titleResId = -1;
                break;
        }
        String text = titleResId != -1 ? getResources().getString(titleResId) : "";
        ((TextView) view.findViewById(R.id.tab_title)).setText(text);
        return view;
    }

    private ArrayList<Factory> getFactoriesSpinnerData() {
        ArrayList<Factory> tmp = new ArrayList<>();
        tmp.add(new Factory("", getResources().getString(R.string.worker_ov_all_factories))); // all factories
        tmp.addAll(WorkingData.getInstance(getActivity()).getFactories());
        return tmp;
    }

    private class FactorySpinnerAdapter extends IconSpinnerAdapter<Factory> {

        public FactorySpinnerAdapter(ArrayList<Factory> objects) {
            super(getActivity(), -1, objects);
        }

        @Override
        public Factory getItem(int position) {
            return (Factory) super.getItem(position);
        }

        @Override
        public String getDropdownSpinnerViewDisplayString(int position) {
            return getItem(position).name;
        }

        @Override
        public String getSpinnerViewDisplayString(int position) {
            return getItem(position).name;
        }

        @Override
        public boolean isDropdownSelectedIconVisible(int position) {
            return position == mFactoriesSpinner.getSelectedItemPosition();
        }

        @Override
        public int getSpinnerIconResourceId() {
            return R.drawable.ic_business_black;
        }
    }

    private ArrayList<Worker> getWorkerListViewAdapterData() {
        ArrayList<Worker> tmp = new ArrayList<>();
        for (Factory factory : WorkingData.getInstance(getActivity()).getFactories()) {
            tmp.addAll(factory.workers);
        }
        return tmp;
    }

    private class WorkerLisViewAdapter extends ArrayAdapter<Worker> implements Filterable {
        private int mSelectedPosition = 0;
        private CustomFilter mFilter;
        private ArrayList<Worker> mOrigData;
        private ArrayList<Worker> mFilteredData;

        public WorkerLisViewAdapter(ArrayList<Worker> workers) {
            super(getActivity(), -1, workers);
            mOrigData = workers;
            mFilteredData = new ArrayList<>(mOrigData);
            mFilter = new CustomFilter();
        }

        @Override
        public int getCount() {
            return mFilteredData.size();
        }

        @Override
        public Worker getItem(int position) {
            return mFilteredData.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.worker_ov_worker_listview_item, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            convertView.findViewById(R.id.header_divider).setVisibility(position == 0 ? View.VISIBLE : View.GONE);
            holder.avatar.setImageDrawable(getItem(position).getAvator());
            holder.name.setText(getItem(position).name);
            holder.title.setText(getItem(position).jobTitle);

            // update background of selected item
            if (position == mSelectedPosition) {
                holder.root.setBackgroundColor(getResources().getColor(R.color.blue1));
                holder.name.setTextColor(Color.WHITE);
                holder.title.setTextColor(Color.WHITE);
            } else {
                holder.root.setBackgroundColor(Color.TRANSPARENT);
                holder.name.setTextColor(getResources().getColor(R.color.black1));
                holder.title.setTextColor(getResources().getColor(R.color.gray1));
            }

            return convertView;
        }

        private class ViewHolder {
            RelativeLayout root;
            ImageView avatar;
            TextView name;
            TextView title;

            public ViewHolder(View view) {
                root = (RelativeLayout) view.findViewById(R.id.worker_ov_worker_listview_root);
                avatar = (ImageView) view.findViewById(R.id.worker_ov_worker_listview_worker_avatar);
                name = (TextView) view.findViewById(R.id.worker_ov_worker_listview_worker_name);
                title = (TextView) view.findViewById(R.id.worker_ov_worker_listview_worker_title);
            }
        }

        @Override
        public Filter getFilter() {
            return mFilter;
        }

        private class CustomFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                constraint = constraint.toString().toLowerCase();
                FilterResults result = new FilterResults();
                ArrayList<Worker> filterResult = new ArrayList<>();

                Factory selectedFactory = (Factory) mFactoriesSpinner.getSelectedItem();
                for (Worker worker : mOrigData) {
                    if ((TextUtils.isEmpty(constraint) || worker.name.toLowerCase().contains(constraint))
                            && TextUtils.isEmpty(selectedFactory.id)
                            || (Utils.isSameId(worker.factoryId, selectedFactory.id))) {
                        filterResult.add(worker);
                    }
                }

                result.values = filterResult;
                result.count = filterResult.size();
                return result;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mFilteredData.clear();
                mFilteredData.addAll((ArrayList<Worker>) results.values);
                notifyDataSetChanged();
            }
        }

        public void setSelectedPosition(int position) {
            mSelectedPosition = position;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.worker_ov_right_pane_edit_worker:
                editWorker();
                break;
            default:
                break;
        }
    }

    private void editWorker() {
        Toast.makeText(getActivity(), "Edit worker", Toast.LENGTH_SHORT).show();
        // TODO
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch(parent.getId()) {
            case R.id.ov_leftpane_listview:
                if (mSelectedWorker == mWorkerLisViewAdapter.getItem(position)) return;
                mSelectedWorker = mWorkerLisViewAdapter.getItem(position);
                mWorkerLisViewAdapter.setSelectedPosition(position);
                onWorkerSelected(mSelectedWorker);
                mWorkerLisViewAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(parent.getId()) {
            case R.id.ov_leftpane_spinner:
                oFactorySelected((Factory) mFactoriesSpinner.getSelectedItem());
                break;
            default:
                break;
        }
    }

    private void oFactorySelected(Factory factory) {
        filterWorkerList(mWorkerSearchEditText.getText().toString());
    }

    /*
     * parameter calledFromActivityCreated: since WorkerFragmentBase is created later,
     * update worker's content only when WorkerFragmentBase fragment is ready
     */
    private void onWorkerSelected(Worker worker) {
        if (worker == null) return;
        // update worker's personal info.
        mIvWorkerAvatar.setImageDrawable(worker.getAvator());
        mTvWorkerName.setText(worker.name);
        mTvWorkerTitle.setText(worker.jobTitle);
        mTvWorkerFactoryName.setText(WorkingData.getInstance(getActivity()).getFactoryById(worker.factoryId).name);
        mTvWorkerAddress.setText(getResources().getString(R.string.worker_ov_worker_address)
                + (TextUtils.isEmpty(worker.address) ? "" : worker.address));
        mTvWorkerPhone.setText(getResources().getString(R.string.worker_ov_worker_phone)
                + (TextUtils.isEmpty(worker.phone) ? "" : worker.phone));
        if (mTabMgr != null) {
            mTabMgr.selectItem(worker);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // do nothing
    }

    @Override
    public void afterTextChanged(Editable s) {
        filterWorkerList(mWorkerSearchEditText.getText().toString());
    }

    private void filterWorkerList(String query) {
        if (mWorkerLisViewAdapter == null || mWorkerLisViewAdapter.getFilter() == null) return;
        mWorkerLisViewAdapter.getFilter().filter(query);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // do nothing
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // do nothing
    }

    @Override
    public void onTabChanged(String tabId) {
        // do nothing
    }

    public Worker getSelectedWorker() {
        return mSelectedWorker;
    }
}
