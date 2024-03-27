package project.snow;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

/**
 * ControlOrderListAdapter class.
 *
 * This class is responsible for showing on screen a List Adapter containing all order items and details about the specified order.
 * The phase when this happens, is when the currently working employee has already logged in, has chosen a specific order to control (handle, process) and it is time to process it.
 *
 * So, the List Adapter in this case, adapts the dynamic list of OrderItems created on ControlOrderActivity class on the screen and makes it (namely the entire order) editable for the currently working employee.
 *
 * @author thanoskalantzis
 */
public class ControlOrderListAdapter extends RecyclerView.Adapter<ControlOrderListAdapter.ViewHolder> {
    //Class variables.
    public ArrayList<OrderItem> myList;

    /**
     * ViewHolder inner class.
     *
     * ViewHolder is an inner class which is responsible for identifying all elements (items) of the View, in which (elements) dynamic order details will be placed.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView controlOrderProductName;
        public TextView controlOrderProductDescription;
        public TextView controlOrderOneProductPrice;
        public TextView controlOrderQuantity;
        public TextView controlOrderPriceOfOrderItem;
        public TextView controlOrderFinalPrice;

        public ViewHolder(View itemView) {
            super(itemView);

            this.controlOrderProductName = (TextView) itemView.findViewById(R.id.controlOrderProductName);
            this.controlOrderProductDescription = (TextView) itemView.findViewById(R.id.controlOrderProductDescription);
            this.controlOrderOneProductPrice = (TextView) itemView.findViewById(R.id.controlOrderOneProductPrice);
            this.controlOrderQuantity = (TextView) itemView.findViewById(R.id.controlOrderQuantity);
            this.controlOrderPriceOfOrderItem = (TextView) itemView.findViewById(R.id.controlOrderPriceOfOrderItem);
        }
    }

    //Constructor.
    public ControlOrderListAdapter(ArrayList<OrderItem> myList) {
        this.myList=myList;
    }

    /**
     * Function to identify and set further crucial elements.
     *
     * @param parent the parent out of which we will get some of our context.
     * @param viewType the view type.
     * @return controlOrderViewHolder the ViewHolder created inside the function below.
     */
    @Override
    public ControlOrderListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater controlOrderLayoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = controlOrderLayoutInflater.inflate(R.layout.list_format_control_order, parent, false);
        ControlOrderListAdapter.ViewHolder controlOrderViewHolder = new ControlOrderListAdapter.ViewHolder(listItem);
        controlOrderViewHolder.setIsRecyclable(false);
        return controlOrderViewHolder;
    }

    /**
     * The method below is responsible for placing all information into the corresponding elements declared earlier.
     *
     * @param holder the ViewHolder parsed.
     * @param position the position of the order details.
     */
    @Override
    public void onBindViewHolder(ControlOrderListAdapter.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);

        final OrderItem item = myList.get(position);

        holder.controlOrderProductName.setText(item.getProduct().getProductName());
        holder.controlOrderProductDescription.setText(item.getProduct().getDescription());
        holder.controlOrderOneProductPrice.setText(String.valueOf(item.getProduct().getPrice() + "€"));
        holder.controlOrderQuantity.setText("x" + String.valueOf(item.getQuantity()));

        double priceOfOrderItem = (item.getQuantity()*item.getProduct().getPrice());

        holder.controlOrderPriceOfOrderItem.setText( "= " + String.valueOf(priceOfOrderItem) + "€" );
    }

    /**
     * The function below counts the size (number of elements) of the myList list.
     *
     * @return myList.size() which is the size of the current list containing all order items.
     */
    @Override
    public int getItemCount() {
        /*
        int myListSize=0;
        if(myList!=null && myList.size()>0){
            myListSize = myList.size();
        }else{
            myListSize=0;
        }
        return myListSize;

         */
        return myList.size();
    }

}