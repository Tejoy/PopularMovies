package com.nanodegree.tejomai.popularmovies.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.nanodegree.tejomai.popularmovies.PopularMoviesUtil;
import com.nanodegree.tejomai.popularmovies.R;

public class MainActivity extends AppCompatActivity implements MoviesGridFragment.OnFragmentInteractionListener {

    private TextView tv_no_movies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            (getSupportFragmentManager().beginTransaction()).add(android.R.id.content, new MoviesGridFragment()).commit();
        }
        tv_no_movies = (TextView) findViewById(R.id.no_data_msg);

    }

    @Override
    public void onFragmentInteraction(boolean isGridEmpty) {
        //set appropriate message when no data
        if (isGridEmpty) {
            tv_no_movies.setVisibility(View.VISIBLE);
            if (!PopularMoviesUtil.isNetworkAvailable(getBaseContext())) {
                tv_no_movies.setText(getResources().getString(R.string.no_network));
            } else {
                tv_no_movies.setText(getResources().getString(R.string.no_data));
            }
        } else {
            tv_no_movies.setVisibility(View.GONE);
        }
    }

    @Override
    public void showToast(String message) {
        if (message != null) {
            Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
