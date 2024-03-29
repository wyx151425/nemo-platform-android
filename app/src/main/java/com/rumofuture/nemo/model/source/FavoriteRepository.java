package com.rumofuture.nemo.model.source;

import android.support.annotation.NonNull;

import com.rumofuture.nemo.app.NemoCallback;
import com.rumofuture.nemo.model.entity.Favorite;

/**
 * Created by WangZhenqi on 2017/9/12.
 */

public class FavoriteRepository implements FavoriteDataSource {

    private static FavoriteRepository sInstance;
    private final FavoriteDataSource mFavoriteLocalDataSource;
    private final FavoriteDataSource mFavoriteRemoteDataSource;

    public static FavoriteRepository getInstance(
            @NonNull FavoriteDataSource favoriteLocalDataSource,
            @NonNull FavoriteDataSource favoriteRemoteDataSource
    ) {
        if (null == sInstance) {
            sInstance = new FavoriteRepository(favoriteLocalDataSource, favoriteRemoteDataSource);
        }
        return sInstance;
    }

    private FavoriteRepository(
            @NonNull FavoriteDataSource favoriteLocalDataSource,
            @NonNull FavoriteDataSource favoriteRemoteDataSource
    ) {
        mFavoriteLocalDataSource = favoriteLocalDataSource;
        mFavoriteRemoteDataSource = favoriteRemoteDataSource;
    }

    @Override
    public void saveFavorite(Favorite favorite, NemoCallback<Favorite> callback) {
        mFavoriteRemoteDataSource.saveFavorite(favorite, callback);
    }

    @Override
    public void deleteFavorite(Favorite favorite, NemoCallback<Favorite> callback) {
        mFavoriteRemoteDataSource.deleteFavorite(favorite, callback);
    }

    @Override
    public void getFavorite(Favorite favorite, NemoCallback<Favorite> callback) {
        mFavoriteRemoteDataSource.getFavorite(favorite, callback);
    }
}
