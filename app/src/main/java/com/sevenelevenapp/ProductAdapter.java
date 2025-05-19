package com.sevenelevenapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<ProductData.Product> products;

    // Constructor
    public ProductAdapter(Context context, List<ProductData.Product> products) {
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
        ProductData.Product product = products.get(position);

        // Bind data to views
        holder.productName.setText(product.getName());
        holder.productPrice.setText("Price: $" + String.format("%.2f", product.getPrice()));

        // Load image with Picasso
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Picasso.get()
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.placeholder) // Placeholder image
                    .error(R.drawable.error) // Error image
                    .into(holder.productImage);
        } else {
            holder.productImage.setImageResource(R.drawable.placeholder);
        }

        // Promotion Banner (example: show if price is below a certain value)
        if (product.getPrice() < 300) {
            holder.promotionText.setVisibility(View.VISIBLE);
            holder.promotionText.setText("7-Eleven獨家 $1000 優惠券 $200");
        } else {
            holder.promotionText.setVisibility(View.GONE);
        }

        // Sentiment Score (you can modify this based on your logic)
        holder.sentimentScore.setText("Sentiment Score: N/A");

        // Add to Cart Button click listener
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
        ImageView productImage;
        TextView productPrice;
        TextView productName;
        TextView promotionText; // Promotion text view
        TextView sentimentScore; // Sentiment score text view
        Button addToCartButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productPrice = itemView.findViewById(R.id.product_price);
            productName = itemView.findViewById(R.id.product_name);
            promotionText = itemView.findViewById(R.id.promotion_text); // Initialize promotion text view
            sentimentScore = itemView.findViewById(R.id.sentiment_score); // Initialize sentiment score text view
            addToCartButton = itemView.findViewById(R.id.add_to_cart_button);
        }
    }
}
