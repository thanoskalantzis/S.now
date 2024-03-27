package project.snow;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

/**
 * BusinessSelectionListAdapter class.
 *
 * This class is responsible for showing on screen a List Adapter containing all registered businesses.
 * The phase when this happens, is when the customer has already logged in/registered and wants to select the business which he/she is currently on, so as to place his/her order.
 *
 * So, the List Adapter in this case, adapts the dynamic list of businesses created on BusinessSelectionActivity class on the screen and makes it selectable for the customer.
 *
 * Additionally, this class identifies the business selected by the customer.
 *
 * @author thanoskalantzis
 */
public class BusinessSelectionListAdapter extends RecyclerView.Adapter<BusinessSelectionListAdapter.ViewHolder> implements Filterable{
    //Class variables.
    private ArrayList<Business> businessesList, businessesListFull;
    private static Customer currentCustomer;

    /**
     * ViewHolder inner class.
     *
     * ViewHolder is an inner class which is responsible for identifying all elements (items) of the View, in which (elements) the dynamic business information will be placed.
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

    //Constructor.
    public BusinessSelectionListAdapter(Customer customer, ArrayList<Business> businessesList) {
        this.currentCustomer = customer;
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
     * @return businessSelectionViewHolder the ViewHolder created inside the function below.
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater businessSelectionLayoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = businessSelectionLayoutInflater.inflate(R.layout.list_format_business_selection, parent, false);
        ViewHolder businessSelectionViewHolder = new ViewHolder(listItem);
        return businessSelectionViewHolder;
    }

    /**
     * The method below is responsible for placing all information into the corresponding elements declared earlier.
     *
     * It is also responsible to identify the business selected when the customer touches the screen.
     *
     * @param holder the ViewHolder parsed.
     * @param position the position of the business selected by the customer.
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Business business = businessesList.get(position);
        holder.businessNameTextView.setText(businessesList.get(position).getBusinessIdName());
        holder.businessDetailsTextView.setText(businessesList.get(position).getAddress()+", "+businessesList.get(position).getPostalCode()+", "+businessesList.get(position).getPhone());
        holder.businessSelectionRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                Toast myToast = Toast.makeText(view.getContext(),"Επιλέχθηκε: "+business.toString(),Toast.LENGTH_SHORT);
                View myToastView = myToast.getView();

                //Gets the actual oval background of the Toast then sets the colour filter.
                myToastView.getBackground().setColorFilter(myToastView.getContext().getResources().getColor(R.color.lime), PorterDuff.Mode.SRC_IN);

                //Gets the TextView from the Toast so it can be edited.
                TextView myTextView = myToastView.findViewById(android.R.id.message);
                myTextView.setTextColor(myToastView.getContext().getResources().getColor(R.color.black));

                myToast.show();
                */

                //Whenever the customer touches the screen the program figures it out.
                //If customer touches the screen to select a particular business, then the program will figure this out too.
                //In this case, a new activity will be created in order for the customer to make (place) an order.
                //The activity which will be created corresponds to the ProductsSelectionActivity class.
                Intent intent = new Intent(view.getContext(), ProductsSelectionActivity.class);
                //Parsing some crucial information to the next activity about to be started.
                intent.putExtra("business", business);
                intent.putExtra("customer", currentCustomer);
                view.getContext().startActivity(intent);
            }
        });
    }

    //Making the businesses list filterable so as the customer to be able to search for the business he/she wants to order from based on business name.
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

    /**
     * The function below counts the size (number of elements) of the businesses list.
     *
     * @return businessesListSize which is the size of the current list containing all registered businesses (or no business at all).
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

    /**
     * The following function is responsible for returning the Filter described above.
     *
     * @return filter which is the search-business-by-name filter.
     */
    @Override
    public Filter getFilter() {
        return filter;
    }
}