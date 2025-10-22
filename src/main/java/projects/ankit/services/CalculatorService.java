package projects.ankit.services;

@RpcService(name = "CalculatorService")
public class CalculatorService {
    public int add(int a, int b) {
        return a + b;
    }
}