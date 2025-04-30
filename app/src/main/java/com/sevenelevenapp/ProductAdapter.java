package com.sevenelevenapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.squareup.picasso.Picasso;
import java.util.List;

public class ProductAdapter extends ArrayAdapter<ProductData.Product> {

    public ProductAdapter(Context context, List<ProductData.Product> products) {
        super(context, 0, products);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_product, parent, false);
        }

        ProductData.Product product = getItem(position);

        TextView productName = convertView.findViewById(R.id.product_name);
        TextView productPrice = convertView.findViewById(R.id.product_price);
        ImageView productImage = convertView.findViewById(R.id.product_image);
        Button addToCartButton = convertView.findViewById(R.id.add_to_cart_button);

        productName.setText(product.getName());
        productPrice.setText("Price: $" + String.format("%.2f", product.getPrice()));

        // Load image with Picasso
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Picasso.get()
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.error)
                    .into(productImage);
        } else {
            productImage.setImageResource(R.drawable.placeholder);
        }

        // Add to Cart Button
        addToCartButton.setOnClickListener(v -> {
            // Implement cart functionality here
        });

        return convertView;
    }
}