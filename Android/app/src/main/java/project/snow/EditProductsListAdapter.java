package project.snow;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

/**
 * EditProductsListAdapter class.
 *
 * This class is responsible for showing on screen a List Adapter containing all products of a specified businesses.
 * The phase when this happens, is when the business has already logged in/registered and has selected the option: edit an already registered product.
 *
 * So, the List Adapter in this case, adapts the dynamic list of registered products on the screen and makes it selectable for the business.
 *
 * Additionally, this class identifies the product selected by the business.
 *
 * @author thanoskalantzis
 */
public class EditProductsListAdapter extends RecyclerView.Adapter<EditProductsListAdapter.ViewHolder> {
    //Class variables.
    public Business businessSelected;
    public ArrayList<Product> productsSelectionListItems;


    /**
     * ViewHolder inner class.
     *
     * ViewHolder is an inner class which is responsible for identifying all elements (items) of the View, in which (elements) the dynamic product information will be placed.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView productName;
        public TextView productDescription;
        public TextView productPrice;
        public Button editThisProductButton;

        public ViewHolder(View itemView) {
            super(itemView);

            this.productName = (TextView) itemView.findViewById(R.id.editPhaseProductName);
            this.productDescription = (TextView) itemView.findViewById(R.id.editPhaseProductDescription);
            this.productPrice = (TextView) itemView.findViewById(R.id.editPhaseProductPrice);

            this.editThisProductButton = (Button) itemView.findViewById(R.id.editThisProductButton);
            this.editThisProductButton.setBackgroundResource(R.drawable.editbutton);
        }
    }

    //Constructor.
    public EditProductsListAdapter(Business businessSelected, ArrayList<Product> productsSelectionListItems) {
        this.businessSelected=businessSelected;
        this.productsSelectionListItems = productsSelectionListItems;

    }


    /**
     * Function to identify and set further crucial elements.
     *
     * @param parent the parent out of which we will get some of our context.
     * @param viewType the view type.
     * @return productsSelectionViewHolder the ViewHolder created inside the function below.
     */
    @Override
    public EditProductsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater productsSelectionLayoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = productsSelectionLayoutInflater.inflate(R.layout.list_format_edit_products, parent, false);
        EditProductsListAdapter.ViewHolder productsSelectionViewHolder = new EditProductsListAdapter.ViewHolder(listItem);
        productsSelectionViewHolder.setIsRecyclable(false);
        return productsSelectionViewHolder;
    }

    /**
     * The method below is responsible for placing all information into the corresponding elements declared earlier.
     *
     * It is also responsible to identify the product selected when the business touches the screen.
     *
     * @param holder the ViewHolder parsed.
     * @param position the position of the product selected by the customer.
     */
    @Override
    public void onBindViewHolder(EditProductsListAdapter.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);

        final Product productItem = productsSelectionListItems.get(position);

        holder.productName.setText(productItem.getProductName());
        holder.productDescription.setText(productItem.getDescription());
        holder.productPrice.setText(String.valueOf(productItem.getPrice()+"€"));

        holder.editThisProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ControlEditProductActivity.class);
                intent.putExtra("business", businessSelected);
                intent.putExtra("product", productItem);
                view.getContext().startActivity(intent);

                //   ((Activity)view.getContext()).finish();

            }
        });
    }

    /**
     * The function below counts the size (number of elements) of the products list.
     *
     * @return listSize which is the size of the current list containing all registered products (or no products at all).
     */
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