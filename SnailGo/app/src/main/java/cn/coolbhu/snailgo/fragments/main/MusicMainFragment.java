package cn.coolbhu.snailgo.fragments.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cn.coolbhu.snailgo.R;
import cn.coolbhu.snailgo.adapters.MusicListAdapter;
import cn.coolbhu.snailgo.beans.Track;

public class MusicMainFragment extends Fragment {

    //view
    private RecyclerView recyclerView;

    //Adatper
    private MusicListAdapter musicListAdapter;

    public static MusicMainFragment newInstance() {
        MusicMainFragment fragment = new MusicMainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_music_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
    }

    //initView
    private void initView(View rootView) {

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        musicListAdapter = new MusicListAdapter();
        recyclerView.setAdapter(musicListAdapter);
        loadMockData();
    }

    private void loadMockData() {
        List<Track> tracks = new ArrayList<>();
        tracks.add(new Track(1, "Voodoo Mon Amor", "Diablo Swing Orchestra", 81));
        tracks.add(new Track(2, "Guerilla Laments", "Diablo Swing Orchestra", 295));
        tracks.add(new Track(3, "Kewlar Sweethearts", "Diablo Swing Orchestra", 264));
        tracks.add(new Track(4, "How To Organize a Lynch Mob", "Diablo Swing Orchestra", 23));
        tracks.add(new Track(5, "Black Box Messiah", "Diablo Swing Orchestra", 297));
        tracks.add(new Track(6, "Exit Strategy of a Wrecking Ball", "Diablo Swing Orchestra", 361));
        tracks.add(new Track(7, "Aurora", "Diablo Swing Orchestra", 305));
        tracks.add(new Track(8, "Mass Rapture", "Diablo Swing Orchestra", 303));
        tracks.add(new Track(9, "Black Box Messiah", "Diablo Swing Orchestra", 297));
        tracks.add(new Track(10, "Exit Strategy of a Wrecking Ball", "Diablo Swing Orchestra", 361));
        tracks.add(new Track(11, "Aurora", "Diablo Swing Orchestra", 305));
        tracks.add(new Track(12, "Mass Rapture", "Diablo Swing Orchestra", 303));
        musicListAdapter.setTrackList(tracks);
    }
}
