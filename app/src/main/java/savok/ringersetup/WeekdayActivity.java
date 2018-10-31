package savok.ringersetup;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Collections;

import savok.ringersetup.model.Ring;
import savok.ringersetup.widget.CopyAdapter;
import savok.ringersetup.widget.RingAdapter;

public class WeekdayActivity extends AppCompatActivity {
    public final static String EXTRA_POSITION = "position";
    public final static String EXTRA_WEEKDAY_NAME = "weekday_name";

    private RingAdapter ringAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekday);
        ActionBar ab = getSupportActionBar();
        if (ab != null) ab.setTitle(getIntent().getStringExtra(EXTRA_WEEKDAY_NAME));

        ringAdapter = new RingAdapter(this, MainActivity.ringList.getList().get(getIntent().getIntExtra(EXTRA_POSITION, 0)));
        ringAdapter.setOnClickListener(new RingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Ring ring, RingAdapter.ViewHolder vh) {
                ring.setEnabled(!ring.getEnabled());
                vh.textView.setChecked(ring.getEnabled());
            }
        });
        ringAdapter.setOnLongClickListener(new RingAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(final Ring ring) {
                new AlertDialog.Builder(WeekdayActivity.this).setTitle(R.string.dialog_change_ring_title)
                        .setMessage(R.string.dialog_change_ring_message)
                        .setPositiveButton(R.string.dialog_change_ring_positive, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            new TimePickerDialog(WeekdayActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                                    ring.setHour((byte) hour);
                                    ring.setMinute((byte) minute);
                                    Collections.sort(ringAdapter.getRings());
                                    ringAdapter.notifyDataSetChanged();
                                }
                            }, ring.getHour(), ring.getMinute(), true).show();
                            }
                        }).setNegativeButton(R.string.dialog_change_ring_negative, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ringAdapter.getRings().remove(ring);
                                ringAdapter.notifyDataSetChanged();
                            }
                        }).setNeutralButton(android.R.string.cancel, null).create().show();
            }
        });


        RecyclerView ringRecycler = findViewById(R.id.recycler_rings);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        ringRecycler.setLayoutManager(llm);
        ringRecycler.addItemDecoration(new DividerItemDecoration(this, llm.getOrientation()));
        ringRecycler.setAdapter(ringAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_weekday, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_add:
                new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        ArrayList<Ring> rings = ringAdapter.getRings();
                        rings.add(new Ring(true, (byte) hour, (byte) minute));
                        Collections.sort(rings);

                        ringAdapter.notifyDataSetChanged();
                    }
                }, 12, 0, true).show();
                break;
            case R.id.item_copy_from:
                showCopyFromDialog();
                break;
            case R.id.item_delete_all:
                showDeleteAllDialog();
                break;
        }
        return true;
    }

    private void showCopyFromDialog() {
        CopyAdapter ca = new CopyAdapter(this, getIntent().getIntExtra(EXTRA_POSITION, 0));
        RecyclerView rv = new RecyclerView(this);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.addItemDecoration(new DividerItemDecoration(this, llm.getOrientation()));
        rv.setAdapter(ca);
        final AlertDialog ad = new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_copy_from_title)
                .setView(rv).setNeutralButton(android.R.string.cancel, null).create();
        ca.setOnItemClickListener(new CopyAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int offset) {
                ad.dismiss();

                ringAdapter.clear();
                ArrayList<Ring> from = MainActivity.ringList.getList().get(offset);
                for (Ring r: from) ringAdapter.add(r.copyTo());
                ringAdapter.notifyDataSetChanged();
            }
        });
        ad.show();
    }

    private void showDeleteAllDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_delete_all_title)
                .setMessage(R.string.dialog_delete_all_message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ringAdapter.clear();
                        ringAdapter.notifyDataSetChanged();
                    }
                }).setNegativeButton(android.R.string.no, null).create().show();
    }
}
