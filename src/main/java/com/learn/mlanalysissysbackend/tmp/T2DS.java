package com.learn.mlanalysissysbackend.tmp;

public class T2DS {
    // 定义模数
    static final long MOD = 998244353L;

    public static void main(String[] args) {
        // 定义边界值 (使用 long 类型)
        long A = 20269876543210L;
        long B = 20260123456789L;

        long result = solve(A, B);
        System.out.println(result);
    }

    public static long solve(long A, long B) {
        // 确保 A 是较小的那个，方便逻辑处理 (可选，但推荐)
        // 如果交换了，逻辑里的 min 和 max 也要对应，这里不交换直接处理更直观
        long minLimit = Math.min(A, B);
        long maxLimit = Math.max(A, B);
        long sumLimit = A + B;

        long totalWays = 0;

        // 遍历所有可能的 k，使得 k^2 <= A + B
        // k 的最大值大约是 sqrt(4 * 10^13) ≈ 6.4 * 10^6
        // 使用 long 防止溢出
        long maxK = (long) Math.sqrt(sumLimit);

        for (long k = 0; k <= maxK; k++) {
            long S = k * k; // 目标和 S = a + b

            long count = 0;

            if (S <= minLimit) {
                // 阶段 1: 上升阶段
                // 解的数量是 S + 1
                count = (S + 1) % MOD;
            } else if (S <= maxLimit) {
                // 阶段 2: 平顶阶段
                // 解的数量受限于较小的边界
                count = (minLimit + 1) % MOD;
            } else if (S <= sumLimit) {
                // 阶段 3: 下降阶段
                // 解的数量是 (A + B) - S + 1
                count = (sumLimit - S + 1) % MOD;
            }
            // 如果 S > sumLimit，循环自然会终止，因为 k 的范围限制

            totalWays = (totalWays + count) % MOD;
        }

        return totalWays;
    }
}