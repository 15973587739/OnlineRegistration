package com.atguigu.yygh.user.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DoubleColorBall {
    public static void main(String[] args) {
        List<Integer> redBalls = generateRedBalls();
        List<Integer> redBall = generateBalls(6,33);
        List<Integer> blue = generateBalls(1,16);
        int blueBall = generateBlueBall();

        System.out.println("红色球号码：" + redBalls);
        System.out.println("蓝色球号码：" + blueBall);

        System.out.println("ss");

        System.out.println("红色球号码：" + redBall);
        System.out.println("蓝色球号码：" + blue);

    }

    // 生成红色球号码
    private static List<Integer> generateRedBalls() {
        List<Integer> redBalls = new ArrayList<>();
        Random random = new Random();

        while (redBalls.size() < 6) {
            int ball = random.nextInt(33) + 1;
            if (!redBalls.contains(ball)) {
                redBalls.add(ball);
            }
        }

        return redBalls;
    }

    // 生成蓝色球号码
    private static int generateBlueBall() {
        Random random = new Random();
        return random.nextInt(16) + 1;
    }



    private static List<Integer> generateBalls(int a ,int num) {
        List<Integer> redBalls = new ArrayList<>();
        Random random = new Random();

        while (redBalls.size() < a) {
            int ball = random.nextInt(num) + 1;
            if (!redBalls.contains(ball)) {
                redBalls.add(ball);
            }
        }

        return redBalls;
    }
}
