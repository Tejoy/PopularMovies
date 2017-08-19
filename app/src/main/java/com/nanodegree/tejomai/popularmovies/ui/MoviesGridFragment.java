package com.nanodegree.tejomai.popularmovies.ui;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.nanodegree.tejomai.popularmovies.PopularMoviesUtil;
import com.nanodegree.tejomai.popularmovies.R;
import com.nanodegree.tejomai.popularmovies.adapters.RVMovieThumbnailsAdapter;
import com.nanodegree.tejomai.popularmovies.db.FavoritesContentProvider;
import com.nanodegree.tejomai.popularmovies.db.FavoritesTable;
import com.nanodegree.tejomai.popularmovies.interfaces.DataDownloadComplete;
import com.nanodegree.tejomai.popularmovies.models.MovieGridItem;
import com.nanodegree.tejomai.popularmovies.tasks.MovieDataFetcherAsyncTask;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.nanodegree.tejomai.popularmovies.db.FavoritesTable.TABLE_NAME_COLUMN_ID;
import static com.nanodegree.tejomai.popularmovies.db.FavoritesTable.TABLE_NAME_COLUMN_OVERVIEW;
import static com.nanodegree.tejomai.popularmovies.db.FavoritesTable.TABLE_NAME_COLUMN_RELEASE;
import static com.nanodegree.tejomai.popularmovies.db.FavoritesTable.TABLE_NAME_COLUMN_TITLE;
import static com.nanodegree.tejomai.popularmovies.db.FavoritesTable.TABLE_NAME_COLUMN_URL;
import static com.nanodegree.tejomai.popularmovies.db.FavoritesTable.TABLE_NAME_COLUMN_VOTE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MoviesGridFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MoviesGridFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoviesGridFragment extends Fragment implements DataDownloadComplete, LoaderManager.LoaderCallbacks<CursorLoader> {


    private final String PARAM_LANGUAGE = "en-US";
    private final String key_instance_save = "moviesList";

    private RVMovieThumbnailsAdapter rvThumbnailsAdapter = null;
    private List<MovieGridItem> gridItems = null;
    private OnFragmentInteractionListener activityCallback;
    private int POTRAIT_COL_COUNT = 2;
    private int LANSCAPE_COL_COUNT = 4;
    private ProgressDialog progressDialog;

    public MoviesGridFragment() {
    }

    public static MoviesGridFragment newInstance(String param1, String param2) {
        MoviesGridFragment fragment = new MoviesGridFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            gridItems = (List<MovieGridItem>) savedInstanceState.getSerializable(key_instance_save);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(key_instance_save, (Serializable) gridItems);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View frame = inflater.inflate(R.layout.fragment_movies_recyclerview, container, false);
        RecyclerView rvMoviesGrid = (RecyclerView) frame.findViewById(R.id.rvMoviesGrid);
        rvThumbnailsAdapter = new RVMovieThumbnailsAdapter(getActivity(), gridItems);
        rvMoviesGrid.setAdapter(rvThumbnailsAdapter);
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            rvMoviesGrid.setLayoutManager(new GridLayoutManager(getActivity(), POTRAIT_COL_COUNT));
        } else {
            rvMoviesGrid.setLayoutManager(new GridLayoutManager(getActivity(), LANSCAPE_COL_COUNT));
        }

        activityCallback = (OnFragmentInteractionListener) getActivity();
        SharedPreferences preferences = getActivity().getSharedPreferences(PopularMoviesUtil.PREF_NAME, Context.MODE_PRIVATE);
        if (gridItems == null) {
            fetchDataByFilter(preferences.getString(PopularMoviesUtil.PREF_FILTER, PopularMoviesUtil.PREF_FILTER_DEFAULT));
        }

        return frame;
    }

    private void fetchDataAndUpdateGrid(String type) {
        showProgressDialog();
        //start task to fetch image thumbnails
        if (!PopularMoviesUtil.isNetworkAvailable(getActivity())) {
            onFailure(null);
            return;
        }
        String url_key = type.equals(PopularMoviesUtil.PREF_FILTER_POPULARITY)? PopularMoviesUtil.URL_KEY_POPULAR : PopularMoviesUtil.URL_KEY_TOP_RATED;
        MovieDataFetcherAsyncTask fetchTask = new MovieDataFetcherAsyncTask();
        fetchTask.setDataDownloadCompete(this);
        fetchTask.execute(type, PopularMoviesUtil.PARAM_API_KEY, PARAM_LANGUAGE, url_key);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_grid_fragment, menu);
        menu.getItem(0).setCheckable(true);
        menu.getItem(1).setCheckable(true);
        menu.getItem(2).setCheckable(true);
        menu.getItem(0).setEnabled(true);
        menu.getItem(1).setEnabled(true);
        menu.getItem(2).setEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        SharedPreferences prefs = getActivity().getSharedPreferences(PopularMoviesUtil.PREF_NAME, Context.MODE_PRIVATE);
        //fetch data based on menu selection
        if (id == R.id.action_popularity) {
            prefs.edit().putString(PopularMoviesUtil.PREF_FILTER, PopularMoviesUtil.PREF_FILTER_POPULARITY).commit();
            fetchDataByFilter(PopularMoviesUtil.PREF_FILTER_POPULARITY);
            return true;
        } else if (id == R.id.action_top_rated) {
            prefs.edit().putString(PopularMoviesUtil.PREF_FILTER, PopularMoviesUtil.PREF_FILTER_TOP_RATING).commit();
            fetchDataByFilter(PopularMoviesUtil.PREF_FILTER_TOP_RATING);
            return true;
        } else if (id == R.id.action_refresh) {
            fetchDataByFilter(prefs.getString(PopularMoviesUtil.PREF_FILTER, PopularMoviesUtil.PREF_FILTER_DEFAULT));
            return true;
        } else if (id == R.id.action_favourites) {
            prefs.edit().putString(PopularMoviesUtil.PREF_FILTER, PopularMoviesUtil.PREF_FILTER_FAVOURITE).commit();
            fetchDataByFilter(PopularMoviesUtil.PREF_FILTER_FAVOURITE);
            return true;
        }
        return false;
    }

    private void fetchDataByFilter(String type){
        if(type.equals(PopularMoviesUtil.PREF_FILTER_FAVOURITE)){
            fetchFavoritesData();
        }else{
            fetchDataAndUpdateGrid(type);
        }
    }

    private void fetchFavoritesData(){
        String[] projection = FavoritesTable.PROJECTION;
        Uri uri = FavoritesContentProvider.CONTENT_URI;
        Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null,
                null);
        gridItems = collectItems(cursor);
        updateGridItems();

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        SharedPreferences prefs = getActivity().getSharedPreferences(PopularMoviesUtil.PREF_NAME, Context.MODE_PRIVATE);
        String value = prefs.getString(PopularMoviesUtil.PREF_FILTER, PopularMoviesUtil.PREF_FILTER_DEFAULT);
        int menuID = R.id.action_popularity;
        if (value.equals(PopularMoviesUtil.PREF_FILTER_TOP_RATING)) {
            menuID = R.id.action_top_rated;
        } else if (value.equals(PopularMoviesUtil.PREF_FILTER_FAVOURITE)) {
            menuID = R.id.action_favourites;
        }
        //set the menu checked status based on the selection made
        if (menu.getItem(0).getItemId() == menuID) {
            menu.getItem(0).setChecked(true);
            menu.getItem(1).setChecked(false);
            menu.getItem(2).setChecked(false);
        } else if (menu.getItem(1).getItemId() == menuID) {
            menu.getItem(1).setChecked(true);
            menu.getItem(0).setChecked(false);
            menu.getItem(2).setChecked(false);
        } else if (menu.getItem(2).getItemId() == menuID) {
            menu.getItem(2).setChecked(true);
            menu.getItem(0).setChecked(false);
            menu.getItem(1).setChecked(false);
        }
    }

    @Override
    public void onResume() {
        SharedPreferences prefs = getActivity().getSharedPreferences(PopularMoviesUtil.PREF_NAME, Context.MODE_PRIVATE);
        String value = prefs.getString(PopularMoviesUtil.PREF_FILTER, PopularMoviesUtil.PREF_FILTER_DEFAULT);
        if (value.equals(PopularMoviesUtil.PREF_FILTER_FAVOURITE)) {
            fetchDataByFilter(PopularMoviesUtil.PREF_FILTER_FAVOURITE);
        }

        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void showProgressDialog() {
        closeProgressDialog();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.fetching_data));
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = null;
    }


    private void updateGridItems(){
        if (rvThumbnailsAdapter != null) {
            rvThumbnailsAdapter.clearItems();

            if (gridItems!=null && gridItems.size() >= 0) {
                activityCallback.onFragmentInteraction(false);
                rvThumbnailsAdapter.addItems(gridItems);
            } else {
                activityCallback.onFragmentInteraction(true);
            }

            rvThumbnailsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onSuccess(List<MovieGridItem> paths) {
        //update adapter and UI on success
        closeProgressDialog();
        gridItems = paths;
        updateGridItems();
    }

    @Override
    public void onFailure(String message) {
        //update adapter and UI on failure
        closeProgressDialog();
        if (message != null) {
            activityCallback.showToast(message);
        }
        gridItems = null;
        updateGridItems();
    }

    @Override
    public void onDestroy() {
        closeProgressDialog();
        super.onDestroy();
    }

    private List<MovieGridItem> collectItems(Cursor cr){
        List<MovieGridItem> list= new ArrayList<MovieGridItem>();
        MovieGridItem item;
        try {
            if (cr != null && cr.getCount() > 0) {
                cr.moveToFirst();
                do {
                    item = new MovieGridItem();
                    item.setId(cr.getString(cr.getColumnIndex(TABLE_NAME_COLUMN_ID)));
                    item.setPosterPath(cr.getString(cr.getColumnIndex(TABLE_NAME_COLUMN_URL)));
                    item.setOriginal_title(cr.getString(cr.getColumnIndex(TABLE_NAME_COLUMN_TITLE)));
                    item.setRelease_date(cr.getString(cr.getColumnIndex(TABLE_NAME_COLUMN_RELEASE)));
                    item.setOverview(cr.getString(cr.getColumnIndex(TABLE_NAME_COLUMN_OVERVIEW)));
                    item.setVote_average(cr.getString(cr.getColumnIndex(TABLE_NAME_COLUMN_VOTE)));
                    list.add(item);
                } while (cr.moveToNext());
            }
        }finally {
            if(cr!=null){
                cr.close();
                cr = null;
            }
        }
        return list;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {

        String[] projection = FavoritesTable.PROJECTION;
        Uri uri = FavoritesContentProvider.CONTENT_URI;
        CursorLoader cursorLoader = new CursorLoader(getContext(), FavoritesContentProvider.CONTENT_URI, projection, null, null, null);

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<CursorLoader> loader, CursorLoader data) {

    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(boolean isGridEmpty);

        void showToast(String message);
    }
}
