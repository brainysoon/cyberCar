package cn.coolbhu.snailgo.adapters;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.coolbhu.snailgo.R;
import cn.coolbhu.snailgo.beans.Track;
import co.mobiwise.library.ProgressLayout;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ken on 16-8-4.
 */
public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.ViewHolder> {

    private static final int SECOND_MS = 1000;

    //TrackList
    private List<Track> trackList;

    //now playing Track
    private Track currentTrack;

    //now playing Duration
    private int currentDuration;

    //isPlaying
    private boolean isPlaying = false;

    //handler for recyclerView
    private Handler mHandler = new Handler();

    //Count Second
    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {

            currentDuration += 1;

            mHandler.postDelayed(mRunnable, SECOND_MS);
        }
    };

    //setTrackList
    public void setTrackList(List<Track> trackList) {

        this.trackList = trackList;

        //notiy
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.music_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Track track = trackList.get(position);

        holder.textViewDuration.setText(calculateSongDuration(track.getDurationInSec()));
        holder.textViewSong.setText(track.getSongName());
        holder.textViewSinger.setText(track.getSingerName());
        holder.imageViewAction.setImageResource(R.drawable.play);
        holder.progressLayout.setMaxProgress(track.getDurationInSec());

        if (currentTrack != null && currentTrack == track) {
            holder.imageViewAction.setImageResource(
                    isPlaying ? R.drawable.pause : R.drawable.play);
            holder.progressLayout.setCurrentProgress(currentDuration);
            if (isPlaying) holder.progressLayout.start();
        } else {
            holder.progressLayout.cancel();
        }

        holder.imageViewAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (track != currentTrack) {
                    currentTrack = track;
                    mHandler.removeCallbacks(mRunnable);
                    currentDuration = 0;
                }

                if (!holder.progressLayout.isPlaying()) {
                    isPlaying = true;
                    holder.progressLayout.start();
                    mHandler.postDelayed(mRunnable, 0);
                    holder.imageViewAction.setImageResource(R.drawable.pause);
                    notifyDataSetChanged();
                } else {
                    isPlaying = false;
                    holder.progressLayout.stop();
                    mHandler.removeCallbacks(mRunnable);
                    holder.imageViewAction.setImageResource(R.drawable.play);
                    notifyDataSetChanged();
                }
            }
        });
        holder.progressLayout.setProgressLayoutListener(new ProgressLayout.ProgressLayoutListener() {
            @Override
            public void onProgressCompleted() {
                holder.imageViewAction.setImageResource(R.drawable.play);
            }

            @Override
            public void onProgressChanged(int seconds) {
                holder.textViewDuration.setText(calculateSongDuration(seconds));
            }
        });
    }

    @Override
    public int getItemCount() {
        return trackList.size();
    }

    //song Duration
    private String calculateSongDuration(int seconds) {

        return new StringBuilder(String.valueOf(seconds / 60))
                .append(":")
                .append(String.valueOf(seconds % 60))
                .toString();
    }


    //ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageViewAction;
        private CircleImageView profileImage;
        private TextView textViewSong;
        private TextView textViewSinger;
        private TextView textViewDuration;
        private ProgressLayout progressLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            imageViewAction = (ImageView) itemView.findViewById(R.id.imageviewAction);
            profileImage = (CircleImageView) itemView.findViewById(R.id.profileImage);
            textViewSong = (TextView) itemView.findViewById(R.id.textviewSong);
            textViewDuration = (TextView) itemView.findViewById(R.id.textviewDuration);
            textViewSinger = (TextView) itemView.findViewById(R.id.textviewSinger);
            progressLayout = (ProgressLayout) itemView.findViewById(R.id.progressLayout);
        }
    }
}
