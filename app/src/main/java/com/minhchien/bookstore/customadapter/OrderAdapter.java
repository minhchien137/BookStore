package com.minhchien.bookstore.customadapter;

import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.minhchien.bookstore.OrderDetailFragment;
import com.minhchien.bookstore.R;
import com.minhchien.bookstore.model.Order;
import com.minhchien.bookstore.sharepreference.Constants;
import com.minhchien.bookstore.sharepreference.PreferenceManager;

import java.util.ArrayList;
import java.util.Calendar;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.HolderOrder> {

    private Context context;

    private ArrayList<Order> orderList;

    private Spinner spinner;

    private FragmentManager fragmentManager;

    int isAdmin;

    public OrderAdapter(Context context, ArrayList<Order> orderList, FragmentManager fragmentManager) {
        this.context = context;
        this.orderList = orderList;
        this.fragmentManager = fragmentManager;
        PreferenceManager preferenceManager = new PreferenceManager(context, Constants.LOGIN_KEY_PREFERENCE_NAME);
        this.isAdmin = preferenceManager.getInt(Constants.LOGIN_IS_ADMIN);
    }


    @NonNull
    @Override
    public HolderOrder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int idLayout = R.layout.row_order_user;
        if(isAdmin == 1){
            idLayout = R.layout.row_order_admin;
        }

        View view = LayoutInflater.from(context).inflate(idLayout, parent, false);
        return new HolderOrder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderOrder holder, int position) {
        Order order = orderList.get(position);
        String orderId = order.getOrderId();
        String orderTime = order.getOrderTime();
        String orderBy = order.getOrderBy();
        String orderCost = order.getOrderTotalCost();
        String orderStatus = order.getOrderStatus();

        holder.txtOrder.setText(orderId);
        holder.txtTotal.setText(orderCost);
        holder.txtStatus.setText(orderStatus);
        if(isAdmin == 1){
            holder.txtOderBy.setText(orderBy);
        }

        if (orderStatus.equals("In progress")){
            holder.txtStatus.setTextColor(context.getResources().getColor(R.color.danggiao));
        } else if (orderStatus.equals("Completed")){
            holder.txtStatus.setTextColor(context.getResources().getColor(R.color.hoanthanh));
        } else if (orderStatus.equals("WaitConfirm")) {
            holder.txtStatus.setTextColor(context.getResources().getColor(R.color.xacnhan));
        } else if (orderStatus.equals("Cancelled")){
            holder.txtStatus.setTextColor(context.getResources().getColor(R.color.huy));
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(orderTime));
        String formatedDate = DateFormat.format("dd/MM/yyyy", calendar).toString();
        holder.txtDate.setText(formatedDate);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open order details
                handleOrderItemClick(orderId,orderBy);
            }
        });


    }





    void handleOrderItemClick(String orderId,String orderBy){
        PreferenceManager preferenceManager = new PreferenceManager(context, Constants.LOGIN_KEY_PREFERENCE_NAME);
        int isAdmin = preferenceManager.getInt(Constants.LOGIN_IS_ADMIN);
        OrderDetailFragment orderDetailFragment = new OrderDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString("orderId",orderId);
        bundle.putString("orderBy",orderBy);
        orderDetailFragment.setArguments(bundle);
        int containerID;
        if(isAdmin == 1){
            containerID = R.id.admin_menu_container;
        }
        else containerID = R.id.container;
        this.fragmentManager.beginTransaction()
                .replace(containerID,orderDetailFragment).addToBackStack(null).commit();
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public void updateOrderList(ArrayList<Order> newOrderList) {
        this.orderList = newOrderList;
        notifyDataSetChanged();
    }

    class HolderOrder extends RecyclerView.ViewHolder {
        private TextView txtOrder, txtDate, txtTotal, txtStatus, txtOderBy;

        private Spinner spinner;

        public HolderOrder(@NonNull View itemView) {
            super(itemView);

            txtOrder = itemView.findViewById(R.id.txtOrder);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtTotal = itemView.findViewById(R.id.txtTotal);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            if(isAdmin == 1){
                txtOderBy = itemView.findViewById(R.id.txtOrderBy);
            }
        }


    }
}
