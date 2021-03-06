package exn.database.remal;

import android.os.Bundle;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

import java.util.Arrays;

import exn.database.remal.core.RemAL;
import exn.database.remal.deck.ITile;
import exn.database.remal.devices.IRemoteDevice;

public class TileOptionsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private ITile tile;

    public void setTile(ITile tile) {
        this.tile = tile;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.tile_preferences, rootKey);

        PreferenceScreen screen = getPreferenceScreen();

        for(int i = 0; i < screen.getPreferenceCount(); i++) {
            Preference pref = screen.getPreference(i);
            initPreference(pref);
            pref.setOnPreferenceChangeListener(this);
            pref.setOnPreferenceClickListener(this);
        }
    }

    private void initPreference(Preference pref) {
        switch(pref.getKey()) {
            case "tile_name": {
                String name = tile.getName();
                pref.setSummary(name.isEmpty() ? "Unset" : name);
                break;
            }
            case "tile_device":
                pref.setSummary(tile.getTargetDevice() == null ? "None selected" : tile.getTargetDevice().getName());
                break;
            case "tile_app":
                pref.setSummary(tile.getRequest().isEmpty() ? "None selected" : tile.getRequest());
                break;
            case "tile_type": {
                ListPreference p = (ListPreference)pref;
                pref.setSummary(p.getEntries()[Arrays.asList(p.getEntryValues()).indexOf(tile.getRequestType())]);
                break;
            }
        }
    }

    public boolean onPreferenceChange(Preference pref, Object newValue) {
        switch(pref.getKey()) {
            case "tile_name": {
                String name = newValue.toString();
                tile.setName(name);
                RemAL.saveTile(tile);

                pref.setSummary(name);
                break;
            }
            case "tile_device": {
                String deviceName = newValue.toString();
                tile.setTargetDevice(RemAL.getDevice(deviceName));
                RemAL.saveTile(tile);

                pref.setSummary(deviceName);
                break;
            }
            case "tile_app": {
                String request = newValue.toString();
                tile.setRequest(request);
                tile.setRequestType("app");
                RemAL.saveTile(tile);

                pref.setSummary(request);
                break;
            }
            case "tile_type": {
                String type = newValue.toString();

                if(!type.equals(tile.getRequestType())) {
                    tile.setRequest("");
                    tile.setRequestType(type);
                    RemAL.saveTile(tile);

                    ListPreference p = (ListPreference)pref;
                    pref.setSummary(p.getEntries()[Arrays.asList(p.getEntryValues()).indexOf(type)]);
                }

                break;
            }
            case "details_path": case "details_macro": case "details_shell":
                String type = pref.getKey().substring(8);

                tile.setRequestType(type);
                tile.setRequest(newValue.toString());
                RemAL.saveTile(tile);

                ListPreference p = (ListPreference)findPreference("tile_type");
                p.setSummary(p.getEntries()[Arrays.asList(p.getEntryValues()).indexOf(type)]);
                break;
        }

        return false;
    }

    @Override
    public boolean onPreferenceClick(Preference pref) {
        switch(pref.getKey()) {
            case "tile_name": {
                String name = tile.getName();
                ((EditTextPreference)pref).setText(name.isEmpty() ? "" : name);
                break;
            }
            case "tile_device": {
                ListPreference p = (ListPreference)pref;

                IRemoteDevice[] devices = RemAL.getDevices();

                int length = devices.length;
                String[] entries = new String[length];

                for(int i = 0; i < length; i++)
                    entries[i] = devices[i].getName();

                p.setEntries(entries);
                p.setEntryValues(entries);
                break;
            }
            case "tile_app": {
                ListPreference p = (ListPreference)pref;

                String[] entries = new String[0];

                p.setEntries(entries);
                p.setEntryValues(entries);

                break;
            }
            case "details_path": case "details_macro": case "details_shell": {
                EditTextPreference p = (EditTextPreference)pref;

                if(tile.getRequestType().equals(pref.getKey().substring(8)))
                    p.setText(tile.getRequest());

                break;
            }
        }

        return true;
    }
}
