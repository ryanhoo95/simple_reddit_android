package com.example.simplereddit.fragment;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.example.simplereddit.utils.webService.RequestParams;
import com.example.simplereddit.utils.webService.Result;
import com.example.simplereddit.utils.webService.WebService;
import com.example.simplereddit.viewModel.TopicViewModel;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.button.MaterialButton;
import com.jakewharton.rxbinding3.view.RxView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

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
public class HomeFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener {
    private static final String TAG = HomeFragment.class.getSimpleName();

    // view
    private CompositeDisposable compositeDisposable;
    private LinearLayout llRoot;
    private TextView tvTitle;
    private ImageButton ibMyTopic;
    private SwipeRefreshLayout srlRefresh;
    private AppBarLayout ablTopic;
    private EditText edtTopic;
    private MaterialButton btnSend;
    private RecyclerView rvTopics;

    // data
    private ArrayList<Topic> topics = new ArrayList<>();
    private TopicAdapter topicAdapter;
    private TopicViewModel topicViewModel;

    // progress dialog
    private ProgressDialog progressDialog;

    // vote
    private static final String ACTION_UPVOTE = "upvote";
    private static final String ACTION_DOWNVOTE = "downvote";

    public HomeFragment() {
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
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        findView(view);
        getTopics();
        setListener();
    }

    private void findView(View view) {
        llRoot = view.findViewById(R.id.ll_root);
        tvTitle = view.findViewById(R.id.tv_toolbar_title);
        ibMyTopic = view.findViewById(R.id.ib_toolbar_right);
        srlRefresh = view.findViewById(R.id.srl_refresh);
        ablTopic = view.findViewById(R.id.abl_topic);
        edtTopic = view.findViewById(R.id.edt_topic);
        btnSend = view.findViewById(R.id.btn_send);
        rvTopics = view.findViewById(R.id.rv_topics);

        // set page title
        tvTitle.setText(getResources().getString(R.string.txt_home));

        // set toolbar icon
        ibMyTopic.setBackgroundResource(R.drawable.ic_recent_post);
        ibMyTopic.setVisibility(View.VISIBLE);

        compositeDisposable = new CompositeDisposable();
        progressDialog = new ProgressDialog(getContext());
    }

