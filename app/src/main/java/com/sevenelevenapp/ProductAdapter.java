package com.sevenelevenapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> products;

    // Constructor
    public ProductAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);

        // Bind data to views
        holder.productName.setText(product.getProduct_name());
        holder.productPrice.setText(product.getPrice());

        // Handle Details click
        holder.detailsLink.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("product", product); // Pass the Product object
            context.startActivity(intent);
        });

        // Add to Cart Button click listener (optional)
        holder.addToCartButton.setOnClickListener(v -> {
            // Implement cart functionality here
            // For example, show a toast or update cart data
        });
    }

    @Override
    public int getItemCount() {
        return products != null ? products.size() : 0;
    }

    // ViewHolder class
    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, detailsLink;
        Button addToCartButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            detailsLink = itemView.findViewById(R.id.details_link);
            addToCartButton = itemView.findViewById(R.id.add_to_cart_button);
        }
    }
}