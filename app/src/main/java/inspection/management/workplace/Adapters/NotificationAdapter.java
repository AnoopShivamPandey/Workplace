package inspection.management.workplace.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import inspection.management.workplace.R;
import inspection.management.workplace.utils.NotificationSiteWork;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<NotificationSiteWork> notificationSiteWorkArrayList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public NotificationAdapter(Context context, ArrayList<NotificationSiteWork> siteWorkArrayList) {
        mContext = context;
        notificationSiteWorkArrayList = siteWorkArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.notificationdesign, parent, false);
        return new MyViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
       NotificationSiteWork currentItem = notificationSiteWorkArrayList.get(i);
        String notification = currentItem.getNotification();
        String notification_id = currentItem.getNotification_id();
        String date = currentItem.getSent_date();
        myViewHolder.notification.setText(notification);
        myViewHolder.date.setText(date);
    }

    @Override
    public int getItemCount() {
        return notificationSiteWorkArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView notification;
        private TextView date;
        private TextView mRegnoTextViewMember;
        private TextView mMobileTextViewMember;
        private TextView mCurrent_StatusTextViewMember;
        private RelativeLayout allViewClickableLayout;
        private ImageView call_dialog;
        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            notification = itemView.findViewById(R.id.notification_name_txt_view_id);
            date = itemView.findViewById(R.id.date_text);
        }

    }
}

