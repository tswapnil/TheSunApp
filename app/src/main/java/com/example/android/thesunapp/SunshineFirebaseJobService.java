package com.example.android.thesunapp;

import android.content.Context;
import android.os.AsyncTask;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * Created by Swapnil on 26-12-2016.
 */

public class SunshineFirebaseJobService extends JobService {
    /**
     * The entry point to your Job. Implementations should offload work to another thread of
     * execution as soon as possible.
     *
     * @param job
     * @return whether there is more work remaining.
     */
    private AsyncTask mAsyncTask;
    private Context mContext;
    @Override
    public boolean onStartJob(final JobParameters job) {
        mAsyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                mContext = getApplicationContext();
                SunshineSyncTask.syncWeather(mContext);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                jobFinished(job,false);
            }
        }.execute();
        return true;
    }

    /**
     * Called when the scheduling engine has decided to interrupt the execution of a running job,
     * most likely because the runtime constraints associated with the job are no longer satisfied.
     *
     * @param job
     * @return whether the job should be retried
     * see Builder#setRetryStrategy(RetryStrategy)
     * see RetryStrategy
     */
    @Override
    public boolean onStopJob(JobParameters job) {
        if(mAsyncTask!=null){
            mAsyncTask.cancel(true);
        }
        return true;
    }
}