    private void setListener() {
        DeviceUtils.setupUI(getContext(), llRoot);

        // hide keyboard when scrolling rv
        rvTopics.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                edtTopic.clearFocus();
            }
        });

        // swipe refresh
        srlRefresh.setOnRefreshListener(() -> {
            srlRefresh.setRefreshing(true);
            edtTopic.setText(null);
            edtTopic.setError(null);
            getTopics();
            srlRefresh.setRefreshing(false);
        });

        // abl
        ablTopic.addOnOffsetChangedListener(this);

        // my recent post
        compositeDisposable.add(
                RxView.clicks(ibMyTopic).subscribe(view -> {
                    try {
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                        fragmentManager.beginTransaction()
                                .add(R.id.fl_fragment, new MyRecentPostFragment())
                                .hide(HomeFragment.this)
                                .addToBackStack(TAG)
                                .commit();
                    } catch (NullPointerException e) {
                        Timber.e(e);
                    }
                })
        );

        // new topic
        compositeDisposable.add(
                RxView.clicks(btnSend).subscribe(view -> {
                    // reset the error
                    edtTopic.setError(null);

                    // get input
                    String input = edtTopic.getText().toString().trim();

                    // validate
                    if (TextUtils.isEmpty(input)) {
                        edtTopic.setError(getResources().getString(R.string.txt_field_required));
                        edtTopic.requestFocus();
                    } else {
                        addNewTopic(input);
                    }
                })
        );
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

    private Observable<ArrayList<Topic>> getTopicsObservable() {
        return Observable.fromCallable(() -> {
            WebService webService = new WebService();

            Result result = webService.get("/topics");

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
                if (topicAdapter == null) {
                    // load all topics into rv
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                    rvTopics.setLayoutManager(linearLayoutManager);

                    topicAdapter = new TopicAdapter(getContext(), topics, false);
                    rvTopics.setAdapter(topicAdapter);

                    // add vote listener
                    topicAdapter.addOnVotingListener(new TopicAdapter.OnVotingListener() {
                        @Override
                        public void onUpvoted(Topic topic, MaterialButton btnUpvote, int position) {
                            Timber.d("upvoting");
                            vote(topic.getId(), ACTION_UPVOTE, btnUpvote);
                        }

                        @Override
                        public void onDownvoted(Topic topic, MaterialButton btnDownvote, int position) {
                            Timber.d("downvoting");
                            vote(topic.getId(), ACTION_DOWNVOTE, btnDownvote);
                        }
                    });
                }

                topicAdapter.notifyDataSetChanged();

                // find thru the list whether contain current user topics, and then saved to cache
                topicViewModel.findCurrentUserTopicAndSaveCache(getContext(), topics);
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

    /*
     * API call to add new topic
     */
    private void addNewTopic(String topic) {
        if (NetworkUtils.isConnectionAvailable(getContext(), true)) {
            compositeDisposable.add(
                    addNewTopicObservable(topic)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe(disposable -> {
                                progressDialog.show();
                            })
                            .subscribeWith(addNewTopicObserver())
            );
        }
    }

    private Observable<Topic> addNewTopicObservable(String topic) {
        return Observable.fromCallable(() -> {
            WebService webService = new WebService();

            RequestParams requestParams = new RequestParams();
            requestParams.put("topic", topic);
            requestParams.put("deviceId", DeviceUtils.getDeviceId(getContext()));

            Result result = webService.post("/topics", requestParams);
            if (result.isSuccess()) {
                return new Topic(result.getDataInJSONObject());
            } else {
                // will go to onError
                return null;
            }
        });
    }

    private DisposableObserver<Topic> addNewTopicObserver() {
        return new DisposableObserver<Topic>() {
            @Override
            public void onNext(Topic topic) {
                // clear input
                edtTopic.setText(null);

                // add this new topic to the list if the list currently is less than 20
                if (topics.size() < 20 && topicAdapter != null) {
                    topics.add(topic);
                    topicAdapter.notifyDataSetChanged();
                }

                // update view model data
                topicViewModel.updateTopic(topic);
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

    /*
     * API call to vote
     */
    private void vote(String topicId, String action, MaterialButton btnVote) {
        if (NetworkUtils.isConnectionAvailable(getContext(), true)) {
            compositeDisposable.add(
                    voteObservable(topicId, action)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe(disposable -> {
                                progressDialog.show();
                            })
                            .subscribeWith(voteObserver(action, btnVote))
            );
        }
    }

    private Observable<Topic> voteObservable(String topicId, String action) {
        return Observable.fromCallable(() -> {
            WebService webService = new WebService();

            Result result = webService.put("/topics/" + topicId + "/" + action, new RequestParams());

            if (result.isSuccess()) {
                return new Topic(result.getDataInJSONObject());
            } else {
                // will go to onError
                return null;
            }
        });
    }

    private DisposableObserver<Topic> voteObserver(String action, MaterialButton btnVote) {
        return new DisposableObserver<Topic>() {
            @Override
            public void onNext(Topic topic) {
                // update the vote count
                if (action.equals(ACTION_UPVOTE)) {
                    btnVote.setText(topic.getUpVote());
                } else {
                    btnVote.setText(topic.getDownVote());
                }

                // update view model data, only if the voting topic is posted by current user
                if (topic.getDeviceId().equals(DeviceUtils.getDeviceId(getContext()))) {
                    topicViewModel.updateTopic(topic);
                }
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

    private void showFailDialog() {
        new SimpleDialog(getContext())
                .setIcon(R.drawable.ic_error)
                .setMessage(getResources().getString(R.string.txt_request_failed))
                .show();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        // only enable swipe refresh when the view is at the top
        srlRefresh.setEnabled(i == 0);
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
