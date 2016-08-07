package cn.coolbhu.snailgo.fragments.muisc;

import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.appthemeengine.ATE;

import java.util.List;

import cn.coolbhu.snailgo.R;
import cn.coolbhu.snailgo.beans.Playlist;
import cn.coolbhu.snailgo.dataloaders.PlaylistLoader;
import cn.coolbhu.snailgo.dialogs.CreatePlaylistDialog;
import cn.coolbhu.snailgo.fragments.muisc.subfragments.PlaylistPagerFragment;
import cn.coolbhu.snailgo.views.MultiViewPager;

public class PlaylistFragment extends Fragment {

    int playlistcount;
    FragmentStatePagerAdapter adapter;
    MultiViewPager pager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_playlist, container, false);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(R.string.playlists);

        final List<Playlist> playlists = PlaylistLoader.getPlaylists(getActivity(), true);
        playlistcount = playlists.size();

        pager = (MultiViewPager) rootView.findViewById(R.id.playlistpager);

        adapter = new FragmentStatePagerAdapter(getChildFragmentManager()) {

            @Override
            public int getCount() {
                return playlistcount;
            }

            @Override
            public Fragment getItem(int position) {
                return PlaylistPagerFragment.newInstance(position);
            }

        };
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(3);

        return rootView;

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("dark_theme", false)) {
            ATE.apply(this, "dark_theme");
        } else {
            ATE.apply(this, "light_theme");
        }
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_playlist, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new_playlist:
                CreatePlaylistDialog.newInstance().show(getChildFragmentManager(), "CREATE_PLAYLIST");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updatePlaylists(final long id) {
        final List<Playlist> playlists = PlaylistLoader.getPlaylists(getActivity(), true);
        playlistcount = playlists.size();
        adapter.notifyDataSetChanged();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < playlists.size(); i++) {
                    long playlistid = playlists.get(i).id;
                    if (playlistid == id) {
                        pager.setCurrentItem(i);
                        break;
                    }
                }
            }
        }, 200);

    }
}

