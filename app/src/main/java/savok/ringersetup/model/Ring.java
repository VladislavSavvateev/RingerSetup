package savok.ringersetup.model;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Ring implements Comparable<Ring> {

    private boolean enabled;
    private byte hour;
    private byte minute;

    public Ring(InputStream is) throws Exception {
        while (is.available() < 3);

        enabled = is.read() == 0x01;
        hour = (byte) is.read();
        minute = (byte) is.read();
    }
    public Ring(boolean enabled, byte hour, byte minute) {
        setEnabled(enabled);
        setHour(hour);
        setMinute(minute);
    }
    public Ring(JSONObject json) throws JSONException{
        setEnabled(json.getBoolean("e"));
        setHour((byte) json.getInt("h"));
        setMinute((byte) json.getInt("m"));
    }

    public void setHour(byte hour) { this.hour = hour; }
    public void setMinute(byte minute) { this.minute = minute; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public byte getHour() { return hour; }
    public byte getMinute() { return minute; }
    public boolean getEnabled() { return enabled; }

    public Ring copyTo() {
        return new Ring(enabled, hour, minute);
    }

    public void upload(OutputStream os) throws IOException {
        os.write(enabled ? 0x01: 0x00);
        os.write(hour);
        os.write(minute);
    }
    public JSONObject toJson() throws JSONException{
        JSONObject json = new JSONObject();
        json.put("e", enabled);
        json.put("h", hour);
        json.put("m", minute);

        return json;
    }

    @Override
    public int compareTo(@NonNull Ring ring) {
        if (hour == ring.hour) {
            if (minute == ring.minute) return 0;
            else return minute > ring.minute ? 1 : -1;
        } else return hour > ring.hour ? 1 : -1;
    }
}
