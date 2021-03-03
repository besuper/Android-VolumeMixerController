package be.besuper.volumemixercontroller.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import be.besuper.volumemixercontroller.MainActivity;
import be.besuper.volumemixercontroller.R;

public class SecondFragment extends Fragment {

    public static final HashMap<String, List<Object>> elements = new HashMap<>();
    public static boolean modified = false;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_second, container, false);
    }

    public void onViewCreated(final @NonNull View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Thread thread = new Thread(() -> new Timer().schedule(new TimerTask() {
            @Override
            public void run() {

                if (MainActivity.client == null || MainActivity.client.isClosed()) {
                    return;
                }

                if (modified) {
                    modified = false;
                    return;
                }

                MainActivity.activity.runOnUiThread(() -> {

                    if (elements.size() == 0) {
                        setup(view);
                    } else {
                        for (final Map.Entry<String, Float> entry : MainActivity.client.getApps().entrySet()) {

                            final String key = entry.getKey();
                            final int progress = (int) (entry.getValue() * 1);

                            if (elements.containsKey(key)) {

                                if (progress == -100) {
                                    ((SeekBar) elements.get(key).get(0)).setProgress(0);
                                    ((TextView) elements.get(key).get(1)).setText(key + " - muted");
                                } else {
                                    ((SeekBar) elements.get(key).get(0)).setProgress(progress);
                                    ((TextView) elements.get(key).get(1)).setText(key + " - " + progress + "%");
                                }
                            }
                        }
                    }

                });

                try {
                    MainActivity.client.send("APPS");
                } catch (Exception e) {
                    this.cancel();
                }
            }
        }, 0, 2000));

        thread.start();

        // Hide Add computer icon on top right
        final MenuItem item = MainActivity.current_menu.findItem(R.id.add_computer);
        item.setVisible(false);

        final Toolbar toolbar = MainActivity.activity.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.baseline_keyboard_backspace_24);
        toolbar.setNavigationOnClickListener(view1 -> {
            toolbar.setNavigationIcon(null);
            item.setVisible(true);

            thread.interrupt();
            MainActivity.client.close();
            elements.clear();
            NavHostFragment.findNavController(SecondFragment.this).navigate(R.id.action_SecondFragment_to_FirstFragment);
        });

        final SwipeRefreshLayout refresh = view.findViewById(R.id.refresh_layout);
        refresh.setOnRefreshListener(() -> {

            if (MainActivity.client == null || MainActivity.client.isClosed()) {
                return;
            }

            ((LinearLayout) view.findViewById(R.id.layout)).removeAllViewsInLayout();

            elements.clear();
            setup(view);

            refresh.setRefreshing(false);
        });
    }

    public void setup(View view) {
        final LinearLayout layout = view.findViewById(R.id.layout);

        //Log.d("BESUPER" , "SIZEER: "+MainActivity.client.getApps().size());

        for (final Map.Entry<String, Float> entry : MainActivity.client.getApps().entrySet()) {
            final String key = entry.getKey();
            final int progress = (int) (entry.getValue() * 1);

            final TextView textView = new TextView(view.getContext());
            textView.setTextSize(20f);
            textView.setText(key + " - " + progress + "%");
            textView.setPadding(50, 50, 0, 0);
            layout.addView(textView);

            final SeekBar bar = new SeekBar(view.getContext());
            bar.setMax(100);
            bar.setProgress(progress);

            final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            );
            params.setMargins(0, 0, 0, 50);
            bar.setLayoutParams(params);

            bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                int progressChangedValue = 0;

                @Override
                public void onProgressChanged(final SeekBar seekBar, final int i, final boolean b) {
                    progressChangedValue = i;
                    textView.setText(key + " - " + progressChangedValue + "%");
                }

                @Override
                public void onStartTrackingTouch(final SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(final SeekBar seekBar) {
                    MainActivity.client.send("VOLUME:" + key + ":" + progressChangedValue);
                    MainActivity.client.getApps().put(key, (float) progressChangedValue);
                    modified = true;
                }

            });

            layout.addView(bar);

            elements.put(key, Arrays.asList(bar, textView));
        }
    }

}