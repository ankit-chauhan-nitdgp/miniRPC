package projects.ankit.services;

import java.util.List;

@RpcService(name = "AverageService")
public class AverageService {
    public double computeAverage(List<Integer> numbers) {
        return numbers.stream().mapToInt(i -> i).average().orElse(0);
    }
}
