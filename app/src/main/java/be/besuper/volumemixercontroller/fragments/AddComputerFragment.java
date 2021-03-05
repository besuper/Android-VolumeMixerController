package be.besuper.volumemixercontroller.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import be.besuper.volumemixercontroller.MainActivity;
import be.besuper.volumemixercontroller.R;
import be.besuper.volumemixercontroller.computer.Computer;
import be.besuper.volumemixercontroller.computer.SComputer;
import be.besuper.volumemixercontroller.websocket.WebSockClient;

public class AddComputerFragment extends Fragment {

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_computer, container, false);
    }

    public void onViewCreated(final @NonNull View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final MenuItem item = MainActivity.current_menu.findItem(R.id.add_computer);
        item.setVisible(false);

        final Toolbar toolbar = MainActivity.activity.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.baseline_keyboard_backspace_24);
        toolbar.setNavigationOnClickListener(view1 -> {
            toolbar.setNavigationIcon(null);
            item.setVisible(true);

            NavHostFragment.findNavController(AddComputerFragment.this).navigate(R.id.action_AddComputer_to_FirstFragment);
        });

        final Button add_computer = view.findViewById(R.id.add_a_computer);

        add_computer.setOnClickListener(view12 -> {

            final String ip = ((EditText) view.findViewById(R.id.plain_text_input)).getText().toString();
            final String port = ((EditText) view.findViewById(R.id.port_edit_text)).getText().toString();

            if (ip.isEmpty() || port.isEmpty()) {
                return;
            }

            final String bind = ip + ":" + port;
            boolean is_duplicate = false;

            for (final Computer com : SComputer.computers) {
                if (com.getBind().equals(bind)) {
                    is_duplicate = true;
                    break;
                }
            }

            if (is_duplicate) {
                Toast.makeText(view.getContext(), getString(R.string.computer_already_exist), Toast.LENGTH_LONG).show();
                return;
            }

            SComputer.addComputer(bind);

            toolbar.setNavigationIcon(null);
            item.setVisible(true);
            NavHostFragment.findNavController(AddComputerFragment.this).navigate(R.id.action_AddComputer_to_FirstFragment);
        });

        final Button auto_search = view.findViewById(R.id.auto_search);

        auto_search.setOnClickListener(view13 -> {
            auto_search.setEnabled(false);
            add_computer.setEnabled(false);
            auto_search.setText("Searching...");

            new Thread(() -> {

                int fb = 1;
                int fa = 0;

                WebSockClient client = null;
                String bind = "";

                search: while (true) {
                    try {
                        if(fb >= 64){
                            if(fa == 0){
                                fa = 1;
                                fb = 2;
                                continue;
                            }
                            break;
                        }

                        fb++;

                        bind = "192.168."+fa+"."+fb+":25565";

                        for (final Computer com : SComputer.computers) {
                            if (com.getBind().equals(bind)) {
                                continue search;
                            }
                        }

                        client = new WebSockClient(bind);
                        client.connect();
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if(client.isOpen()){
                            break;
                        }

                        client.close();
                    } catch (Exception ignored) {
                    }
                }

                if(client == null || !client.isOpen()) {
                    MainActivity.activity.runOnUiThread(() -> Toast.makeText(view.getContext(), "No device found :(", Toast.LENGTH_LONG).show());
                }else {
                    SComputer.addComputer(bind);

                    MainActivity.activity.runOnUiThread(() -> {
                        toolbar.setNavigationIcon(null);
                        item.setVisible(true);
                        NavHostFragment.findNavController(AddComputerFragment.this).navigate(R.id.action_AddComputer_to_FirstFragment);
                    });
                }

                MainActivity.activity.runOnUiThread(() -> {
                    auto_search.setText(getString(R.string.auto_search));
                    auto_search.setEnabled(true);
                    add_computer.setEnabled(true);
                });
            }).start();
        });
    }
}