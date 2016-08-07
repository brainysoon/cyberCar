package cn.coolbhu.snailgo.fragments.muisc;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import cn.coolbhu.snailgo.R;
import cn.coolbhu.snailgo.beans.Artist;
import cn.coolbhu.snailgo.dataloaders.ArtistLoader;
import cn.coolbhu.snailgo.lastfmapi.LastFmClient;
import cn.coolbhu.snailgo.lastfmapi.callbacks.ArtistInfoListener;
import cn.coolbhu.snailgo.lastfmapi.models.ArtistQuery;
import cn.coolbhu.snailgo.lastfmapi.models.LastfmArtist;
import cn.coolbhu.snailgo.utils.ATEUtils;
import cn.coolbhu.snailgo.utils.Constants;
import cn.coolbhu.snailgo.utils.Helpers;
import cn.coolbhu.snailgo.utils.ImageUtils;

public class ArtistDetailFragment extends Fragment {

    long artistID = -1;

    ImageView artistArt;

    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    AppBarLayout appBarLayout;
    boolean largeImageLoaded = false;
    int primaryColor = -1;

    public static ArtistDetailFragment newInstance(long id, boolean useTransition, String transitionName) {
        ArtistDetailFragment fragment = new ArtistDetailFragment();
        Bundle args = new Bundle();
        args.putLong(Constants.ARTIST_ID, id);
        args.putBoolean("transition", useTransition);
        if (useTransition)
            args.putString("transition_name", transitionName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            artistID = getArguments().getLong(Constants.ARTIST_ID);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_artist_detail, container, false);

        artistArt = (ImageView) rootView.findViewById(R.id.artist_art);

        collapsingToolbarLayout = (CollapsingToolbarLayout) rootView.findViewById(R.id.collapsing_toolbar);
        appBarLayout = (AppBarLayout) rootView.findViewById(R.id.app_bar);

        if (getArguments().getBoolean("transition")) {
            artistArt.setTransitionName(getArguments().getString("transition_name"));
        }

        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        setupToolbar();
        setUpArtistDetails();

        getChildFragmentManager().beginTransaction().replace(R.id.container, ArtistMusicFragment.newInstance(artistID)).commit();


        return rootView;
    }

    private void setupToolbar() {

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setUpArtistDetails() {

        final Artist artist = ArtistLoader.getArtist(getActivity(), artistID);

        collapsingToolbarLayout.setTitle(artist.name);

        LastFmClient.getInstance(getActivity()).getArtistInfo(new ArtistQuery(artist.name), new ArtistInfoListener() {
            @Override
            public void artistInfoSucess(final LastfmArtist artist) {
                if (artist != null) {

                    ImageLoader.getInstance().displayImage(artist.mArtwork.get(4).mUrl, artistArt,
                            new DisplayImageOptions.Builder().cacheInMemory(true)
                                    .cacheOnDisk(true)
                                    .showImageOnFail(R.drawable.ic_empty_music2)
                                    .build(), new SimpleImageLoadingListener() {
                                @Override
                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                    largeImageLoaded = true;
                                    try {
                                        new Palette.Builder(loadedImage).generate(new Palette.PaletteAsyncListener() {
                                            @Override
                                            public void onGenerated(Palette palette) {
                                                Palette.Swatch swatch = palette.getVibrantSwatch();
                                                if (swatch != null) {
                                                    primaryColor = swatch.getRgb();
                                                    collapsingToolbarLayout.setContentScrimColor(primaryColor);
                                                    if (getActivity() != null)
                                                        ATEUtils.setStatusBarColor(getActivity(), Helpers.getATEKey(getActivity()), primaryColor);
                                                } else {
                                                    Palette.Swatch swatchMuted = palette.getMutedSwatch();
                                                    if (swatchMuted != null) {
                                                        primaryColor = swatchMuted.getRgb();
                                                        collapsingToolbarLayout.setContentScrimColor(primaryColor);
                                                        if (getActivity() != null)
                                                            ATEUtils.setStatusBarColor(getActivity(), Helpers.getATEKey(getActivity()), primaryColor);
                                                    }
                                                }

                                            }
                                        });
                                    } catch (Exception ignored) {

                                    }
                                }
                            });
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setBlurredPlaceholder(artist);
                        }
                    }, 100);

                }
            }

            @Override
            public void artistInfoFailed() {

            }
        });

    }

    private void setBlurredPlaceholder(LastfmArtist artist) {
        ImageLoader.getInstance().loadImage(artist.mArtwork.get(1).mUrl, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (getActivity() != null && !largeImageLoaded)
                    new setBlurredAlbumArt().execute(loadedImage);

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        toolbar.setBackgroundColor(Color.TRANSPARENT);
        if (primaryColor != -1 && getActivity() != null) {
            collapsingToolbarLayout.setContentScrimColor(primaryColor);
            String ateKey = Helpers.getATEKey(getActivity());
            ATEUtils.setStatusBarColor(getActivity(), ateKey, primaryColor);
        }

    }

    private class setBlurredAlbumArt extends AsyncTask<Bitmap, Void, Drawable> {

        @Override
        protected Drawable doInBackground(Bitmap... loadedImage) {
            Drawable drawable = null;
            try {
                drawable = ImageUtils.createBlurredImageFromBitmap(loadedImage[0], getActivity(), 3);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return drawable;
        }

        @Override
        protected void onPostExecute(Drawable result) {
            if (result != null && !largeImageLoaded) {
                artistArt.setImageDrawable(result);
            }
        }

        @Override
        protected void onPreExecute() {
        }
    }

}