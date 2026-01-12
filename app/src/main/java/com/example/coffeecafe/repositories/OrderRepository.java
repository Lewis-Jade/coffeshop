package com.example.coffeecafe.repositories;

import android.content.Context;
import com.example.coffeecafe.config.SupabaseClient;
import com.example.coffeecafe.models.CartItem;
import com.example.coffeecafe.models.Order;
import com.example.coffeecafe.models.OrderItem;
import com.example.coffeecafe.utils.SessionManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class OrderRepository {
    private final SupabaseClient supabaseClient;
    private final SessionManager sessionManager;

    public OrderRepository(Context context) {
        this.supabaseClient = SupabaseClient.getInstance();
        this.sessionManager = SessionManager.getInstance(context);
    }

    public interface CreateOrderCallback {
        void onSuccess(String orderId);
        void onError(String error);
    }

    public interface FetchOrdersCallback {
        void onSuccess(List<Order> orders);
        void onError(String error);
    }

    public void createOrder(List<CartItem> cartItems, double totalAmount, 
                           String paymentReference, CreateOrderCallback callback) {
        try {
            String userId = sessionManager.getUserId();
            if (userId == null) {
                callback.onError("User not logged in");
                return;
            }

            // Create order object
            JSONObject orderData = new JSONObject();
            orderData.put("user_id", userId);
            orderData.put("total_amount", totalAmount);
            orderData.put("status", "pending");
            orderData.put("payment_reference", paymentReference);

            supabaseClient.insert("orders", orderData, new SupabaseClient.SingleRecordCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        String orderId = response.getString("id");
                        
                        // Insert order items
                        insertOrderItems(orderId, cartItems, new InsertItemsCallback() {
                            @Override
                            public void onSuccess() {
                                // Create payment record
                                createPaymentRecord(orderId, totalAmount, paymentReference, callback);
                            }

                            @Override
                            public void onError(String error) {
                                callback.onError(error);
                            }
                        });
                    } catch (JSONException e) {
                        callback.onError("Failed to parse order response");
                    }
                }

                @Override
                public void onError(String error) {
                    callback.onError(error);
                }
            });
        } catch (JSONException e) {
            callback.onError(e.getMessage());
        }
    }

    private interface InsertItemsCallback {
        void onSuccess();
        void onError(String error);
    }

    private void insertOrderItems(String orderId, List<CartItem> cartItems, InsertItemsCallback callback) {
        // Insert items one by one (in production, you'd want batch insert)
        insertNextItem(orderId, cartItems, 0, callback);
    }

    private void insertNextItem(String orderId, List<CartItem> cartItems, int index, InsertItemsCallback callback) {
        if (index >= cartItems.size()) {
            callback.onSuccess();
            return;
        }

        CartItem item = cartItems.get(index);
        try {
            JSONObject itemData = new JSONObject();
            itemData.put("order_id", orderId);
            itemData.put("product_id", item.getProductId());
            itemData.put("product_name", item.getProductName());
            itemData.put("quantity", item.getQuantity());
            itemData.put("price", item.getProductPrice());
            itemData.put("subtotal", item.getSubtotal());

            supabaseClient.insert("order_items", itemData, new SupabaseClient.SingleRecordCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    // Insert next item
                    insertNextItem(orderId, cartItems, index + 1, callback);
                }

                @Override
                public void onError(String error) {
                    callback.onError(error);
                }
            });
        } catch (JSONException e) {
            callback.onError(e.getMessage());
        }
    }

    private void createPaymentRecord(String orderId, double amount, String reference, CreateOrderCallback callback) {
        try {
            JSONObject paymentData = new JSONObject();
            paymentData.put("order_id", orderId);
            paymentData.put("payment_reference", reference);
            paymentData.put("amount", amount);
            paymentData.put("status", "pending");
            paymentData.put("provider", "paystack");

            supabaseClient.insert("payments", paymentData, new SupabaseClient.SingleRecordCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    callback.onSuccess(orderId);
                }

                @Override
                public void onError(String error) {
                    // Order created but payment record failed - still return success
                    callback.onSuccess(orderId);
                }
            });
        } catch (JSONException e) {
            callback.onSuccess(orderId); // Still return success if payment record fails
        }
    }

    public void fetchUserOrders(FetchOrdersCallback callback) {
        String userId = sessionManager.getUserId();
        if (userId == null) {
            callback.onError("User not logged in");
            return;
        }

        String filter = "user_id=eq." + userId + "&order=created_at.desc";
        
        supabaseClient.select("orders", "*", filter, new SupabaseClient.DatabaseCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                List<Order> orders = new ArrayList<>();
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject obj = response.getJSONObject(i);
                        Order order = new Order();
                        order.setId(obj.getString("id"));
                        order.setUserId(obj.getString("user_id"));
                        order.setTotalAmount(obj.getDouble("total_amount"));
                        order.setStatus(obj.getString("status"));
                        order.setPaymentReference(obj.optString("payment_reference"));
                        order.setCreatedAt(obj.getString("created_at"));
                        order.setUpdatedAt(obj.optString("updated_at"));
                        orders.add(order);
                    }
                    callback.onSuccess(orders);
                } catch (JSONException e) {
                    callback.onError("Failed to parse orders");
                }
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }

    public void updateOrderStatus(String orderId, String newStatus, SupabaseClient.SingleRecordCallback callback) {
        try {
            JSONObject updateData = new JSONObject();
            updateData.put("status", newStatus);
            
            supabaseClient.update("orders", "id=eq." + orderId, updateData, callback);
        } catch (JSONException e) {
            callback.onError(e.getMessage());
        }
    }
}
