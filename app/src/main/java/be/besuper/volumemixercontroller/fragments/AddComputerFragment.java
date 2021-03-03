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
                Toast.makeText(view.getContext(), "Computer already exist!", Toast.LENGTH_LONG).show();
                return;
            }

            SComputer.addComputer(bind);

            toolbar.setNavigationIcon(null);
            item.setVisible(true);
            NavHostFragment.findNavController(AddComputerFragment.this).navigate(R.id.action_AddComputer_to_FirstFragment);
        });
    }
}