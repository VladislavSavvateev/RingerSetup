package savok.ringersetup;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Calendar;
import java.util.Locale;
import java.util.Scanner;

import savok.ringersetup.model.RingList;
import savok.ringersetup.widget.ConfAdapter;
import savok.ringersetup.widget.WeekdayAdapter;

public class MainActivity extends AppCompatActivity {
    private LinearLayout loadingLayout;
    static InputStream is;
    static OutputStream os;

    static RingList ringList;
    private RecyclerView weekdayRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadingLayout = findViewById(R.id.layout_loading);
        weekdayRecycler = findViewById(R.id.recycler_weekdays);

        new ConnectTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_clear:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.dialog_clear_all_rings_title)
                        .setMessage(R.string.dialog_clear_all_rings_message)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new ClearAllRings().execute();
                            }
                        }).setNegativeButton(android.R.string.no, null).create().show();
                break;
            case R.id.item_manual_mode:
                startActivity(new Intent(this, ManualActivity.class));
                break;
            case R.id.item_set_time:
                new SetTimeTask().execute();
                break;
            case R.id.item_upload:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.dialog_upload_warning_title)
                        .setMessage(R.string.dialog_upload_warning_message)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new UploadRingsTask().execute();
                            }
                        }).setNegativeButton(android.R.string.no, null).create().show();
                break;
            case R.id.item_open:
                showLoadConfDialog();
                break;
            case R.id.item_save:
                showSaveConfDialog();
                break;
        }
        return true;
    }

    private void showLoadConfDialog() {
        final ConfAdapter adapter = new ConfAdapter(this);
        if (adapter.isEmpty())
            Toast.makeText(this, R.string.text_there_is_no_confs, Toast.LENGTH_LONG).show();
        else {
            RecyclerView rv = new RecyclerView(this);
            LinearLayoutManager llm = new LinearLayoutManager(this);
            rv.setLayoutManager(llm);
            rv.addItemDecoration(new DividerItemDecoration(this, llm.getOrientation()));
            rv.setAdapter(adapter);

            final AlertDialog ad = new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_select_conf_title)
                    .setView(rv).create();
            adapter.setOnItemClickListener(new ConfAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(File f) {
                    try {
                        Scanner sc = new Scanner(openFileInput(f.getName()), "UTF-8");
                        String jsonStr = sc.nextLine();
                        sc.close();
                        ringList = new RingList(new JSONArray(jsonStr));

                        Toast.makeText(MainActivity.this, R.string.text_loading_was_successful, Toast.LENGTH_LONG).show();
                        ad.dismiss();
                    } catch (Exception ex) {
                        ex.printStackTrace();

                        Toast.makeText(MainActivity.this, R.string.text_loading_conf_error, Toast.LENGTH_LONG).show();
                    }
                }
            });
            adapter.setOnLongClickListener(new ConfAdapter.OnItemLongClickListener() {
                @Override
                public void onItemLongClick(final File f, final int pos) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle(R.string.dialog_delete_conf_title)
                            .setMessage(R.string.dialog_delete_conf_message)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (f.delete()) {
                                        adapter.remove(pos);
                                        adapter.notifyItemRemoved(pos);

                                        if (adapter.isEmpty()) {
                                            ad.dismiss();
                                            Toast.makeText(MainActivity.this, R.string.text_there_is_no_confs, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            }).setNegativeButton(android.R.string.no, null).create().show();
                }
            });
            ad.show();
        }
    }
    private void showSaveConfDialog() {
        final EditText et = new EditText(this);
        et.setHint(R.string.hint_conf_name);
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_type_conf_name)
                .setView(et).setPositiveButton(R.string.dialog_type_conf_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Calendar c = Calendar.getInstance();
                        try {
                            FileOutputStream fos = openFileOutput(String.format(Locale.getDefault(), "%s %d-%d-%d %d:%d:%d.rsf",
                                    et.getText().toString(),
                                    c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH), c.get(Calendar.YEAR),
                                    c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND)), MODE_PRIVATE);
                            fos.write(ringList.toJson().toString().getBytes("UTF-8"));
                            fos.close();

                            Toast.makeText(MainActivity.this, R.string.text_saving_was_successful, Toast.LENGTH_LONG).show();
                        } catch (Exception ex) {
                            ex.printStackTrace();

                            Toast.makeText(MainActivity.this, R.string.text_saving_conf_error, Toast.LENGTH_LONG).show();
                        }
                    }
                }).setNeutralButton(android.R.string.cancel, null).create().show();
    }

    private class ConnectTask extends AsyncTask<Void, Void, Socket> {
        @Override
        protected Socket doInBackground(Void... voids) {
            try {
                return new Socket("192.168.4.1", 1488);
            } catch (Exception ex) { ex.printStackTrace(); }
            return null;
        }

        @Override
        protected void onPostExecute(Socket socket) {
            if (socket == null) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.dialog_socket_unavailable_title)
                        .setMessage(R.string.dialog_socket_unavailable_message)
                        .setPositiveButton(R.string.dialog_socket_unavailable_positive, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startActivity(new Intent(Settings.ACTION_SETTINGS));
                                finish();
                            }
                        }).setNegativeButton(R.string.dialog_socket_unavailable_negative, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new ConnectTask().execute();
                            }
                        }).setNeutralButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) { finish(); }
                        }).create().show();
            } else try {
                is = socket.getInputStream();
                os = socket.getOutputStream();
                new GetAllRings().execute();
            } catch (Exception ex) {
                ex.printStackTrace();

                Toast.makeText(MainActivity.this, R.string.dialog_socket_unavailable_title, Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private class GetAllRings extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                os.write(0x01);
                os.flush();

                while (is.available() < 1);
                if (is.read() != 0x01) return false;

                ringList = new RingList(is);
                return true;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            if (bool == null) {
                finish();
                return;
            }
            if (bool) {
                loadingLayout.animate().setDuration(1000).alpha(0).start();
                weekdayRecycler.animate().setDuration(1000).alpha(1).start();
                LinearLayoutManager llm = new LinearLayoutManager(MainActivity.this);
                weekdayRecycler.setLayoutManager(llm);
                weekdayRecycler.addItemDecoration(new DividerItemDecoration(MainActivity.this, llm.getOrientation()));
                WeekdayAdapter wa = new WeekdayAdapter(MainActivity.this, ringList.getList());
                wa.setOnItemClickListener(new WeekdayAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position, WeekdayAdapter.ViewHolder vh) {
                        Intent i = new Intent(MainActivity.this, WeekdayActivity.class);
                        i.putExtra(WeekdayActivity.EXTRA_POSITION, position);
                        i.putExtra(WeekdayActivity.EXTRA_WEEKDAY_NAME, vh.textView.getText().toString());
                        startActivity(i);
                    }
                });
                weekdayRecycler.setAdapter(wa);
            } else {
                Toast.makeText(MainActivity.this, "error", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private class ClearAllRings extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                os.write(0x02);
                os.flush();

                return is.read() == 0x01;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean == null) finish();
            else Toast.makeText(MainActivity.this,
                    aBoolean ? R.string.text_clearings_was_successful : R.string.text_error_was_occurred,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private class SetTimeTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                Calendar c = Calendar.getInstance();
                os.write(0x05);

                os.write(c.get(Calendar.HOUR_OF_DAY));
                os.write(c.get(Calendar.MINUTE));
                os.write(c.get(Calendar.SECOND));
                os.write(c.get(Calendar.DAY_OF_MONTH));
                os.write(c.get(Calendar.MONTH) + 1);
                int year = c.get(Calendar.YEAR);
                os.write(year >> 8);
                os.write(year & 255);
                os.flush();

                return is.read() == 0x01;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean == null) finish();
            else Toast.makeText(MainActivity.this,
                    aBoolean ? R.string.text_time_was_changed : R.string.text_error_was_occurred,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private class UploadRingsTask extends AsyncTask<Void, Void, Boolean> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(MainActivity.this);
            pd.setTitle(R.string.dialog_please_wait_title);
            pd.show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                os.write(0x06);
                ringList.upload(os);

                while (is.available() < 1);
                return is.read() == 0x01;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            pd.dismiss();
            Toast.makeText(MainActivity.this, aBoolean != null ? R.string.text_upload_was_successful : R.string.text_error_was_occurred, Toast.LENGTH_LONG).show();
        }
    }
}
