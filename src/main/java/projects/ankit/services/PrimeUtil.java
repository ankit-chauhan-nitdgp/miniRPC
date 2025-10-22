package projects.ankit.services;

import java.util.ArrayList;
import java.util.List;


@RpcService(name = "PrimeFactorService")
public class PrimeUtil {
    public static List<Integer> primeFactors(int n) {
        List<Integer> list = new ArrayList<>();
        for (int p = 2; p * p <= n; p++) {
            while (n % p == 0) {
                list.add(p);
                n /= p;
            }
        }
        if (n > 1) list.add(n);
        return list;
    }
}
