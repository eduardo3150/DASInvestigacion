package com.chavez.eduardo.dasinvestigacion;


import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.chavez.eduardo.dasinvestigacion.network.GameObject;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.transitionseverywhere.Fade;
import com.transitionseverywhere.Slide;
import com.transitionseverywhere.Transition;
import com.transitionseverywhere.TransitionManager;
import com.transitionseverywhere.TransitionSet;
import com.transitionseverywhere.extra.Scale;

public class DetailActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {
    private GameObject object;
    private ImageView headerReceived, thumbnailImage;
    private TextView gameTitle, gameDescription, gameDeveloper, gameReleaseDate, gamePlatforms, developerLabel, platformsLabel, labelRelease;
    Bundle extras;
    private ViewGroup viewGroup;
    private ImageButton likeButton, browserButton;


    private static final int PERCENTAGE_TO_SHOW_IMAGE = 20;
    private int mMaxScrollSize;
    private boolean mIsImageHidden;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        supportPostponeEnterTransition();

        viewGroup = (ViewGroup) findViewById(R.id.viewGroupContainer);
        headerReceived = (ImageView) findViewById(R.id.tab_header);
        thumbnailImage = (ImageView) viewGroup.findViewById(R.id.thumbnailPicture);
        gameTitle = (TextView) viewGroup.findViewById(R.id.gameTitle);
        gameDescription = (TextView) viewGroup.findViewById(R.id.gameDescription);
        gameDeveloper = (TextView) viewGroup.findViewById(R.id.developerTitle);
        gamePlatforms = (TextView) viewGroup.findViewById(R.id.platformTitle);
        developerLabel = (TextView) viewGroup.findViewById(R.id.developedLabel);
        platformsLabel = (TextView) viewGroup.findViewById(R.id.platformLabel);
        gameReleaseDate = (TextView) viewGroup.findViewById(R.id.releaseTitle);
        labelRelease = (TextView) viewGroup.findViewById(R.id.releaseLabel);
        likeButton = (ImageButton) viewGroup.findViewById(R.id.buttonLike);
        browserButton = (ImageButton) viewGroup.findViewById(R.id.buttonBrowser);

        if (getIntent().getExtras() != null) {
            extras = getIntent().getExtras();
            object = (GameObject) extras.getSerializable("object");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(object.getGame_name());
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String transitionName = extras.getString("transition");
            headerReceived.setTransitionName(transitionName);
            Picasso.with(this)
                    .load(object.getGame_image())
                    .noFade()
                    .into(headerReceived, new Callback() {
                        @Override
                        public void onSuccess() {
                            supportStartPostponedEnterTransition();
                        }

                        @Override
                        public void onError() {
                            supportPostponeEnterTransition();
                        }
                    });
        } else {
            Picasso.with(this)
                    .load(object.getGame_image())
                    .noFade()
                    .into(headerReceived, new Callback() {
                        @Override
                        public void onSuccess() {
                            supportStartPostponedEnterTransition();
                        }

                        @Override
                        public void onError() {
                            supportPostponeEnterTransition();
                        }
                    });
        }
        workReceivedItem();

        //YAS
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                animate();
            }
        }, 250);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Editar", Snackbar.LENGTH_SHORT).show();
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(this);


    }


    private void workReceivedItem() {
        Picasso.with(this)
                .load(object.getCover_image())
                .noFade()
                .into(thumbnailImage);
        gameTitle.setText(object.getGame_name());
        gameDescription.setText(object.getGame_description());
        gameDeveloper.setText(object.getGame_developer());
        gamePlatforms.setText(object.getPlatform());
        gameReleaseDate.setText(object.getRelease_date());

        likeButton.setOnClickListener(new View.OnClickListener() {
            boolean like;

            @Override
            public void onClick(View view) {
                like = !like;
                likeButton.setImageResource(like ? R.drawable.ic_star_24dp : R.drawable.ic_star_border_24dp);
                if (like) {
                    Snackbar.make(view, "Me gusta", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(view, "Ya no me gusta", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        browserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Buscar " + object.getWebsite(), Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void animate() {
        TransitionSet set = new TransitionSet()
                .addTransition(new Scale(0.7f))
                .addTransition(new Fade())
                .setInterpolator(new FastOutLinearInInterpolator())
                .addListener(new Transition.TransitionListener() {
                    @Override
                    public void onTransitionStart(Transition transition) {

                    }

                    @Override
                    public void onTransitionEnd(Transition transition) {
                        animateButtons();
                    }

                    @Override
                    public void onTransitionCancel(Transition transition) {

                    }

                    @Override
                    public void onTransitionPause(Transition transition) {

                    }

                    @Override
                    public void onTransitionResume(Transition transition) {

                    }
                });
        TransitionManager.beginDelayedTransition(viewGroup, set);
        gameTitle.setVisibility(View.VISIBLE);
        gameDescription.setVisibility(View.VISIBLE);
        thumbnailImage.setVisibility(View.VISIBLE);
        gameDeveloper.setVisibility(View.VISIBLE);
        gamePlatforms.setVisibility(View.VISIBLE);
        developerLabel.setVisibility(View.VISIBLE);
        platformsLabel.setVisibility(View.VISIBLE);
        gameReleaseDate.setVisibility(View.VISIBLE);
        labelRelease.setVisibility(View.VISIBLE);
    }

    private void animateButtons() {
        TransitionManager.beginDelayedTransition(viewGroup, new Slide(Gravity.BOTTOM));
        likeButton.setVisibility(View.VISIBLE);
        browserButton.setVisibility(View.VISIBLE);
    }


    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (mMaxScrollSize == 0)
            mMaxScrollSize = appBarLayout.getTotalScrollRange();

        int currentScrollPercentage = (Math.abs(verticalOffset)) * 100
                / mMaxScrollSize;

        if (currentScrollPercentage >= PERCENTAGE_TO_SHOW_IMAGE) {
            if (!mIsImageHidden) {
                mIsImageHidden = true;

                ViewCompat.animate(fab).scaleY(0).scaleX(0).start();
            }
        }

        if (currentScrollPercentage < PERCENTAGE_TO_SHOW_IMAGE) {
            if (mIsImageHidden) {
                mIsImageHidden = false;
                ViewCompat.animate(fab).scaleY(1).scaleX(1).start();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
