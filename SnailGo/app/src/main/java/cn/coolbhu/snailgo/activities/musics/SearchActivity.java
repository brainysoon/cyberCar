package cn.coolbhu.snailgo.activities.musics;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import cn.coolbhu.snailgo.R;
import cn.coolbhu.snailgo.adapters.SearchAdapter;
import cn.coolbhu.snailgo.beans.Album;
import cn.coolbhu.snailgo.beans.Artist;
import cn.coolbhu.snailgo.beans.Song;
import cn.coolbhu.snailgo.dataloaders.AlbumLoader;
import cn.coolbhu.snailgo.dataloaders.ArtistLoader;
import cn.coolbhu.snailgo.dataloaders.SongLoader;
import cn.coolbhu.snailgo.providers.SearchHistory;
import cn.coolbhu.snailgo.utils.IntentUtils;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class SearchActivity extends BaseThemedActivity implements SearchView.OnQueryTextListener, View.OnTouchListener {

    private SearchView mSearchView;
    private InputMethodManager mImm;
    private String queryString;

    private SearchAdapter adapter;
    private RecyclerView recyclerView;

    private List searchResults = Collections.emptyList();

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mImm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new SearchAdapter(this);
        recyclerView.setAdapter(adapter);

        SearchActivityPermissionsDispatcher.initSearchAdapterWithCheck(this);
    }

    @NeedsPermission({Manifest.permission.READ_EXTERNAL_STORAGE})
    public void initSearchAdapter() {


    }

    @OnShowRationale({Manifest.permission.READ_EXTERNAL_STORAGE})
    public void onMusicShowRationale(final PermissionRequest request) {

        new AlertDialog.Builder(SearchActivity.this)
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

        Toast.makeText(this, R.string.permission_denie, Toast.LENGTH_SHORT).show();

        this.finish();
    }

    @OnNeverAskAgain({Manifest.permission.READ_EXTERNAL_STORAGE})
    public void onMusicPermissionNerver() {

        Snackbar.make(recyclerView, R.string.permission_denie, Snackbar.LENGTH_LONG)
                .setAction(R.string.setting, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        IntentUtils.toSnailGoSettings(SearchActivity.this);
                    }
                })
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        SearchActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {

        getMenuInflater().inflate(R.menu.menu_search, menu);

        mSearchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_search));

        mSearchView.setOnQueryTextListener(this);
        mSearchView.setQueryHint(getString(R.string.search_library));

        mSearchView.setIconifiedByDefault(false);
        mSearchView.setIconified(false);

        MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.menu_search), new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                finish();
                return false;
            }
        });

        menu.findItem(R.id.menu_search).expandActionView();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(final String query) {
        onQueryTextChange(query);
        hideInputManager();

        return true;
    }

    @Override
    public boolean onQueryTextChange(final String newText) {

        if (newText.equals(queryString)) {
            return true;
        }
        queryString = newText;
        if (!queryString.trim().equals("")) {
            this.searchResults = new ArrayList();
            List<Song> songList = SongLoader.searchSongs(this, queryString);
            List<Album> albumList = AlbumLoader.getAlbums(this, queryString);
            List<Artist> artistList = ArtistLoader.getArtists(this, queryString);

            if (!songList.isEmpty()) {
                searchResults.add("Songs");
            }
            searchResults.addAll((Collection) (songList.size() < 10 ? songList : songList.subList(0, 10)));
            if (!albumList.isEmpty()) {
                searchResults.add("Albums");
            }
            searchResults.addAll((Collection) (albumList.size() < 7 ? albumList : albumList.subList(0, 7)));
            if (!artistList.isEmpty()) {
                searchResults.add("Artists");
            }
            searchResults.addAll((Collection) (artistList.size() < 7 ? artistList : artistList.subList(0, 7)));
        } else {
            searchResults.clear();
            adapter.updateSearchResults(searchResults);
            adapter.notifyDataSetChanged();
        }

        adapter.updateSearchResults(searchResults);
        adapter.notifyDataSetChanged();

        return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        hideInputManager();
        return false;
    }

    public void hideInputManager() {
        if (mSearchView != null) {
            if (mImm != null) {
                mImm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);
            }
            mSearchView.clearFocus();

            SearchHistory.getInstance(this).addSearchString(queryString);
        }
    }
}
