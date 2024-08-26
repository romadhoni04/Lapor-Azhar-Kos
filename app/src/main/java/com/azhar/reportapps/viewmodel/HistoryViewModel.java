package com.azhar.reportapps.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.azhar.reportapps.dao.DatabaseDao;
import com.azhar.reportapps.database.DatabaseClient;
import com.azhar.reportapps.model.ModelDatabase;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class HistoryViewModel extends AndroidViewModel {

    LiveData<List<ModelDatabase>> modelLaundry;
    DatabaseDao databaseDao;

    public HistoryViewModel(@NonNull Application application) {
        super(application);

        databaseDao = DatabaseClient.getInstance(application).getAppDatabase().databaseDao();
        modelLaundry = databaseDao.getAllReport();
    }

    public LiveData<List<ModelDatabase>> getDataLaporan() {
        return modelLaundry;
    }

    public void deleteDataById(final int uid) {
        Completable.fromAction(() -> databaseDao.deleteSingleReport(uid))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

}
