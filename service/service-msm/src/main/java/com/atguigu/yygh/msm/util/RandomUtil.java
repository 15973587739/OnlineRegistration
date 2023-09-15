package com.atguigu.yygh.msm.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * 验证码生成
 * @author SIYU
 */
public class RandomUtil {

    // 随机数生成器
    private static final Random random = new Random();

    // 生成四位随机数的格式化对象
    private static final DecimalFormat fourdf = new DecimalFormat("0000");

    // 生成六位随机数的格式化对象
    private static final DecimalFormat sixdf = new DecimalFormat("000000");

    /**
     * 生成四位随机数
     *
     * @return 四位随机数的字符串表示
     */
    public static String getFourBitRandom() {
        return fourdf.format(random.nextInt(10000));
    }

    /**
     * 生成六位随机数
     *
     * @return 六位随机数的字符串表示
     */
    public static String getSixBitRandom() {
        return sixdf.format(random.nextInt(1000000));
    }

    /**
     * 从给定数组中抽取n个元素
     *
     * @param list 给定的数组
     * @param n    要抽取的元素个数
     * @return 抽取的n个元素构成的ArrayList
     */
    public static ArrayList getRandom(List list, int n) {

        Random random = new Random();

        // 存储抽取的元素的HashMap，用于记录抽取的元素的索引
        HashMap<Object, Object> hashMap = new HashMap<Object, Object>();

        // 生成随机数字并存入HashMap
        for (int i = 0; i < list.size(); i++) {
            int number = random.nextInt(100) + 1;
            hashMap.put(number, i);
        }

        // 从HashMap中获取抽取的索引数组
        Object[] robjs = hashMap.values().toArray();

        ArrayList r = new ArrayList();

        // 遍历索引数组，根据索引从给定数组中获取元素，并存入结果ArrayList中
        for (int i = 0; i < n; i++) {
            r.add(list.get((int) robjs[i]));
            System.out.print(list.get((int) robjs[i]) + "    ");
        }
        System.out.print("\n");
        return r;
    }
}
