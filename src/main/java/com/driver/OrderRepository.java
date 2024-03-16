package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class OrderRepository {

    private HashMap<String, Order> orderMap;
    private HashMap<String, DeliveryPartner> partnerMap;
    private HashMap<String, HashSet<String>> partnerToOrderMap;
    private HashMap<String, String> orderToPartnerMap;

    public OrderRepository(){
        this.orderMap = new HashMap<String, Order>();
        this.partnerMap = new HashMap<String, DeliveryPartner>();
        this.partnerToOrderMap = new HashMap<String, HashSet<String>>();
        this.orderToPartnerMap = new HashMap<String, String>();
    }

    public void saveOrder(Order order){
        // your code here
        orderMap.put(order.getId(), order);
    }

    public void savePartner(String partnerId){
        // your code here
        // create a new partner with given partnerId and save it
        DeliveryPartner deliveryPartner = new DeliveryPartner(partnerId);
        partnerMap.put(partnerId, deliveryPartner);
    }

    public void saveOrderPartnerMap(String orderId, String partnerId){
        if(orderMap.containsKey(orderId) && partnerMap.containsKey(partnerId)){
            // your code here
            //add order to given partner's order list
            //increase order count of partner
            //assign partner to this order

            orderToPartnerMap.put(orderId, partnerId);

            DeliveryPartner partner = partnerMap.get(partnerId);
            partnerToOrderMap.computeIfAbsent(partnerId, k -> new HashSet<>()).add(orderId);

            partner.setNumberOfOrders(partner.getNumberOfOrders() + 1);
        }
    }

    public Order findOrderById(String orderId){
        // your code here
        return orderMap.get(orderId);
    }

    public DeliveryPartner findPartnerById(String partnerId){
        // your code here
        return partnerMap.get(partnerId);
    }

    public Integer findOrderCountByPartnerId(String partnerId){
        // your code here
//        if (partnerToOrderMap.containsKey(partnerId)) {
//            return partnerToOrderMap.get(partnerId).size();
//        }
//        return 0;
        DeliveryPartner deliveryPartner = partnerMap.get(partnerId);
        return deliveryPartner.getNumberOfOrders();
    }

    public List<String> findOrdersByPartnerId(String partnerId){
        // your code here
        Set<String> orders = partnerToOrderMap.get(partnerId);
        return new ArrayList<>(orders);
    }

    public List<String> findAllOrders(){
        // your code here
        // return list of all orders
            return new ArrayList<>(orderMap.keySet());
        }

    public void deletePartner(String partnerId){
        // your code here
        // delete partner by ID
        if (partnerMap.containsKey(partnerId)) {
            // Remove partner from partnerMap
            partnerMap.remove(partnerId);

            // Retrieve the set of orders assigned to this partner
            Set<String> assignedOrders = partnerToOrderMap.get(partnerId);

            // Remove partner from partnerToOrderMap
            partnerToOrderMap.remove(partnerId);

            // Unassign all orders previously assigned to this partner
            for (String orderId : assignedOrders) {
                orderToPartnerMap.remove(orderId);
            }
        }
    }

    public void deleteOrder(String orderId){
        // your code here
        // delete order by ID
        if (orderMap.containsKey(orderId)) {
            // Retrieve the partnerId associated with this order
            String partnerId = orderToPartnerMap.get(orderId);

            // Remove order from orderMap
            orderMap.remove(orderId);

            //Decrement the order count
            DeliveryPartner partner = partnerMap.get(partnerId);
            partner.setNumberOfOrders(partner.getNumberOfOrders()-1);

            // Remove order from orderToPartnerMap
            orderToPartnerMap.remove(orderId);

            // If the order was assigned to a partner, remove it from the partner's assigned orders
            if (partnerId != null && partnerToOrderMap.containsKey(partnerId)) {
                partnerToOrderMap.get(partnerId).remove(orderId);
            }
        }
    }

    public Integer findCountOfUnassignedOrders(){
        // your code here
        int unassignedCount = 0;
        for (String orderId : orderMap.keySet()) {
            if (!orderToPartnerMap.containsKey(orderId)) {
                unassignedCount++;
            }
        }
        return unassignedCount;
    }

    public Integer findOrdersLeftAfterGivenTimeByPartnerId(String timeString, String partnerId){
        // your code here
        int ordersLeft = 0;
        String[] timeParts = timeString.split(":");
        if (timeParts.length == 2) {
            int givenTime = Integer.parseInt(timeParts[0]) * 60 + Integer.parseInt(timeParts[1]);
            Set<String> partnerOrders = partnerToOrderMap.getOrDefault(partnerId, new HashSet<>());
            for (String orderId : partnerOrders) {
                Order order = orderMap.get(orderId);
                if (order != null) {
                    int orderDeliveryTime = order.getDeliveryTime();
                    if (orderDeliveryTime > givenTime) {
                        ordersLeft++;
                    }
                }
            }
        }
        return ordersLeft;
    }

    public String findLastDeliveryTimeByPartnerId(String partnerId){
        // your code here
        // code should return string in format HH:MM
        int latestTime = Integer.MIN_VALUE;
        String lastDeliveryTime = "";
        int time = 0;
        Set<String> partnerOrders = partnerToOrderMap.get(partnerId);
        for (String orderId : partnerOrders) {
            Order order = orderMap.get(orderId);
            if (order != null) {
                int orderDeliveryTime = order.getDeliveryTime();
                if (orderDeliveryTime > latestTime) {
                    latestTime = orderDeliveryTime;
                    time = order.getDeliveryTime();
                }
            }
        }
        int hours = time / 60;
        int minutes = time % 60;
        lastDeliveryTime = String.format("%02d:%02d", hours, minutes);
        return lastDeliveryTime;
    }
}