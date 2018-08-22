package savok.ringersetup.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ru.kollad.oxygen.widget.ArrayRecyclerAdapter;
import savok.ringersetup.model.Ring;

public class WeekdayAdapter extends ArrayRecyclerAdapter<ArrayList<Ring>, WeekdayAdapter.ViewHolder> {

    private OnItemClickListener listener;

    public WeekdayAdapter(Context context, List<ArrayList<Ring>> list) {
        super(context, list);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        DateFormatSymbols dfs = DateFormatSymbols.getInstance(Locale.getDefault());
        String weekday = dfs.getWeekdays()[position != 6 ? position + 2 : 1];
        weekday = Character.toString(weekday.charAt(0)).toUpperCase() + weekday.substring(1);
        holder.textView.setText(weekday);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) listener.onItemClick(holder.getAdapterPosition(), holder);
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView textView;

        ViewHolder(ViewGroup container) {
            super(getLayoutInflater().inflate(android.R.layout.simple_list_item_1, container, false));

            textView = itemView.findViewById(android.R.id.text1);

            int[] attrs = new int[] { android.R.attr.selectableItemBackground};
            TypedArray ta = getContext().obtainStyledAttributes(attrs);
            itemView.setBackground(ta.getDrawable(0));
            ta.recycle();
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, ViewHolder vh);
    }
}
