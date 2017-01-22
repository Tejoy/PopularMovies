package com.nanodegree.tejomai.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity{

    final String TAG = "MovieDetailActivity";
    ImageView img;
    TextView tv_title,tv_vote,tv_overview,tv_release_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        img = (ImageView) findViewById(R.id.thumbnail);
        tv_title = (TextView) findViewById(R.id.title);
        tv_overview = (TextView) findViewById(R.id.overview);
        tv_release_date = (TextView) findViewById(R.id.release_date);
        tv_vote = (TextView) findViewById(R.id.vote);
        String url;

        Intent intent = getIntent();
        if(intent!=null){
            //retrieve data from intent and set in the layout
            url = intent.getStringExtra(PopularMoviesUtil.EXTRA_IMAGE_URL);
            Picasso.with(getBaseContext()).load(url).into(img);
            tv_title.setText(intent.getStringExtra(PopularMoviesUtil.EXTRA_TITLE));
            tv_overview.setText(intent.getStringExtra(PopularMoviesUtil.EXTRA_OVERVIEW));
            tv_release_date.setText(intent.getStringExtra(PopularMoviesUtil.EXTRA_RELEASE_DATE));
            tv_vote.setText(""+intent.getIntExtra(PopularMoviesUtil.EXTRA_VOTE,0));
        }
    }
}
