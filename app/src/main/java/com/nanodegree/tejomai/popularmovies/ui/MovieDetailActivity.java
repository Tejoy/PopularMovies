package com.nanodegree.tejomai.popularmovies.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.nanodegree.tejomai.popularmovies.PopularMoviesUtil;
import com.nanodegree.tejomai.popularmovies.R;
import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity{

    final String TAG = "MovieDetailActivity";
    ImageView thumbnail_img;
    TextView tv_title,tv_vote,tv_overview,tv_release_date;
    String title;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.coordinator_layout);

        final CollapsingToolbarLayout layout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        layout.setTitleEnabled(true);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        thumbnail_img = (ImageView) findViewById(R.id.thumbnail_image);
        tv_title = (TextView) findViewById(R.id.title);
        tv_overview = (TextView) findViewById(R.id.overview);
        tv_release_date = (TextView) findViewById(R.id.release_date);
        tv_vote = (TextView) findViewById(R.id.vote);
        String url;

        Intent intent = getIntent();
        if(intent!=null){
            //retrieve data from intent and set in the layout
            title = intent.getStringExtra(PopularMoviesUtil.EXTRA_TITLE);
            tv_title.setText(title);
            tv_overview.setText(intent.getStringExtra(PopularMoviesUtil.EXTRA_OVERVIEW));
            tv_release_date.setText(intent.getStringExtra(PopularMoviesUtil.EXTRA_RELEASE_DATE));
            tv_vote.setText(""+intent.getStringExtra(PopularMoviesUtil.EXTRA_VOTE));

            url = intent.getStringExtra(PopularMoviesUtil.EXTRA_IMAGE_URL);
            Picasso.with(getBaseContext()).load(url).into(thumbnail_img);
            layout.setTitle(" ");
            appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                boolean isShow = false;
                int scrollRange = -1;

                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    if(scrollRange == -1){
                        scrollRange = appBarLayout.getTotalScrollRange();
                    }
                    if(scrollRange+verticalOffset == 0){
                        layout.setTitle(title);
                        tv_title.setVisibility(View.GONE);
                        isShow = true;
                    }else if (isShow){
                        layout.setTitle(" ");
                        isShow = false;
                        tv_title.setVisibility(View.VISIBLE);
                    }
                }
            });




        }
    }
}
