package project.snow;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

/**
 * ProductsSelectionListAdapter class.
 *
 * This class is responsible for showing on screen a List Adapter containing all registered products.
 * The phase when this happens, is when the customer has already chosen the business he/she wants to order from and wants to select and place products from the available list to his/her basket.
 *
 * So, the List Adapter in this case, adapts the dynamic list of products created on ProductsSelectionActivity class on the screen and makes it selectable for the customer.
 *
 * Additionally, this class identifies the plus and minus buttons pressed by the customer.
 *
 * @author thanoskalantzis
 */
public class ProductsSelectionListAdapter extends RecyclerView.Adapter<ProductsSelectionListAdapter.ViewHolder> {
    //Class variables.
    private static Customer currentCustomer;
    public Business businessSelected;
    public static ArrayList<OrderItem> productsSelectionListItems;
    public static ArrayList<OrderItem> allSelectedItemsList;
    public static int totalQuantity;
    public static Button basketButton;

    /**
     * ViewHolder inner class.
     *
     * ViewHolder is an inner class which is responsible for identifying all elements (items) of the View, in which (elements) the dynamic product information will be placed.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView productName;
        public TextView productDescription;
        public TextView productPrice;
        public Button plusButton;
        public Button minusButton;

        public int quantity;

        public ViewHolder(View itemView) {
            super(itemView);

            this.productName = (TextView) itemView.findViewById(R.id.productName);
            this.productDescription = (TextView) itemView.findViewById(R.id.productDescription);
            this.productPrice = (TextView) itemView.findViewById(R.id.productPrice);

            plusButton = (Button) itemView.findViewById(R.id.plusButton);
            plusButton.setBackgroundResource(R.drawable.ic_plus);

            minusButton = (Button) itemView.findViewById(R.id.minusButton);
            minusButton.setBackgroundResource(R.drawable.ic_minus);

            totalQuantity=0;
        }
    }
    //Constructor.
    public ProductsSelectionListAdapter(Customer customer, Business businessSelected, Button basketButton) {
        this.currentCustomer = customer;
        this.businessSelected=businessSelected;
        this.basketButton=basketButton;

        this.allSelectedItemsList = new ArrayList<OrderItem>();
    }


    /**
     * Function to identify and set further crucial elements.
     *
     * @param parent the parent out of which we will get some of our context.
     * @param viewType the view type.
     * @return productsSelectionViewHolder the ViewHolder created inside the function below.
     */
    @Override
    public ProductsSelectionListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater productsSelectionLayoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = productsSelectionLayoutInflater.inflate(R.layout.list_format_products_selection, parent, false);
        ProductsSelectionListAdapter.ViewHolder productsSelectionViewHolder = new ProductsSelectionListAdapter.ViewHolder(listItem);
        productsSelectionViewHolder.setIsRecyclable(false);
        return productsSelectionViewHolder;
    }

    /**
     * The method below is responsible for placing all information into the corresponding elements declared earlier.
     *
     * It is also responsible to identify the plus and minus buttons pressed when the customer touches the screen.
     *
     * @param holder the ViewHolder parsed.
     * @param position the position of the business selected by the customer.
     */
    @Override
    public void onBindViewHolder(ProductsSelectionListAdapter.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);

        final OrderItem myList = productsSelectionListItems.get(position);

        holder.productName.setText(myList.getProduct().getProductName());
        holder.productDescription.setText(myList.getProduct().getDescription());
        holder.productPrice.setText(String.valueOf(myList.getProduct().getPrice()+"€"));

        final TextView quantitySelected = holder.itemView.findViewById(R.id.quantitySelected);
        quantitySelected.setText(String.valueOf(myList.getQuantity()));

        if(myList.getQuantity() > 0){
            quantitySelected.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.lime));
        }

        holder.plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myList.setQuantity((myList.getQuantity()+1));
                ++totalQuantity;

                quantitySelected.setTextColor(view.getContext().getResources().getColor(R.color.lime));
                quantitySelected.setText(String.valueOf(myList.getQuantity()));

                boolean productExists = false;
                int indexOfProduct = 0;

                for(int i=0; i<productsSelectionListItems.size(); i++){
                    Product productFromListToCompare = productsSelectionListItems.get(i).getProduct();
                    if(myList.getProduct().getProductId()==productFromListToCompare.getProductId()){

                        if(allSelectedItemsList.size()>0){
                            for(int t=0; t<allSelectedItemsList.size(); t++){
                                if(allSelectedItemsList.get(t).getProduct().getProductId() == productFromListToCompare.getProductId()){
                                    productExists =  true;
                                    indexOfProduct = t;
                                    break;
                                }
                            }
                        }

                        if(productExists && allSelectedItemsList.size()>0){
                            allSelectedItemsList.get(indexOfProduct).setQuantity(myList.getQuantity());
                        }else{
                            allSelectedItemsList.add(new OrderItem(productsSelectionListItems.get(i).getProduct(), myList.getQuantity()));
                        }

                        break;
                    }
                }

            }
        });

        holder.minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myList.getQuantity() > 0 && myList.getQuantity()!=1){
                    myList.setQuantity((myList.getQuantity()-1));
                    --totalQuantity;

                    quantitySelected.setText(String.valueOf(myList.getQuantity()));

                    boolean workIsIndeedDone = false;

                    for(int j=0; j<productsSelectionListItems.size(); j++){
                        Product productFromListToCompare = productsSelectionListItems.get(j).getProduct();
                        if(myList.getProduct().getProductId()==productFromListToCompare.getProductId()){

                            for(int k=0; k<allSelectedItemsList.size(); k++){
                                if(allSelectedItemsList.get(k).getProduct().getProductId() == productFromListToCompare.getProductId()){
                                    allSelectedItemsList.get(k).setQuantity(myList.getQuantity());
                                    workIsIndeedDone=true;
                                    break;
                                }
                            }

                            if(workIsIndeedDone){
                                for(int q=0; q<productsSelectionListItems.size(); q++){
                                    if(productsSelectionListItems.get(q).getProduct().getProductId() == productFromListToCompare.getProductId()){
                                        productsSelectionListItems.get(q).setQuantity(myList.getQuantity());
                                        break;
                                    }
                                }
                            }
                            break;
                        }
                    }
                }else if (myList.getQuantity() == 1){
                    myList.setQuantity((myList.getQuantity()-1));
                    --totalQuantity;

                    quantitySelected.setTextColor(view.getContext().getResources().getColor(R.color.black));
                    quantitySelected.setText(String.valueOf(myList.getQuantity()));

                    for(int k=0; k<productsSelectionListItems.size(); k++){
                        Product productFromListToCompare = productsSelectionListItems.get(k).getProduct();
                        if(myList.getProduct().getProductId()==productFromListToCompare.getProductId()){

                            boolean workIsIndeedDone = false;
                            for(int p=0; p<allSelectedItemsList.size(); p++){
                                if(allSelectedItemsList.get(p).getProduct().getProductId() == myList.getProduct().getProductId()){
                                    allSelectedItemsList.get(p).setQuantity(0);
                                    workIsIndeedDone = true;
                                    break;
                                }
                            }

                            if(workIsIndeedDone){
                                for(int d=0; d<productsSelectionListItems.size(); d++){
                                    if(productsSelectionListItems.get(d).getProduct().getProductId() == productFromListToCompare.getProductId()){
                                        productsSelectionListItems.get(d).setQuantity(0);
                                    }
                                }
                            }
                            break;
                        }
                    }
                } else {
                    Toast.makeText(view.getContext(),"Μη αποδεκτή τιμή!",Toast.LENGTH_LONG).show();
                }
            }
        });

        basketButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), OrderActivity.class);
                intent.putExtra("customer", currentCustomer);
                intent.putExtra("businessSelected", businessSelected);
                intent.putExtra("entireList", allSelectedItemsList);
                view.getContext().startActivity(intent);
            }
        });


    }

    public void setProductsList(ArrayList<OrderItem> newList) {
        this.productsSelectionListItems = new ArrayList<OrderItem>();
        for(int i=0; i<newList.size(); i++){
            this.productsSelectionListItems.add(newList.get(i));
        }
        if(allSelectedItemsList.size()>0){
            for(int j=0; j<allSelectedItemsList.size(); j++){
                for(int x=0; x<productsSelectionListItems.size(); x++){
                    if(allSelectedItemsList.get(j).getProduct().getProductId() == productsSelectionListItems.get(x).getProduct().getProductId()){
                        productsSelectionListItems.get(x).setQuantity(allSelectedItemsList.get(j).getQuantity());
                    }
                }
            }
        }

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        int listSize=0;
        if(productsSelectionListItems!=null && productsSelectionListItems.size()>0){
            listSize = productsSelectionListItems.size();
        }else{
            listSize=0;
        }
        return listSize;
    }

}