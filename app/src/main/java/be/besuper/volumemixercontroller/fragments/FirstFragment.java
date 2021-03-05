package be.besuper.volumemixercontroller.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.fragment.NavHostFragment;

import java.net.URISyntaxException;

import be.besuper.volumemixercontroller.MainActivity;
import be.besuper.volumemixercontroller.R;
import be.besuper.volumemixercontroller.computer.Computer;
import be.besuper.volumemixercontroller.computer.SComputer;
import be.besuper.volumemixercontroller.websocket.WebSockClient;

public class FirstFragment extends Fragment {

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onViewCreated(final @NonNull View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Toolbar toolbar = MainActivity.activity.findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.add_computer) {
                NavHostFragment.findNavController(FirstFragment.this).navigate(R.id.action_FirstFragment_to_AddComputer);
            }
            return true;
        });

        SComputer.readAllComputers();

        if (SComputer.computers.size() >= 1) {
            final LinearLayout layout = view.findViewById(R.id.home_layout);

            layout.setPadding(50, 50, 50, 50);

            for (final Computer comp : SComputer.computers) {

                final Button btn = new Button(view.getContext());
                btn.setTextSize(20f);
                btn.setText(Html.fromHtml(comp.getName() + " <br> " + comp.getBind()));
                btn.setPadding(50, 50, 50, 50);
                btn.setGravity(Gravity.LEFT);

                final Drawable draw = ContextCompat.getDrawable(view.getContext(), R.drawable.baseline_keyboard_arrow_right_24);
                final int h = draw.getIntrinsicHeight() + 30;
                final int w = draw.getIntrinsicWidth() + 30;
                draw.setBounds(0, 0, w, h);

                btn.setCompoundDrawables(null, null, draw, null);

                final AlertDialog.Builder delete = new AlertDialog.Builder(view.getContext())
                        .setTitle(getString(R.string.confirm))
                        .setMessage(getString(R.string.confirm_message))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.ok, (dialog, whichButton) -> {

                            SComputer.computers.remove(comp);

                            if(SComputer.deleteFile(comp.getName() + ".txt")) {
                                Toast.makeText(view.getContext(), getString(R.string.deleted), Toast.LENGTH_SHORT).show();
                                getParentFragmentManager().beginTransaction().detach(this).attach(this).commit();
                            }else {
                                Toast.makeText(view.getContext(), getString(R.string.error), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null);

                btn.setOnLongClickListener(view12 -> {
                    delete.show();
                    return true;
                });

                btn.setOnClickListener(view1 -> {
                    try {
                        MainActivity.client = new WebSockClient(comp);
                        MainActivity.client.connect();

                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if (MainActivity.client.isOpen()) {
                            MainActivity.client.send("INFO"); // Test if connection is really opened

                            toolbar.setTitle(comp.getName());
                            // Send to second fragment!
                            NavHostFragment.findNavController(FirstFragment.this).navigate(R.id.action_FirstFragment_to_SecondFragment);
                        } else {
                            Toast.makeText(view.getContext(), getString(R.string.unable_connect), Toast.LENGTH_LONG).show();
                        }

                    } catch (URISyntaxException e) {
                        Toast.makeText(view.getContext(), getString(R.string.unable_connect), Toast.LENGTH_LONG).show();
                    }
                });

                layout.addView(btn);
            }
        }
    }
}