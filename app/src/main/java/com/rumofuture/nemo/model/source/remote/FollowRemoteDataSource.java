package com.rumofuture.nemo.model.source.remote;

import com.rumofuture.nemo.app.NemoCallback;
import com.rumofuture.nemo.model.entity.Follow;
import com.rumofuture.nemo.model.schema.FollowSchema;
import com.rumofuture.nemo.model.source.FollowDataSource;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by WangZhenqi on 2017/9/12.
 */

public class FollowRemoteDataSource implements FollowDataSource {

    private static final FollowRemoteDataSource sInstance = new FollowRemoteDataSource();

    public static FollowRemoteDataSource getInstance() {
        return sInstance;
    }

    private FollowRemoteDataSource() {

    }

    @Override
    public void saveFollow(final Follow follow, final NemoCallback<Follow> callback) {
        follow.save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                if (null == e) {
                    follow.setObjectId(objectId);
                    callback.onSuccess(follow);
                } else {
                    callback.onFailed(e.getMessage());
                }
            }
        });
    }

    @Override
    public void deleteFollow(final Follow follow, final NemoCallback<Follow> callback) {
        follow.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (null == e) {
                    callback.onSuccess(follow);
                } else {
                    callback.onFailed(e.getMessage());
                }
            }
        });
    }

    @Override
    public void getFollow(Follow follow, final NemoCallback<Follow> callback) {
        BmobQuery<Follow> query = new BmobQuery<>();
        query.addWhereEqualTo(FollowSchema.Table.Cols.AUTHOR, follow.getAuthor());
        query.addWhereEqualTo(FollowSchema.Table.Cols.FOLLOWER, follow.getFollower());
        query.include(FollowSchema.Table.Cols.AUTHOR);
        query.include(FollowSchema.Table.Cols.FOLLOWER);
        query.findObjects(new FindListener<Follow>() {
            @Override
            public void done(List<Follow> followList, BmobException e) {
                if (null == e) {
                    callback.onSuccess(followList.get(0));
                } else {
                    callback.onFailed(e.getMessage());
                }
            }
        });
    }
}
