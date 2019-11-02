package com.example.simplereddit.fragment;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.simplereddit.R;
import com.example.simplereddit.adapter.TopicAdapter;
import com.example.simplereddit.model.Topic;
import com.example.simplereddit.utils.DeviceUtils;
import com.example.simplereddit.utils.NetworkUtils;
import com.example.simplereddit.utils.customDialog.ProgressDialog;
import com.example.simplereddit.utils.customDialog.SimpleDialog;
import com.example.simplereddit.utils.webService.Result;
import com.example.simplereddit.utils.webService.WebService;
import com.example.simplereddit.viewModel.TopicViewModel;
import com.google.android.material.button.MaterialButton;
import com.jakewharton.rxbinding3.view.RxView;

import org.json.JSONArray;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyRecentPostFragment extends Fragment {
    // view
    private CompositeDisposable compositeDisposable;
    private TextView tvTitle;
    private ImageButton ibBack;
    private SwipeRefreshLayout srlRefresh;
    private RecyclerView rvTopics;

    // data
    private ArrayList<Topic> topics = new ArrayList<>();
    private TopicAdapter topicAdapter;
    private TopicViewModel topicViewModel;

    // progress dialog
    private ProgressDialog progressDialog;

    public MyRecentPostFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // init view model
        topicViewModel = ViewModelProviders.of(getActivity()).get(TopicViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_recent_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        findView(view);
        getTopics();
        setListener();
    }

    private void findView(View view) {
        tvTitle = view.findViewById(R.id.tv_toolbar_title);
        ibBack = view.findViewById(R.id.ib_toolbar_left);
        srlRefresh = view.findViewById(R.id.srl_refresh);
        rvTopics = view.findViewById(R.id.rv_topics);

        // set page title
        tvTitle.setText(getResources().getString(R.string.txt_my_recent_post));

        // set toolbar icon
        ibBack.setBackgroundResource(R.drawable.ic_arrow_back);
        ibBack.setVisibility(View.VISIBLE);

        compositeDisposable = new CompositeDisposable();
        progressDialog = new ProgressDialog(getContext());
    }

    private void setListener() {
        // swipe refresh
        srlRefresh.setOnRefreshListener(() -> {
            srlRefresh.setRefreshing(true);
            getTopicsFromNetwork();
            srlRefresh.setRefreshing(false);
        });

        // back
        compositeDisposable.add(
                RxView.clicks(ibBack).subscribe(view -> {
                    try {
                        getActivity().getSupportFragmentManager().popBackStack();
                    } catch (NullPointerException e) {
                        Timber.e(e);
                    }
                })
        );
    }

    /*
     * API call to get all my topics
     */
    private void getTopics() {
        if (topicViewModel.getTopics().getValue() == null) {
            // get from network call
            getTopicsFromNetwork();
        } else {
            // load from view model
            topics.clear();
            topics.addAll(topicViewModel.getTopics().getValue());

            loadData(topics);
        }
    }

    private void getTopicsFromNetwork() {
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

    private Observable<ArrayList<Topic>> getTopicsObservable() {
        return Observable.fromCallable(() -> {
            WebService webService = new WebService();

            Result result = webService.get("/topics/" + DeviceUtils.getDeviceId(getContext()));

            topics.clear();

            if (result.isSuccess()) {
                JSONArray data = result.getDataInJSONArray();

                for (int i = 0; i < data.length(); i++) {
                    topics.add(new Topic(data.getJSONObject(i)));
                }
            } else {
                // will go to onError
                return null;
            }

            return topics;
        });
    }

    private DisposableObserver<ArrayList<Topic>> getTopicsObserver() {
        return new DisposableObserver<ArrayList<Topic>>() {
            @Override
            public void onNext(ArrayList<Topic> topics) {
                if (topics.isEmpty()) {
                    SimpleDialog noPostDialog = new SimpleDialog(getContext());
                    noPostDialog
                            .setIcon(R.drawable.ic_error)
                            .setMessage(getResources().getString(R.string.txt_nothing_posted))
                            .setAction(null, view -> {
                                try {
                                    getActivity().getSupportFragmentManager().popBackStack();
                                    noPostDialog.dismiss();
                                } catch (NullPointerException e) {
                                    noPostDialog.dismiss();
                                }
                            })
                            .show();

                    return;
                }

                // load data into rv
                loadData(topics);

                // save all the data into view model as cache
                topicViewModel.setTopics(topics);
            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e);
                progressDialog.dismiss();

                // show fail dialog
                showFailDialog();
            }

            @Override
            public void onComplete() {
                progressDialog.dismiss();
            }
        };
    }

    private void loadData(ArrayList<Topic> topics) {
        if (topicAdapter == null) {
            // load all topics into rv
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            rvTopics.setLayoutManager(linearLayoutManager);

            topicAdapter = new TopicAdapter(getContext(), topics, true);
            rvTopics.setAdapter(topicAdapter);
        }

        topicAdapter.notifyDataSetChanged();
    }

    private void showFailDialog() {
        new SimpleDialog(getContext())
                .setIcon(R.drawable.ic_error)
                .setMessage(getResources().getString(R.string.txt_request_failed))
                .show();
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
