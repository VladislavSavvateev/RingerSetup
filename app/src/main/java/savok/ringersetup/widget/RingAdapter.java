package savok.ringersetup.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import java.util.ArrayList;

import ru.kollad.oxygen.widget.ArrayRecyclerAdapter;
import savok.ringersetup.model.Ring;

public class RingAdapter extends ArrayRecyclerAdapter<Ring, RingAdapter.ViewHolder> {
    private final ArrayList<Ring> rings;

    private OnItemClickListener clickListener;
    private OnItemLongClickListener longClickListener;

    public RingAdapter(Context context, ArrayList<Ring> list) {
        super(context, list);

        rings = list;
    }

    public ArrayList<Ring> getRings() { return rings; }

    public void setOnClickListener(OnItemClickListener onClickListener) {
        this.clickListener = onClickListener;
    }

    public void setOnLongClickListener(OnItemLongClickListener onLongClickListener) {
        this.longClickListener = onLongClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Ring r = getItem(position);
        holder.textView.setText(String.format("%s:%s", r.getHour() < 10 ? "0" + String.valueOf(r.getHour()) : String.valueOf(r.getHour()),
                r.getMinute() < 10 ? "0" + String.valueOf(r.getMinute()) : String.valueOf(r.getMinute())));
        holder.textView.setChecked(r.getEnabled());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickListener != null) clickListener.onItemClick(r, holder);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (longClickListener != null) longClickListener.onItemLongClick(r);
                return true;
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final CheckedTextView textView;

        ViewHolder(ViewGroup container) {
            super(getLayoutInflater().inflate(android.R.layout.simple_list_item_multiple_choice, container, false));
            textView = itemView.findViewById(android.R.id.text1);

            int[] attrs = new int[] { android.R.attr.selectableItemBackground};
            TypedArray ta = getContext().obtainStyledAttributes(attrs);
            itemView.setBackground(ta.getDrawable(0));
            ta.recycle();
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Ring ring, ViewHolder vh);
    }
    public interface OnItemLongClickListener {
        void onItemLongClick(Ring ring);
    }
}
