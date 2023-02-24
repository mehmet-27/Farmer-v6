package xyz.geik.farmer.model.inventory;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Farmer item which contains item name, price, amount and material.
 * Farmer item is an item which farmer can store in his inventory.
 *
 * @author Geik
 */
@Setter
@Getter
public class FarmerItem {

    // Name of item (Material name)
    private String name;

    // Price of item
    private double price;

    // Amount of item
    private long amount;

    // XMaterial of item Calculated in constructor
    private XMaterial material;

    // If item is calculating average production or not
    private boolean productionCalculation;

    /**
     * Constructor of FarmerItem
     *
     * @param name
     * @param price
     * @param amount
     */
    public FarmerItem(String name, double price, long amount, boolean productionCalculation) {
        this.name = name;
        this.price = price;
        this.amount = amount;
        this.material = XMaterial.matchXMaterial(name).get();
        this.productionCalculation = productionCalculation;
    }

    // Summing x to amount
    public void sumAmount(long sum) {
        this.amount += sum;
    }

    // Negating x from amount
    public void negateAmount(long negate) {
        this.amount -= negate;
    }

    /**
     * Serializing FarmerItem set to flat string
     * Because it should save to database
     *
     * @param items
     * @return
     */
    public static String serializeItems(@NotNull List<FarmerItem> items) {
        StringBuilder builder = new StringBuilder();
        for (FarmerItem item : items) {
            if (item.amount == 0)
                continue;
            builder.append(item.getName() + ":" + item.getAmount() + ",");
        }
        return (builder.length() > 0)
                ? builder.substring(0, builder.length()-1)
                : "";
    }

    /**
     * Deserializeing FarmerItem from flat string to Set<FarmerItem>
     *
     * @param items
     * @return
     */
    public static List<FarmerItem> deserializeItems(String items) {
        // Return default items if item list is null
        if (items == null)
            return new ArrayList<>(FarmerInv.defaultItems);

        HashMap<String, Long> tempItems = new LinkedHashMap<>();
        Arrays.stream(items.split(",")).forEach(key -> {
            String[] rawArr = key.split(":");
            tempItems.put(rawArr[0], Long.parseLong(rawArr[1]));
        });

        List<FarmerItem> result = new ArrayList<>(FarmerInv.defaultItems);
        result = result.stream().map(farmerItem -> {
            if (tempItems.containsKey(farmerItem.getName()))
                farmerItem.setAmount(tempItems.get(farmerItem.getName()));
            return farmerItem;
        }).collect(Collectors.toList());
        return result;
    }

    /**
     * Check if item is calculating average production
     *
     * @param item
     * @return
     */
    public static boolean hasProductCalculating(@NotNull FarmerItem item) {
        return item.isProductionCalculation();
    }
}
