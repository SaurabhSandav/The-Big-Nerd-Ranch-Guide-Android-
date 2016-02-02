package com.bignerdranch.android.photogallery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.bignerdranch.android.photogallery.model.Photo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saurabh on 31/1/16.
 */
public class PhotoGalleryFragment extends VisibleFragment {

    private static final String TAG = "PhotoGalleryFragment";
    private static final int ITEM_WIDTH = 360;

    private RecyclerView photoRecyclerView;
    private GridLayoutManager layoutManager;
    private PhotoAdapter photoAdapter;
    private ThumbnailDownloader<PhotoHolder> thumbnailDownloader;
    private List<Photo> items = new ArrayList<>();
    private int page = 1;

    public static PhotoGalleryFragment newInstance() {
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        updateItems();

        Handler responseHandler = new Handler();
        thumbnailDownloader = new ThumbnailDownloader<>(responseHandler);
        thumbnailDownloader.setThumbnailDownloaderListener(new ThumbnailDownloader.ThumbnailDownloadListener<PhotoHolder>() {
            @Override
            public void onThumbnailDownloaded(PhotoHolder target, Bitmap thumbnail, String url) {
                Drawable drawable = new BitmapDrawable(getResources(), thumbnail);
                target.bindDrawable(drawable);
            }
        });
        thumbnailDownloader.start();
        thumbnailDownloader.getLooper();
        //Log.i(TAG, "Background thread started");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);

        layoutManager = new GridLayoutManager(getActivity(), 3);

        photoRecyclerView = (RecyclerView) v.findViewById(R.id.fragment_photo_gallery_recycler_view);
        photoRecyclerView.setLayoutManager(layoutManager);
        photoRecyclerView.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        int grid_size = photoRecyclerView.getWidth() / ITEM_WIDTH;
                        layoutManager.setSpanCount(grid_size);
                    }
                });
        photoRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (layoutManager.findLastVisibleItemPosition() == (layoutManager.getItemCount() - 1) && newState == RecyclerView.SCROLL_STATE_IDLE)
                    new FetchItemsTask(null).execute(++page);
            }
        });

        setupAdapter();

        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        thumbnailDownloader.quit();
        //Log.i(TAG, "Background thread destroyed");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        thumbnailDownloader.clearQueue();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_photo_gallery, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Log.d(TAG, "QueryTextSubmit: " + query);
                QueryPreferences.setStoredQuery(getActivity(), query);
                searchView.onActionViewCollapsed();
                photoRecyclerView.setAdapter(null);
                page = 1;
                updateItems();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Log.d(TAG, "QueryTextChange: " + newText);
                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = QueryPreferences.getStoredQuery(getActivity());
                searchView.setQuery(query, false);
            }
        });

        MenuItem toggleItem = menu.findItem(R.id.menu_item_toggle_polling);
        if (PollService.isServiceAlarmOn(getActivity()))
            toggleItem.setTitle(getString(R.string.stop_polling));
        else
            toggleItem.setTitle(getString(R.string.start_polling));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_clear:
                QueryPreferences.setStoredQuery(getActivity(), null);
                updateItems();
                return true;
            case R.id.menu_item_toggle_polling:
                boolean shouldStartAlarm = !PollService.isServiceAlarmOn(getActivity());
                PollService.setServiceAlarm(getActivity(), shouldStartAlarm);
                getActivity().invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateItems() {
        String query = QueryPreferences.getStoredQuery(getActivity());
        new FetchItemsTask(query).execute();
    }

    private void setupAdapter() {
        photoAdapter = new PhotoAdapter(items);
        if (isAdded())
            photoRecyclerView.setAdapter(photoAdapter);
    }

    private void updateAdapter(List<Photo> newItems) {
        photoAdapter.appendToList(newItems);
    }

    private class PhotoHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView itemImageView;
        private Photo item;

        public PhotoHolder(View itemView) {
            super(itemView);
            itemImageView = (ImageView) itemView.findViewById(R.id.fragment_photo_gallery_image_view);
            itemView.setOnClickListener(this);
        }

        public void bindDrawable(Drawable drawable) {
            itemImageView.setImageDrawable(drawable);
        }

        public void bindPhotoItem(Photo item){
            this.item = item;
        }

        @Override
        public void onClick(View v) {
            Intent i = PhotoPageActivity.newIntent(getActivity(), item.getPhotoPageUri());
            startActivity(i);
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {

        private List<Photo> galleryItems;

        public PhotoAdapter(List<Photo> galleryItems) {
            this.galleryItems = galleryItems;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.gallery_item, parent, false);
            return new PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {
            Photo galleryItem = galleryItems.get(position);
            holder.bindPhotoItem(galleryItem);
            Drawable placeholder;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1)
                placeholder = getResources().getDrawable(R.drawable.bill_up_close, getActivity().getTheme());
            else
                placeholder = getResources().getDrawable(R.drawable.bill_up_close);

            holder.bindDrawable(placeholder);
            thumbnailDownloader.queueThumbnail(holder, galleryItem.getUrl_s());
        }

        @Override
        public int getItemCount() {
            return galleryItems.size();
        }

        public void appendToList(List<Photo> newGalleryItems) {
            int newPosition = galleryItems.size();
            galleryItems.addAll(newGalleryItems);
            notifyItemRangeInserted(newPosition, newGalleryItems.size());
        }
    }

    private class FetchItemsTask extends AsyncTask<Integer, Void, List<Photo>> {

        private String query;

        public FetchItemsTask(String query) {
            this.query = query;
        }

        @Override
        protected List<Photo> doInBackground(Integer... params) {

            if (query == null) {
                return new FlickrFetchr().getRecentPhotos(String.valueOf(page));
            } else {
                return new FlickrFetchr().searchPhotos(String.valueOf(page), query);
            }
        }

        @Override
        protected void onPostExecute(List<Photo> galleryItems) {
            if (page == 1) {
                items = galleryItems;
                setupAdapter();
            } else
                updateAdapter(galleryItems);
        }
    }
}
