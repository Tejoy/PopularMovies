package com.nanodegree.tejomai.popularmovies.ui;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.nanodegree.tejomai.popularmovies.BuildConfig;
import com.nanodegree.tejomai.popularmovies.DataDownloadComplete;
import com.nanodegree.tejomai.popularmovies.MovieDataFetcherAsyncTask;
import com.nanodegree.tejomai.popularmovies.PopularMoviesUtil;
import com.nanodegree.tejomai.popularmovies.R;
import com.nanodegree.tejomai.popularmovies.RVMovieThumbnailsAdapter;
import com.nanodegree.tejomai.popularmovies.models.MovieGridItem;

import java.io.Serializable;
import java.util.List;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MoviesGridFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MoviesGridFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoviesGridFragment extends Fragment implements DataDownloadComplete {

    private final String PARAM_API_KEY = BuildConfig.POPULAR_MOVIES_API_KEY;
    private final String PARAM_LANGUAGE = "en-US";
    private final String key_instance_save = "moviesList";

    private RVMovieThumbnailsAdapter rvThumbnailsAdapter = null;
    private List<MovieGridItem> gridItems = null;
    private OnFragmentInteractionListener activityCallback;
    private int POTRAIT_COL_COUNT = 2;
    private int LANSCAPE_COL_COUNT = 4;

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
        if(savedInstanceState!=null){
            gridItems = (List<MovieGridItem>) savedInstanceState.getSerializable(key_instance_save);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(key_instance_save,(Serializable) gridItems);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        View frame = inflater.inflate(R.layout.fragment_movies_recyclerview, container, false);
        RecyclerView rvMoviesGrid = (RecyclerView) frame.findViewById(R.id.rvMoviesGrid);
        rvThumbnailsAdapter = new RVMovieThumbnailsAdapter(getActivity(), gridItems);
        rvMoviesGrid.setAdapter(rvThumbnailsAdapter);
        int orientation = getResources().getConfiguration().orientation;
        if(orientation == Configuration.ORIENTATION_PORTRAIT){
            rvMoviesGrid.setLayoutManager(new GridLayoutManager(getActivity(),POTRAIT_COL_COUNT));
        }else{
            rvMoviesGrid.setLayoutManager(new GridLayoutManager(getActivity(),LANSCAPE_COL_COUNT));
        }

        activityCallback = (OnFragmentInteractionListener) getActivity();
        SharedPreferences preferences = getActivity().getSharedPreferences(PopularMoviesUtil.PREF_NAME,Context.MODE_PRIVATE);
        if(gridItems == null) {
            fetchDataAndUpdateGrid(preferences.getString(PopularMoviesUtil.PREF_FILTER, PopularMoviesUtil.PREF_FILTER_DEFAULT));
        }

        return frame;
    }

    private void fetchDataAndUpdateGrid(String type){
        //start task to fetch image thumbnails
        if(!PopularMoviesUtil.isNetworkAvailable(getActivity())){
            onFailure(null);
            return;
        }
        MovieDataFetcherAsyncTask fetchTask = new MovieDataFetcherAsyncTask();
        fetchTask.setDataDownloadCompete(this);
        fetchTask.execute(type,PARAM_API_KEY,PARAM_LANGUAGE);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_grid_fragment, menu);
        menu.getItem(0).setCheckable(true);
        menu.getItem(1).setCheckable(true);
        menu.getItem(0).setEnabled(true);
        menu.getItem(1).setEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        SharedPreferences prefs = getActivity().getSharedPreferences(PopularMoviesUtil.PREF_NAME,Context.MODE_PRIVATE);
        //fetch data based on menu selection
        if (id == R.id.action_popularity) {
            prefs.edit().putString(PopularMoviesUtil.PREF_FILTER,PopularMoviesUtil.PREF_FILTER_POPULARITY).commit();
            fetchDataAndUpdateGrid(PopularMoviesUtil.PREF_FILTER_POPULARITY);
            return true;
        }else if(id == R.id.action_top_rated){
            prefs.edit().putString(PopularMoviesUtil.PREF_FILTER,PopularMoviesUtil.PREF_FILTER_TOP_RATING).commit();
            fetchDataAndUpdateGrid(PopularMoviesUtil.PREF_FILTER_TOP_RATING);
            return true;
        }else if(id==R.id.action_refresh){
            fetchDataAndUpdateGrid(prefs.getString(PopularMoviesUtil.PREF_FILTER,PopularMoviesUtil.PREF_FILTER_DEFAULT));
        }
        return false;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        SharedPreferences prefs = getActivity().getSharedPreferences(PopularMoviesUtil.PREF_NAME,Context.MODE_PRIVATE);
        String value = prefs.getString(PopularMoviesUtil.PREF_FILTER,PopularMoviesUtil.PREF_FILTER_DEFAULT);
        int menuID = R.id.action_popularity;
        if(value.equals(PopularMoviesUtil.PREF_FILTER_TOP_RATING)){
            menuID = R.id.action_top_rated;
        }
        //set the menu checked status based on the selection made
        if(menu.getItem(0).getItemId() == menuID){
            menu.getItem(0).setChecked(true);
            menu.getItem(1).setChecked(false);
        }else{
            menu.getItem(1).setChecked(true);
            menu.getItem(0).setChecked(false);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onSuccess(List<MovieGridItem> paths) {
        Log.i(TAG,"Got callback success");
        //update adapter and UI on success
        gridItems = paths;
        if(rvThumbnailsAdapter!=null) {
            if(gridItems.size()==0){
                activityCallback.onFragmentInteraction(true);
            }else{
                activityCallback.onFragmentInteraction(false);
            }
            rvThumbnailsAdapter.clearItems();
            rvThumbnailsAdapter.addItems(gridItems);
            rvThumbnailsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onFailure(String message) {

        //update adapter and UI on failure
        activityCallback.onFragmentInteraction(true);
        if(message!=null) {
            Log.i(TAG,"Failed to get data with message "+message);
            activityCallback.showToast(message);
        }
        gridItems = null;
        if(rvThumbnailsAdapter!=null) {
            rvThumbnailsAdapter.clearItems();
            rvThumbnailsAdapter.notifyDataSetChanged();
        }
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
