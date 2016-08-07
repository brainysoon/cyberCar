package cn.coolbhu.snailgo.fragments.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.coolbhu.snailgo.R;
import cn.coolbhu.snailgo.fragments.muisc.SongsFragment;

public class MusicMainFragment extends Fragment {

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

//        new initQuickControls(getActivity(), this).execute("");
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

        getChildFragmentManager().beginTransaction()
                .add(R.id.fragment_container, new SongsFragment())
                .commit();
    }
}
