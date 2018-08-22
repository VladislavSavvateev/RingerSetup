package savok.ringersetup.model;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class RingList {
    private final ArrayList<ArrayList<Ring>> _rings;

    public RingList(InputStream is) throws Exception {
        _rings = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            ArrayList<Ring> ringList = new ArrayList<>();
            while (is.available() < 1);

            int count = is.read();
            for (int k = 0; k < count; k++)
                ringList.add(new Ring(is));
            _rings.add(ringList);
        }
    }

    public RingList(JSONArray jsonArray) throws JSONException{
        _rings = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            ArrayList<Ring> ringList = new ArrayList<>();
            JSONArray ringJson = jsonArray.getJSONArray(i);
            for (int k = 0; k < ringJson.length(); k++)
                ringList.add(new Ring(ringJson.getJSONObject(k)));
            _rings.add(ringList);
        }
    }

    public void upload(OutputStream os) throws IOException {
        for (int i = 0; i < 7; i++) {
            ArrayList<Ring> rings = _rings.get(i);
            os.write((byte) rings.size());
            for (Ring r: rings) r.upload(os);
        }
    }

    public JSONArray toJson() throws JSONException {
        JSONArray arr = new JSONArray();
        for (int i = 0; i < 7; i++) {
            JSONArray ringArr = new JSONArray();
            ArrayList<Ring> rings = _rings.get(i);
            for (Ring r: rings) ringArr.put(r.toJson());
            arr.put(ringArr);
        }

        return arr;
    }

    public ArrayList<ArrayList<Ring>> getList() {
        return _rings;
    }
}
