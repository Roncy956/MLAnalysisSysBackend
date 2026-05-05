package com.learn.mlanalysissysbackend.tmp;

public class T2 {
    public static void main(String[] args) {
        int lastNum = 0;
        for (int i = 0; i <= 10; i++) {
            int num = cul(i * i);

            System.out.println("i=" + i + "  0到" + (i * i) + "有：" + num + "个  " + "增加了：" + (num - lastNum));
            lastNum = num;
        }
    }

    public static int cul(int num) {
        int count = 0;
        for (int i = 0; i <= num; i++) {
            for (int j = 0; j <= num; j++) {
                if (i + j == Math.sqrt(i + j) * Math.sqrt(i + j)) {
                    count++;
                }
            }
        }
        return count;
    }
}

//6366317
//40529992144489