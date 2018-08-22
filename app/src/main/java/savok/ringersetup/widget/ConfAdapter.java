package savok.ringersetup.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;

import ru.kollad.oxygen.widget.ArrayRecyclerAdapter;

public class ConfAdapter extends ArrayRecyclerAdapter<File, ConfAdapter.ViewHolder> {
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    public ConfAdapter(Context context) {
        super(context, getAllConfs(context));
    }

    private static ArrayList<File> getAllConfs(Context context) {
        File dir = context.getFilesDir();
        return new ArrayList<>(Arrays.asList(dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                return s.endsWith(".rsf");
            }
        })));
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    public void setOnLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final File f = getItem(position);
        String name = f.getName();
        holder.textView.setText(name.substring(0, name.lastIndexOf('.')));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) onItemClickListener.onItemClick(f);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (onItemLongClickListener != null) onItemLongClickListener.onItemLongClick(f, holder.getAdapterPosition());
                return true;
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView textView;

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
        void onItemClick(File f);
    }
    public interface OnItemLongClickListener {
        void onItemLongClick(File f, int pos);
    }
}
