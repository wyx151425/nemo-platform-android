package com.rumofuture.nemo.model.source.remote;

import com.rumofuture.nemo.app.NemoCallback;
import com.rumofuture.nemo.model.entity.Favorite;
import com.rumofuture.nemo.model.schema.FavoriteSchema;
import com.rumofuture.nemo.model.source.FavoriteDataSource;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by WangZhenqi on 2017/9/12.
 */

public class FavoriteRemoteDataSource implements FavoriteDataSource {

    private static final FavoriteRemoteDataSource sInstance = new FavoriteRemoteDataSource();

    public static FavoriteRemoteDataSource getInstance() {
        return sInstance;
    }

    private FavoriteRemoteDataSource() {
    }

    @Override
    public void saveFavorite(final Favorite favorite, final NemoCallback<Favorite> callback) {
        favorite.save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                if (null == e) {
                    favorite.setObjectId(objectId);
                    callback.onSuccess(favorite);
                } else {
                    callback.onFailed(e.getMessage());
                }
            }
        });
    }

    @Override
    public void deleteFavorite(final Favorite favorite, final NemoCallback<Favorite> callback) {
        favorite.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (null == e) {
                    callback.onSuccess(favorite);
                } else {
                    callback.onFailed(e.getMessage());
                }
            }
        });
    }

    @Override
    public void getFavorite(Favorite favorite, final NemoCallback<Favorite> callback) {
        BmobQuery<Favorite> query = new BmobQuery<>();
        query.addWhereEqualTo(FavoriteSchema.Table.Cols.BOOK, favorite.getBook());
        query.addWhereEqualTo(FavoriteSchema.Table.Cols.FAVOR, favorite.getFavor());
        query.include(FavoriteSchema.Table.Cols.BOOK);
        query.include(FavoriteSchema.Table.Cols.FAVOR);
        query.findObjects(new FindListener<Favorite>() {
            @Override
            public void done(List<Favorite> favoriteList, BmobException e) {
                if (null == e) {
                    callback.onSuccess(favoriteList.get(0));
                } else {
                    callback.onFailed(e.getMessage());
                }
            }
        });
    }
}
