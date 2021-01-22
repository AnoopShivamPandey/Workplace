package inspection.management.workplace.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import inspection.management.workplace.R;
import inspection.management.workplace.activities.ActionDetail;
import inspection.management.workplace.activities.InspectionDetail;
import inspection.management.workplace.utils.InspectionSiteWork;

public class InpectionAdapter extends RecyclerView.Adapter<InpectionAdapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<InspectionSiteWork> inspectionSiteWorkArrayList;
    private OnItemClickListener mListener;
    public interface OnItemClickListener {
        void onItemClick(int position);

    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
    public InpectionAdapter(Context context, ArrayList<InspectionSiteWork> siteWorkArrayList) {
        mContext = context;
        inspectionSiteWorkArrayList = siteWorkArrayList;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.inpectionlistdesign, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        InspectionSiteWork currentItem = inspectionSiteWorkArrayList.get(i);
        if (currentItem.getStatus().equalsIgnoreCase("0")) {
            if (currentItem.getQuestion_color().equalsIgnoreCase("")) {
                myViewHolder.actionBtn.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
            } else {
                myViewHolder.actionBtn.setBackgroundColor(Color.parseColor(currentItem.getQuestion_color()));
                myViewHolder.actionBtn.setText("Checked");
                myViewHolder.ins_con.setBackgroundResource(R.drawable.stycheck);
            }
        }
        else {
            myViewHolder.actionBtn.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
            myViewHolder.actionBtn.setText("New");
        }
        String project = currentItem.getTopic();
        String location = currentItem.getLocation();
        String s_date = currentItem.getStart_date();
        String e_date = currentItem.getEnd_date();
         myViewHolder.topic_name.setText(project);
         myViewHolder.location.setText(location);
         myViewHolder.s_date.setText(s_date);
         myViewHolder.e_data.setText(e_date);
         myViewHolder.actionBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 InspectionSiteWork selectedItem = inspectionSiteWorkArrayList.get(i);
                 Intent intent = new Intent(mContext, InspectionDetail.class);
                 intent.putExtra("Project",selectedItem.getProject());
                 intent.putExtra("location",selectedItem.getLocation());
                 intent.putExtra("employee_name",selectedItem.getEmployee_name());
                 intent.putExtra("start_date",selectedItem.getStart_date());
                 intent.putExtra("end_date",selectedItem.getEnd_date());
                 intent.putExtra("status",selectedItem.getStatus());
                 intent.putExtra("topic",selectedItem.getTopic());
                 intent.putExtra("ins_id",selectedItem.getInspection_id());
                 intent.putExtra("topic_id",selectedItem.getTopic_id());
                 intent.putExtra("is_delete",selectedItem.getIs_delete());
                 mContext.startActivity(intent);
             }
         });

    }

    @Override
    public int getItemCount() {
        return inspectionSiteWorkArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView topic_name;
        private TextView s_date,e_data;
        private TextView location;
        private RelativeLayout ins_con;
        private TextView mCurrent_StatusTextViewMember;
        private RelativeLayout allViewClickableLayout;
        private Button actionBtn;
        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            topic_name = itemView.findViewById(R.id.textView2);
            s_date = itemView.findViewById(R.id.startDate);
            e_data = itemView.findViewById(R.id.endDate);
            location = itemView.findViewById(R.id.Location);
            actionBtn = itemView.findViewById(R.id.button);
            ins_con = itemView.findViewById(R.id.ins_con);

        }

    }
}

