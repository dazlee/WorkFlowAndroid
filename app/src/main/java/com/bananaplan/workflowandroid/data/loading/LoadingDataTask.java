package com.bananaplan.workflowandroid.data.loading;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.bananaplan.workflowandroid.data.Case;
import com.bananaplan.workflowandroid.data.Factory;
import com.bananaplan.workflowandroid.data.Task;
import com.bananaplan.workflowandroid.data.Vendor;
import com.bananaplan.workflowandroid.data.Worker;
import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.main.MainApplication;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * Async task to load data from server
 *
 * @author Danny Lin
 * @since 2015/10/1.
 */
public class LoadingDataTask extends AsyncTask<Void, Void, Void> {
    // [TODO] should move out to be a identical interface from here
    public interface OnFinishLoadingDataListener {
        void onFinishLoadingData();
        void onFailLoadingData(boolean isFailCausedByInternet);
    }

    private Context mContext;
    private OnFinishLoadingDataListener mOnFinishLoadingDataListener;

    private boolean isFailCausedByInternet = false;


    public LoadingDataTask(Context context, OnFinishLoadingDataListener listener) {
        mContext = context;
        mOnFinishLoadingDataListener = listener;
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (!MainApplication.sUseTestData) {
            if (RestfulUtils.isConnectToInternet(mContext)) {
                loadCases();
                loadFactories();
                loadEquipments();
                putWorkerIdsIntoCases();
                putTasksIntoWorkers();
                putCasesIntoVendors();
            } else {
                isFailCausedByInternet = true;
                cancel(true);
            }
        }

        return null;
    }

    @Override
    protected void onCancelled(Void aVoid) {
        super.onCancelled(aVoid);
        mOnFinishLoadingDataListener.onFailLoadingData(isFailCausedByInternet);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mOnFinishLoadingDataListener.onFinishLoadingData();
    }

    /**
     * Load all cases including all tasks in each case
     */
    private void loadCases() {
        LoadingDataUtils.loadCases(mContext);
        for (Case aCase : WorkingData.getInstance(mContext).getCases()) {
            LoadingDataUtils.loadTasksByCase(mContext, aCase.id);
        }
    }

    /**
     * Load all factories including all workers in each factory
     */
    private void loadFactories() {
        LoadingDataUtils.loadFactories(mContext);
        for (Factory factory : WorkingData.getInstance(mContext).getFactories()) {
            LoadingDataUtils.loadWorkersByFactory(mContext, factory.id);
        }
    }

    /**
     * Load all equipments
     */
    private void loadEquipments () {
        LoadingDataUtils.loadEquipments(mContext);
    }

    /**
     * Put WIP-task and scheduled-tasks into each worker
     */
    private void putTasksIntoWorkers() {
        for (Worker worker : WorkingData.getInstance(mContext).getWorkers()) {
            if (WorkingData.getInstance(mContext).hasTask(worker.wipTaskId)) {
                worker.setWipTask(WorkingData.getInstance(mContext).getTaskById(worker.wipTaskId));
            }

            // We need task ids to find the corresponding case data, so we don't use clearScheduleTasks() here.
            worker.getScheduledTasks().clear();

            for (String stId : worker.scheduledTaskIds) {
                if (WorkingData.getInstance(mContext).hasTask(stId)) {
                    worker.addScheduledTask(WorkingData.getInstance(mContext).getTaskById(stId));
                }
            }
        }
    }

    private void putWorkerIdsIntoCases() {
        for (Case c : WorkingData.getInstance(mContext).getCases()) {
            List<String> workerIdList = new ArrayList<>();

            for (Task task : c.tasks) {
                if (TextUtils.isEmpty(task.workerId) || workerIdList.contains(task.workerId)) continue;
                workerIdList.add(task.workerId);
            }

            c.involvedWorkerIds = workerIdList;
        }
    }

    private void putCasesIntoVendors() {
        for (Vendor vendor : WorkingData.getInstance(mContext).getVendors()) {
            // We need case ids to find the corresponding case data, so we don't use clearCases() here.
            vendor.getCases().clear();

            for (String caseId : vendor.caseIds) {
                if (WorkingData.getInstance(mContext).hasCase(caseId)) {
                    vendor.addCase(WorkingData.getInstance(mContext).getCaseById(caseId));
                }
            }
        }
    }

    public static void loadTimeCardsByWorker(Context context, String workerId) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        long start = cal.getTimeInMillis();
        cal.add(Calendar.WEEK_OF_YEAR, 1);
        long end = cal.getTimeInMillis();
        LoadingDataUtils.loadTimeCardsByWorker(context, workerId, start, end);
    }
}
