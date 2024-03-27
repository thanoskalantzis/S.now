package project.snow;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * WorkSelectionActivityListAdapter class.
 *
 * This class is responsible for showing on screen a List Adapter containing all workplaces.
 * The phase when this happens, is when the employee has already logged in and wants to select the workplace for which he/she is currently working for.
 *
 * So, the List Adapter in this case, adapts the dynamic list of workplaces created on WorkSelectionActivity class on the screen and makes it selectable for the employee.
 *
 * Additionally, this class identifies the workplace selected by the employee.
 *
 * @author thanoskalantzis
 */
public class WorkSelectionActivityListAdapter extends RecyclerView.Adapter<WorkSelectionActivityListAdapter.ViewHolder>{
    //CLass variables.
    private ArrayList<Business> businessesList, businessesListFull;
    private static Employee currentEmployee;
    private static int afmEmployee;

    /**
     * ViewHolder inner class.
     *
     * ViewHolder is an inner class which is responsible for identifying all elements (items) of the View, in which (elements) the dynamic workplaces information will be placed.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView businessNameTextView;
        public TextView businessDetailsTextView;
        public RelativeLayout businessSelectionRelativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            this.businessNameTextView = (TextView) itemView.findViewById(R.id.businessNameTextView);
            this.businessDetailsTextView = (TextView) itemView.findViewById(R.id.businessDetailsTextView);
            businessSelectionRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);
        }
    }

    //CONSTRUCTOR.
    public WorkSelectionActivityListAdapter(Employee currentEmployee, int afm, ArrayList<Business> businessesList) {
        this.currentEmployee = currentEmployee;
        this.afmEmployee = afm;
        this.businessesList = businessesList;
        try {
            businessesListFull = new ArrayList<>(businessesList);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Function to identify and set further crucial elements.
     *
     * @param parent the parent out of which we will get some of our context.
     * @param viewType the view type.
     * @return workSelectionViewHolder the ViewHolder created inside the function below.
     */
    @Override
    public WorkSelectionActivityListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater workSelectionLayoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = workSelectionLayoutInflater.inflate(R.layout.list_format_business_selection, parent, false);
        WorkSelectionActivityListAdapter.ViewHolder workSelectionViewHolder = new WorkSelectionActivityListAdapter.ViewHolder(listItem);
        return workSelectionViewHolder;
    }

    /**
     * The method below is responsible for placing all information into the corresponding elements declared earlier.
     *
     * It is also responsible to identify the workplace selected when the employee touches the screen.
     *
     * @param holder the ViewHolder parsed.
     * @param position the position of the workplace selected by the employee.
     */
    @Override
    public void onBindViewHolder(WorkSelectionActivityListAdapter.ViewHolder holder, final int position) {
        final Business business = businessesList.get(position);
        holder.businessNameTextView.setText(businessesList.get(position).getBusinessIdName());
        holder.businessDetailsTextView.setText(businessesList.get(position).getAddress()+", "+businessesList.get(position).getPostalCode()+", "+businessesList.get(position).getPhone());
        holder.businessSelectionRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Business businessSelected = businessesList.get(position);
                Intent intent = new Intent(view.getContext(), DisplayOrdersDetailsActivity.class);
                intent.putExtra("Business", businessSelected);
                intent.putExtra("businessesWorkingForList", businessesList);
                intent.putExtra("currentEmployee", currentEmployee);
                intent.putExtra("afm_employee", afmEmployee);
                view.getContext().startActivity(intent);
                ((Activity)view.getContext()).finish();
            }
        });
    }

    /*
    Filter filter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Business> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(businessesListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Business item : businessesListFull) {
                    if (item.getBusinessIdName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            businessesList.clear();
            businessesList.addAll( (ArrayList<Business>) results.values );
            notifyDataSetChanged();
        }


    };

     @Override
    public Filter getFilter() {
        return filter;
    }
     */

    /**
     * The function below counts the size (number of elements) of the workplaces list.
     *
     * @return businessesListSize which is the size of the current list containing all registered workplaces (or no workplace at all).
     */
    @Override
    public int getItemCount() {
        int businessesListSize=0;
        if(businessesList!=null && businessesList.size()>0){
            businessesListSize = businessesList.size();
        }else{
            businessesListSize=0;
        }
        return businessesListSize;
    }


}