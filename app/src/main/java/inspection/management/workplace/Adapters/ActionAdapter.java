package inspection.management.workplace.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import inspection.management.workplace.R;
import inspection.management.workplace.activities.ActionDetail;
import inspection.management.workplace.utils.ActionSiteWork;
import inspection.management.workplace.utils.NotificationSiteWork;

public class ActionAdapter extends RecyclerView.Adapter<ActionAdapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<ActionSiteWork> actionSiteWorkArrayList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public ActionAdapter(Context context, ArrayList<ActionSiteWork> siteWorkArrayList) {
        mContext = context;
        actionSiteWorkArrayList = siteWorkArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.action_design, parent, false);
        return new MyViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        ActionSiteWork currentItem = actionSiteWorkArrayList.get(i);

        if (currentItem.getStatus().equalsIgnoreCase("ok")){
            myViewHolder.actionBtn.setBackgroundColor(mContext.getResources().getColor(R.color.green));
            myViewHolder.actionBtn.setText("Checked");
            myViewHolder.ac_con.setBackgroundResource(R.drawable.stycheck);
        }
        else {
            myViewHolder.actionBtn.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
            myViewHolder.actionBtn.setText("New");
        }
        String action = currentItem.getTopic_name();
        String location = currentItem.getLocation();
        String s_date = currentItem.getStart_date();
        String e_date = currentItem.getEnd_date();
         myViewHolder.action.setText(action);
         myViewHolder.location.setText(location);
         myViewHolder.s_date.setText(s_date);
         myViewHolder.e_data.setText(e_date);
         myViewHolder.actionBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 ActionSiteWork selectedItem = actionSiteWorkArrayList.get(i);
                 Intent intent = new Intent(mContext, ActionDetail.class);
                 intent.putExtra("action",selectedItem.getAction());
                 intent.putExtra("location",selectedItem.getLocation());
                 intent.putExtra("employee_name",selectedItem.getEmployee_name());
                 intent.putExtra("start_date",selectedItem.getStart_date());
                 intent.putExtra("end_date",selectedItem.getEnd_date());
                 intent.putExtra("status",selectedItem.getStatus());
                 intent.putExtra("topic",selectedItem.getTopic());
                 intent.putExtra("id",selectedItem.getAction_id());
                 intent.putExtra("topic_id",selectedItem.getTopic_id());
                 intent.putExtra("topic_name",selectedItem.getTopic_name());
                 mContext.startActivity(intent);
             }
         });

    }

    @Override
    public int getItemCount() {
        return actionSiteWorkArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView action;
        private TextView s_date,e_data;
        private TextView location;
        private ConstraintLayout ac_con;
        private TextView mCurrent_StatusTextViewMember;
        private RelativeLayout allViewClickableLayout;
        private Button actionBtn;
        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            action = itemView.findViewById(R.id.action_title);
            s_date = itemView.findViewById(R.id.action_startDate);
            e_data = itemView.findViewById(R.id.action_endDate);
            location = itemView.findViewById(R.id.action_Location);
            actionBtn = itemView.findViewById(R.id.actionBtn);
            ac_con = itemView.findViewById(R.id.ac_con);

        }

    }
}

