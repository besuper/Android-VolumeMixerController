package be.besuper.volumemixercontroller.websocket;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;

import be.besuper.volumemixercontroller.computer.Computer;
import be.besuper.volumemixercontroller.computer.SComputer;

public class WebSockClient extends WebSocketClient {

    private final HashMap<String, Float> apps = new HashMap<>();
    private final Computer computer;

    public WebSockClient(final Computer computer) throws URISyntaxException {
        super(new URI("ws://" + computer.getBind()));
        this.computer = computer;
    }

    @Override
    public void onOpen(final ServerHandshake handshake) {
        send("APPS"); // Get all APPS
        send("INFO"); // Get all computer information
    }

    @Override
    public void onMessage(final String message) {
        //Log.d("BESUPER", "Message: "+message);

        if (message.startsWith("{\"apps\":")) {
            try {
                final JSONObject apps = new JSONObject(message).getJSONObject("apps");

                for (final Iterator<String> it = apps.keys(); it.hasNext(); ) {
                    final String s = it.next();
                    this.apps.put(s, Float.parseFloat(apps.getString(s).replace(",", ".")) * 100);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (message.startsWith("{\"info\":") && this.computer.getName().equals("Unknow")) {
            try {
                SComputer.fixComputerName(new JSONObject(message).getJSONObject("info").getString("computer"), this.computer);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onClose(final int code, final String reason, final boolean remote) {}

    @Override
    public void onError(final Exception ex) {
        Log.d("SOCK", "Error");
        ex.printStackTrace();
    }

    public HashMap<String, Float> getApps() {
        return apps;
    }
}
