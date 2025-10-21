package projects.ankit.services;

@RpcService(name = "CalculatorService")
public class CalculatorServiceImpl implements CalculatorService {
    public int add(int a, int b) {
        return a + b;
    }
}
