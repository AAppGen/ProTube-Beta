package io.awesome.gagtube.fragments.comments;

import static io.awesome.gagtube.ktx.ViewUtils.animate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.schabi.newpipe.extractor.ListExtractor;
import org.schabi.newpipe.extractor.comments.CommentsInfo;

import io.awesome.gagtube.R;
import io.awesome.gagtube.fragments.list.BaseListInfoFragment;
import io.awesome.gagtube.util.ExtractorHelper;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;

public class CommentsFragment extends BaseListInfoFragment<CommentsInfo> {
    private final CompositeDisposable disposables = new CompositeDisposable();

    private TextView emptyMessage;
    private ImageView closeComment;
    private ImageView back;

    public static CommentsFragment getInstance(final int serviceId, final String url,
                                               final String name) {
        final CommentsFragment instance = new CommentsFragment();
        instance.setInitialData(serviceId, url, name);
        return instance;
    }

    @Override
    protected void initViews(final View rootView, final Bundle savedInstanceState) {
        super.initViews(rootView, savedInstanceState);

        emptyMessage = rootView.findViewById(R.id.empty_message);
        closeComment = rootView.findViewById(R.id.close_comments);
        back = rootView.findViewById(R.id.back);

        closeComment.setOnClickListener(v -> {
            animate(activity.findViewById(R.id.comments_container), false, 100);
        });

        back.setOnClickListener(v -> {
            animate(activity.findViewById(R.id.comments_container), false, 100);
        });
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_comments, container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }

    @Override
    protected Single<ListExtractor.InfoItemsPage> loadMoreItemsLogic() {
        return ExtractorHelper.getMoreCommentItems(serviceId, currentInfo, currentNextPage);
    }

    @Override
    protected Single<CommentsInfo> loadResult(final boolean forceLoad) {
        return ExtractorHelper.getCommentsInfo(serviceId, url, forceLoad);
    }

    @Override
    public void handleResult(@NonNull final CommentsInfo result) {
        super.handleResult(result);
        emptyMessage.setText(result.isCommentsDisabled() ? R.string.comments_are_disabled : R.string.no_comments);
        disposables.clear();
    }
}
