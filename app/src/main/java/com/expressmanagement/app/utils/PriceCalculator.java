package com.expressmanagement.app.utils;

import java.util.HashMap;
import java.util.Map;

public class PriceCalculator {

    // 基础价格配置
    private static final float BASE_PRICE = 10.0f;           // 首重价格（1kg内）
    private static final float ADDITIONAL_PRICE = 5.0f;      // 续重价格（每kg）

    // 物品类型加价配置
    private static final Map<String, Float> itemTypeExtraPrice = new HashMap<>();

    static {
        itemTypeExtraPrice.put("文件", 0f);
        itemTypeExtraPrice.put("数码", 5f);
        itemTypeExtraPrice.put("服装", 2f);
        itemTypeExtraPrice.put("食品", 3f);
        itemTypeExtraPrice.put("其他", 0f);
    }

    /**
     * 计算运费
     * 规则: 基础费用(10元/1kg) + 续重费用(5元/kg) + 特殊物品加价
     *
     * @param weight 重量(kg)
     * @param itemType 物品类型
     * @return 运费(元)
     */
    public static float calculatePrice(float weight, String itemType) {
        if (weight <= 0) {
            return 0f;
        }

        float price = BASE_PRICE;

        // 计算续重费用
        if (weight > 1) {
            float additionalWeight = weight - 1;
            price += Math.ceil(additionalWeight) * ADDITIONAL_PRICE;
        }

        // 加上特殊物品加价
        Float extraPrice = itemTypeExtraPrice.get(itemType);
        if (extraPrice != null) {
            price += extraPrice;
        }

        return price;
    }

    /**
     * 获取所有可选的物品类型
     */
    public static String[] getItemTypes() {
        return new String[]{"文件", "数码", "服装", "食品", "其他"};
    }

    /**
     * 获取物品类型的加价说明
     */
    public static String getItemTypeExtraInfo(String itemType) {
        Float extraPrice = itemTypeExtraPrice.get(itemType);
        if (extraPrice == null || extraPrice == 0) {
            return "无额外费用";
        }
        return "加价 " + extraPrice + " 元";
    }

    /**
     * 格式化价格显示
     */
    public static String formatPrice(float price) {
        return String.format("%.2f", price);
    }
}