package project.snow;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

/**
 * DisplayOrdersDetailsListAdapter class.
 *
 * This class is responsible for showing on screen a List Adapter containing all orders of a specific business.
 * The phase when this happens, is when the currently working employee has already logged in and wants to select the order he/she wants to control (handle, process).
 *
 * So, the List Adapter in this case, adapts the dynamic list of orders created on DisplayOrdersDetailsActivity class on the screen and makes it selectable for the currently working employee.
 *
 * Additionally, this class identifies the order selected by the currently working employee.
 *
 * @author thanoskalantzis
 */
public class DisplayOrdersDetailsListAdapter extends RecyclerView.Adapter<DisplayOrdersDetailsListAdapter.ViewHolder> {
    //Class variables.
    public ArrayList<FinalOrder> finalOrdersList;
    public Business business;
    public Employee employee;
    public int afmEmployee;
    public ArrayList<Business> businessesList;

    /**
     * ViewHolder inner class.
     *
     * ViewHolder is an inner class which is responsible for identifying all elements (items) of the View, in which (elements) all the dynamic information of orders will be placed.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView displayNumberTable;
        public TextView displayDateTime;
        public TextView displayOrderStatus;
        public LinearLayout displayOrdersDetailsLinearLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            this.displayNumberTable = (TextView) itemView.findViewById(R.id.displayNumberTable);
            this.displayDateTime = (TextView) itemView.findViewById(R.id.displayDateTime);
            this.displayOrderStatus = (TextView) itemView.findViewById(R.id.displayOrderStatus);
            this.displayOrdersDetailsLinearLayout = (LinearLayout) itemView.findViewById(R.id.displayOrdersDetailsLinearLayout);
        }
    }

    //Constructor.
    public DisplayOrdersDetailsListAdapter(Employee employee, int afmEmployee, ArrayList<Business> businessesList, ArrayList<FinalOrder> finalOrdersList, Business business) {
        this.employee = employee;
        this.afmEmployee =afmEmployee;
        this.businessesList = businessesList;
        this.finalOrdersList = finalOrdersList;
        this.business=business;
    }

    /**
     * Function to identify and set further crucial elements.
     *
     * @param parent the parent out of which we will get some of our context.
     * @param viewType the view type.
     * @return ordersViewHolder the ViewHolder created inside the function below.
     */
    @Override
    public DisplayOrdersDetailsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater displayOrdersLayoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = displayOrdersLayoutInflater.inflate(R.layout.list_format_display_orders_details, parent, false);
        DisplayOrdersDetailsListAdapter.ViewHolder ordersViewHolder = new DisplayOrdersDetailsListAdapter.ViewHolder(listItem);
        ordersViewHolder.setIsRecyclable(false);
        return ordersViewHolder;
    }

    /**
     * The method below is responsible for placing all orders and some details of those orders into the corresponding elements declared earlier.
     *
     * @param holder the ViewHolder parsed.
     * @param position the position of order on screen.
     */
    @Override
    public void onBindViewHolder(DisplayOrdersDetailsListAdapter.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);

        final FinalOrder finalOrder = finalOrdersList.get(position);

        holder.displayNumberTable.setText("Τραπέζι: "+ finalOrder.getNumberTable());

        String[] dateTimeArray = finalOrder.getDateTime().split(" ");
        holder.displayDateTime.setText("Ώρα - Ημερομηνία: "+(dateTimeArray[1]).trim()+" - "+(dateTimeArray[0]).trim());

        holder.displayOrdersDetailsLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ControlOrderActivity.class);
                intent.putExtra("finalOrder", finalOrder);
                intent.putExtra("business", business);
                intent.putExtra("afm_employee", afmEmployee);
                intent.putExtra("businessesWorkingForList", businessesList);
                intent.putExtra("currentEmployee", employee);
                view.getContext().startActivity(intent);
                ((Activity)view.getContext()).finish();

            }
        });

        holder.displayOrderStatus.setText("Κατάσταση: "+ finalOrder.getOrderStatus());

        if(finalOrder.getIsActive()){
            holder.displayOrderStatus.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.green));
        }else{
            holder.displayOrderStatus.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.red));
        }

    }

    /**
     * The function below counts the size (number of elements) of the finalOrdersList.
     *
     * @return productsListSize which is the calculated size of finalOrdersList.
     */
    @Override
    public int getItemCount() {
        int productsListSize=0;
        if(finalOrdersList!=null && finalOrdersList.size()>0){
            productsListSize = finalOrdersList.size();
        }else{
            productsListSize=0;
        }
        return productsListSize;
    }

}