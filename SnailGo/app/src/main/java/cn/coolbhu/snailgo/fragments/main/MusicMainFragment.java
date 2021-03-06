package cn.coolbhu.snailgo.fragments.main;

import android.Manifest;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cn.coolbhu.snailgo.R;
import cn.coolbhu.snailgo.activities.musics.BaseActivity;
import cn.coolbhu.snailgo.adapters.SongsListAdapter;
import cn.coolbhu.snailgo.beans.Song;
import cn.coolbhu.snailgo.dataloaders.SongLoader;
import cn.coolbhu.snailgo.listeners.MusicStateListener;
import cn.coolbhu.snailgo.utils.IntentUtils;
import cn.coolbhu.snailgo.utils.PreferencesUtils;
import cn.coolbhu.snailgo.views.DividerItemDecoration;
import cn.coolbhu.snailgo.views.FastScroller;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MusicMainFragment extends Fragment implements MusicStateListener {

    private SongsListAdapter mAdapter;
    private RecyclerView recyclerView;
    private PreferencesUtils mPreferences;


    public static MusicMainFragment newInstance() {
        MusicMainFragment fragment = new MusicMainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

        }

        mPreferences = PreferencesUtils.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_recyclerview, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        FastScroller fastScroller = (FastScroller) rootView.findViewById(R.id.fastscroller);
        fastScroller.setRecyclerView(recyclerView);

        MusicMainFragmentPermissionsDispatcher.toLoadSongsWithCheck(this);
        ((BaseActivity) getActivity()).setMusicStateListenerListener(this);

        return rootView;
    }

    @NeedsPermission({Manifest.permission.READ_EXTERNAL_STORAGE})
    public void toLoadSongs() {

        new loadSongs().execute("");
    }

    @OnShowRationale({Manifest.permission.READ_EXTERNAL_STORAGE})
    public void onMusicShowRationale(final PermissionRequest request) {

        new AlertDialog.Builder(getContext())
                .setTitle(R.string.request_permission)
                .setIcon(R.mipmap.ic_launcher)
                .setMessage(R.string.request_music_permission)
                .setPositiveButton(R.string.allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        request.proceed();
                    }
                })
                .setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        request.cancel();
                    }
                })
                .setCancelable(false)
                .show();
    }

    @OnPermissionDenied({Manifest.permission.READ_EXTERNAL_STORAGE})
    public void onMusicPermissionDenied() {

        Snackbar.make(recyclerView, R.string.permission_denie, Snackbar.LENGTH_SHORT)
                .setAction(R.string.setting, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        IntentUtils.toSnailGoSettings(getContext());
                    }
                })
                .show();
    }

    @OnNeverAskAgain({Manifest.permission.READ_EXTERNAL_STORAGE})
    public void onMusicPermissionNerver() {

        Snackbar.make(recyclerView, R.string.permission_denie, Snackbar.LENGTH_LONG)
                .setAction(R.string.setting, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        IntentUtils.toSnailGoSettings(getContext());
                    }
                })
                .show();
    }

    public void restartLoader() {

    }

    public void onPlaylistChanged() {

    }

    public void onMetaChanged() {
        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }

    private void reloadAdapter() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... unused) {
                List<Song> songList = SongLoader.getAllSongs(getActivity());
                mAdapter.updateDataSet(songList);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                mAdapter.notifyDataSetChanged();
            }
        }.execute();
    }
//修复家宝冲突bug
//    @Override
//    public void onActivityCreated(final Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        setHasOptionsMenu(true);
//    }
//
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.song_sort_by, menu);
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.menu_sort_by_az:
//                mPreferences.setSongSortOrder(SortOrder.SongSortOrder.SONG_A_Z);
//                reloadAdapter();
//                return true;
//            case R.id.menu_sort_by_za:
//                mPreferences.setSongSortOrder(SortOrder.SongSortOrder.SONG_Z_A);
//                reloadAdapter();
//                return true;
//            case R.id.menu_sort_by_artist:
//                mPreferences.setSongSortOrder(SortOrder.SongSortOrder.SONG_ARTIST);
//                reloadAdapter();
//                return true;
//            case R.id.menu_sort_by_album:
//                mPreferences.setSongSortOrder(SortOrder.SongSortOrder.SONG_ALBUM);
//                reloadAdapter();
//                return true;
//            case R.id.menu_sort_by_year:
//                mPreferences.setSongSortOrder(SortOrder.SongSortOrder.SONG_YEAR);
//                reloadAdapter();
//                return true;
//            case R.id.menu_sort_by_duration:
//                mPreferences.setSongSortOrder(SortOrder.SongSortOrder.SONG_DURATION);
//                reloadAdapter();
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    private class loadSongs extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            if (getActivity() != null)

                try {
                    mAdapter = new SongsListAdapter((AppCompatActivity) getActivity(), SongLoader.getAllSongs(getActivity()), false);
                } catch (Exception ex) {

                    ex.printStackTrace();
                }

            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            recyclerView.setAdapter(mAdapter);
            if (getActivity() != null)
                recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        }

        @Override
        protected void onPreExecute() {
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        MusicMainFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
