package com.nanodegree.tejomai.popularmovies;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

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
public class MoviesGridFragment extends Fragment implements DataDownloadComplete{

    private final String PARAM_API_KEY = "<ENTER YOUR API KEY>";
    private final String PARAM_LANGUAGE = "en-US";

    private MovieThumbnailsAdapter thumbnailsAdapter = null;
    private OnFragmentInteractionListener activityCallback;

    public MoviesGridFragment() {
    }

    public static MoviesGridFragment newInstance(String param1, String param2) {
        MoviesGridFragment fragment = new MoviesGridFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View frame = inflater.inflate(R.layout.fragment_movies_grid, container, false);
        GridView moviesGrid = (GridView) frame.findViewById(R.id.gridview);

        thumbnailsAdapter = new MovieThumbnailsAdapter(getActivity(),null);
        activityCallback = (OnFragmentInteractionListener) getActivity();
        SharedPreferences preferences = getActivity().getSharedPreferences(PopularMoviesUtil.PREF_NAME,Context.MODE_PRIVATE);

        fetchDataAndUpdateGrid(preferences.getString(PopularMoviesUtil.PREF_FILTER,PopularMoviesUtil.PREF_FILTER_DEFAULT));
        moviesGrid.setAdapter(thumbnailsAdapter);


        return moviesGrid;
    }

    private void fetchDataAndUpdateGrid(String type){
        //start task to fetch image thumbnails
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
    public void onSuccess(List<MovieGridItem> posterPaths) {
        Log.i(TAG,"Got callback success");
        //update adapter and UI on success
        if(thumbnailsAdapter!=null) {
            if(posterPaths.size()==0){
                activityCallback.onFragmentInteraction(true);
            }else{
                activityCallback.onFragmentInteraction(false);
            }
            thumbnailsAdapter.clearItems();
            thumbnailsAdapter.addItems(posterPaths);
            thumbnailsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onFailure(String message) {
        Log.i(TAG,"Failed to get data with message "+message);
        //update adapter and UI on failure
        activityCallback.onFragmentInteraction(true);
        activityCallback.showToast(message);
        if(thumbnailsAdapter!=null) {
            thumbnailsAdapter.clearItems();
            thumbnailsAdapter.notifyDataSetChanged();
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
