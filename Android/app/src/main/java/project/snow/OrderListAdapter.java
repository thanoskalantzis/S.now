package project.snow;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

/**
 * OrderListAdapter class.
 *
 * This class is responsible for showing on screen a List Adapter containing all order items.
 *
 * @author thanoskalantzis
 */
public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.ViewHolder> {
    //Class variables.
    public Business businessSelected;
    public ArrayList<OrderItem> ordersList;

    /**
     * ViewHolder inner class.
     *
     * ViewHolder is an inner class which is responsible for identifying all elements (items) of the View, in which (elements) the dynamic order details (information) will be placed.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView productNameForOrder;
        public TextView productDescriptionForOrder;
        public TextView oneProductPrice;
        public TextView quantityForOrder;
        public TextView priceOfOrderItem;
        private double doublePriceOfOrderItem;

        public ViewHolder(View itemView) {
            super(itemView);

            this.productNameForOrder = (TextView) itemView.findViewById(R.id.productNameForOrder);
            this.productDescriptionForOrder = (TextView) itemView.findViewById(R.id.productDescriptionForOrder);
            this.oneProductPrice = (TextView) itemView.findViewById(R.id.oneProductPrice);
            this.quantityForOrder = (TextView) itemView.findViewById(R.id.quantityForOrder);
            this.priceOfOrderItem = (TextView) itemView.findViewById(R.id.priceOfOrderItem);
        }
    }
    //Constructor.
    public OrderListAdapter(Business businessSelected, ArrayList<OrderItem> ordersList) {
        this.businessSelected=businessSelected;
        this.ordersList = ordersList;
    }

    /**
     * Function to identify and set further crucial elements.
     *
     * @param parent the parent out of which we will get some of our context.
     * @param viewType the view type.
     * @return ordersViewHolder the ViewHolder created inside the function below.
     */
    @Override
    public OrderListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater productsSelectionLayoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = productsSelectionLayoutInflater.inflate(R.layout.list_format_order, parent, false);
        OrderListAdapter.ViewHolder ordersViewHolder = new OrderListAdapter.ViewHolder(listItem);
        ordersViewHolder.setIsRecyclable(false);
        return ordersViewHolder;
    }

    /**
     * The method below is responsible for placing all information into the corresponding elements declared earlier.
     *
     * @param holder the ViewHolder parsed.
     * @param position the position of each order.
     */
    @Override
    public void onBindViewHolder(OrderListAdapter.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);

        final OrderItem orderItem = ordersList.get(position);

        holder.productNameForOrder.setText(orderItem.getProduct().getProductName());
        holder.productDescriptionForOrder.setText(orderItem.getProduct().getDescription());
        holder.oneProductPrice.setText(String.valueOf(orderItem.getProduct().getPrice() + "€"));
        holder.quantityForOrder.setText("x" + String.valueOf(orderItem.getQuantity()));

        holder.doublePriceOfOrderItem=0;
        holder.doublePriceOfOrderItem = (orderItem.getQuantity() * orderItem.getProduct().getPrice());

        //DecimalFormat decimalFormat = new DecimalFormat("###.##");
        //holder.doublePriceOfOrderItem = Double.parseDouble(decimalFormat.format(holder.doublePriceOfOrderItem));

        holder.priceOfOrderItem.setText("= " + String.valueOf(holder.doublePriceOfOrderItem) + "€");

    }

    @Override
    public int getItemCount() {
        int listSize=0;
        if(ordersList!=null && ordersList.size()>0){
            listSize = ordersList.size();
        }else{
            listSize=0;
        }
        return listSize;
    }

}