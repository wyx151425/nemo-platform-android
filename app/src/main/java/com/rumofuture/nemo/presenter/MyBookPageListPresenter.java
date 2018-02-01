package com.rumofuture.nemo.presenter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.rumofuture.nemo.R;
import com.rumofuture.nemo.app.contract.MyBookPageListContract;
import com.rumofuture.nemo.app.manager.ImageChooseManager;
import com.rumofuture.nemo.model.entity.Book;
import com.rumofuture.nemo.model.entity.Page;
import com.rumofuture.nemo.model.schema.BookSchema;
import com.rumofuture.nemo.model.source.BookDataSource;
import com.rumofuture.nemo.model.source.PageDataSource;
import com.smile.filechoose.api.ChosenImage;

import java.io.File;
import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;

/**
 * Created by WangZhenqi on 2017/4/15.
 */

public class MyBookPageListPresenter implements MyBookPageListContract.Presenter, PageDataSource.PageListGetCallback, PageDataSource.TotalGetCallback,
        PageDataSource.PageSaveCallback, PageDataSource.PageDeleteCallback, PageDataSource.PageUpdateCallback, BookDataSource.BookUpdateCallback {

    private static final int NO_REQUEST_CODE = 0;
    private static final int UPLOAD_PAGE_REQUEST_CODE = 1;
    private static final int UPDATE_PAGE_REQUEST_CODE = 2;

    private int requestCode = NO_REQUEST_CODE;

    private MyBookPageListContract.View mView;
    private BookDataSource mBookRepository;
    private PageDataSource mPageRepository;
    private ImageChooseManager mImageChooseManager;
    private Book mBook;
    private Page mPage;

    private ChosenImage mPageImage;

    public MyBookPageListPresenter(
            @NonNull MyBookPageListContract.View view,
            @NonNull BookDataSource bookRepository,
            @NonNull PageDataSource pageRepository
    ) {
        mView = view;
        mBookRepository = bookRepository;
        mPageRepository = pageRepository;
    }

    @Override
    public void start() {
        requestCode = NO_REQUEST_CODE;
        mPage = null;
        mPageImage = null;
        mImageChooseManager = new ImageChooseManager((Fragment) mView, this);  // 构造成功默认创建一个图片选择器
    }

    /**
     * 图片选择成功后 最后一个处理方法
     * @param chosenImage 被图片选择器封装的被选择的图片
     */
    @Override
    public void setChosenImage(ChosenImage chosenImage) {
        mPageImage = chosenImage;
        // 由于处理过程在异步线程中进行，所以需要调用主线程进行后续操作
        ((Fragment) mView).getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 图片选择成功后，根据请求码进行判断，如果请求码为1，则将进行的是新漫画分页保存
                // 如果请求码为2，则将进行的是漫画分页更新操作
                if (mView.isActive()) {
                    final BmobFile newImage = new BmobFile(new File(mPageImage.getFilePathOriginal()));
                    if (UPLOAD_PAGE_REQUEST_CODE == requestCode) {
                        mView.showProgressBar(true, R.string.prompt_uploading);
                        mPage.setImage(newImage);
                        mPageRepository.savePage(mPage, MyBookPageListPresenter.this);
                    } else if (UPDATE_PAGE_REQUEST_CODE == requestCode) {
                        mView.showProgressBar(true, R.string.prompt_updating);
                        mPageRepository.updatePage(mPage, newImage, MyBookPageListPresenter.this);
                    }
                }
            }
        });
    }

    @Override
    public void releaseImageChooseManager() {

    }

    /**
     * 图片选择方法，view发出图片选择请求时将调用此方法
     */
    @Override
    public void chooseImage() {
        // 获取选择到的图片
        mImageChooseManager.chooseImage();
    }

    @Override
    public void submitChoice(int requestCode, Intent data) {
        // 图片选择的中间操作
        mImageChooseManager.submitChoice(requestCode, data);
    }

    @Override
    public void uploadPage(Page page) {
        start();
        requestCode = UPLOAD_PAGE_REQUEST_CODE;
        mPage = page;
        chooseImage();
    }

    @Override
    public void deletePage(Page page) {
        mView.showProgressBar(true, R.string.prompt_deleting);
        mPageRepository.deletePage(page, this);
    }

    @Override
    public void updatePage(Page page) {
        start();
        requestCode = UPDATE_PAGE_REQUEST_CODE;
        mPage = page;
        chooseImage();
    }

    @Override
    public void getBookPageList(Book book, int pageCode) {
        mBook = book;
        mPageRepository.getPageListByBook(mBook, pageCode, this);
        mPageRepository.getPageTotalNumber(mBook, this);
    }

    @Override
    public void onPageSaveSuccess(Page page) {
        if (mView.isActive()) {
            mView.showProgressBar(false, 0);
            mView.showPageSaveSuccess(page);
        }

        mBook.increment(BookSchema.Table.Cols.PAGE_TOTAL);
        mBookRepository.updateBook(mBook, null, this);
    }

    @Override
    public void onPageSaveFailed(BmobException e) {
        if (mView.isActive()) {
            mView.showProgressBar(false, 0);
            mView.showPageSaveFailed(e);
        }
    }

    @Override
    public void onPageDeleteSuccess(Page page) {
        if (mView.isActive()) {
            mView.showProgressBar(false, 0);
            mView.showPageDeleteSuccess(page);
        }

        mBook.increment(BookSchema.Table.Cols.PAGE_TOTAL, -1);
        mBookRepository.updateBook(mBook, null, this);
    }

    @Override
    public void onPageDeleteFailed(BmobException e) {
        if (mView.isActive()) {
            mView.showProgressBar(false, 0);
            mView.showPageDeleteFailed(e);
        }
    }

    @Override
    public void onPageUpdateSuccess(Page page) {
        if (mView.isActive()) {
            mView.showProgressBar(false, 0);
            mView.showPageUpdateSuccess(page);
        }
    }

    @Override
    public void onPageUpdateFailed(BmobException e) {
        if (mView.isActive()) {
            mView.showProgressBar(false, 0);
            mView.showPageUpdateFailed(e);
        }
    }

    @Override
    public void onPageListGetSuccess(List<Page> pageList) {
        if (mView.isActive()) {
            mView.showPageListGetSuccess(pageList);
        }

        mBook.setPage(pageList.size());
        mBookRepository.updateBook(mBook, null, this);
    }

    @Override
    public void onPageListGetFailed(BmobException e) {
        if (mView.isActive()) {
            mView.showPageListGetFailed(e);
        }
    }

    @Override
    public void onBookUpdateSuccess(Book book) {

    }

    @Override
    public void onBookUpdateFailed(BmobException e) {

    }

    @Override
    public void onTotalGetSuccess(Integer total) {
        mBook.setPage(total);
        mBookRepository.updateBook(mBook, null, this);
    }

    @Override
    public void onTotalGetFailed(BmobException e) {

    }
}
