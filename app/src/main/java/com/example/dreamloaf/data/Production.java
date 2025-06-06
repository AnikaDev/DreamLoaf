package com.example.dreamloaf.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "production")
public class Production {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "product_id")
    private int productId;

    private int quantity;
    private String date;

    @ColumnInfo(name = "user_id")
    private int userId;

    public Production() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
}
