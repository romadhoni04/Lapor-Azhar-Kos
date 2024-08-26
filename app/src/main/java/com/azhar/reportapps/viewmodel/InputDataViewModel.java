package com.azhar.reportapps.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.azhar.reportapps.dao.DatabaseDao;
import com.azhar.reportapps.database.DatabaseClient;
import com.azhar.reportapps.model.ModelDatabase;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.azhar.reportapps.database.DatabaseClient;
import com.azhar.reportapps.model.ModelDatabase;

public class InputDataViewModel extends AndroidViewModel {

    private DatabaseDao databaseDao;

    public InputDataViewModel(@NonNull Application application) {
        super(application);
        databaseDao = DatabaseClient.getInstance(application).getAppDatabase().databaseDao();
    }

    public void insertData(ModelDatabase modelDatabase) {
        Completable.fromAction(() -> databaseDao.insertData(modelDatabase))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    public void addLaporan(final String kategori, final String image,
                           final String nama, final String lokasi, final String tanggal,
                           final String isi_laporan, final String telepon) {
        Completable.fromAction(() -> {
                    ModelDatabase modelDatabase = new ModelDatabase();
                    modelDatabase.kategori = kategori;
                    modelDatabase.image = image;
                    modelDatabase.nama = nama;
                    modelDatabase.lokasi = lokasi;
                    modelDatabase.tanggal = tanggal;
                    modelDatabase.isi_laporan = isi_laporan;
                    modelDatabase.telepon = telepon;
                    databaseDao.insertData(modelDatabase);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }
}

