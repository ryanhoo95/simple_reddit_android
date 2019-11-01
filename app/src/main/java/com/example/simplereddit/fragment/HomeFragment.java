package com.example.simplereddit.fragment;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.simplereddit.R;
import com.example.simplereddit.utils.NetworkUtils;
import com.example.simplereddit.utils.customDialog.ProgressDialog;
import com.example.simplereddit.utils.customDialog.SimpleDialog;
import com.example.simplereddit.utils.webService.RequestParams;
import com.example.simplereddit.utils.webService.Result;
import com.example.simplereddit.utils.webService.WebService;

import org.json.JSONArray;
import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    // view
    private CompositeDisposable compositeDisposable;
    private TextView tvTitle;

    // progress dialog
    private ProgressDialog progressDialog;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        findView(view);
        getTopics();
    }

    private void findView(View view) {
        tvTitle = view.findViewById(R.id.tv_toolbar_title);
        tvTitle.setText("hello");

        compositeDisposable = new CompositeDisposable();
        progressDialog = new ProgressDialog(getContext());
    }

    /*
     * API call to get all topics
     */
    private void getTopics() {
        if (NetworkUtils.isConnectionAvailable(getContext(), true)) {
            compositeDisposable.add(
                    getTopicsObservable()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe(disposable -> {
                                progressDialog.show();
                            })
                            .subscribeWith(getTopicsObserver())
            );
        }
    }

    private Observable<JSONArray> getTopicsObservable() {
        return Observable.fromCallable(() -> {
            WebService webService = new WebService();

            return webService.get("/topics").getDataInJSONArray();
        });
    }

    private DisposableObserver<JSONArray> getTopicsObserver() {
        return new DisposableObserver<JSONArray>() {
            @Override
            public void onNext(JSONArray jsonArray) {
                Timber.d(jsonArray.toString());
            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e);
                progressDialog.dismiss();
            }

            @Override
            public void onComplete() {
                progressDialog.dismiss();
            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Timber.d("on destroy");

        // dispose all disposable
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }

        // dismiss progress dialog
        progressDialog.dismiss();
    }
}
