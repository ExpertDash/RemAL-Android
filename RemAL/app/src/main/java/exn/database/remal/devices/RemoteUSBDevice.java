package exn.database.remal.devices;

import org.json.JSONException;
import org.json.JSONObject;

import exn.database.remal.requests.ActionValidCallback;

public class RemoteUSBDevice extends RemoteDevice {
    public RemoteUSBDevice(String name) {
        super(name);
    }

    public RemoteUSBDevice() {
        super();
    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public void disconnect() {

    }

    @Override
    public void connect(ActionValidCallback callback) {
        callback.run(false);
    }

    @Override
    public void sendRequest(String request, ActionValidCallback callback) {
        callback.run(false);
    }

    public String getConnectionName() {
        return "USB";
    }

    @Override
    public String save() {
        JSONObject data = new JSONObject();

        try {
            data.put("name", name);
        } catch(JSONException e) {
            e.printStackTrace();
        }

        return data.toString();
    }

    @Override
    public void load(String data) {
        try {
            JSONObject obj = new JSONObject(data);

            name = obj.getString("name");
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }
}
