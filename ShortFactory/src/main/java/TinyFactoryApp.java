
import automobiles.Bicycle;
import tinyFactory.CarFactory;

public class TinyFactoryApp {
    public static void main(String[] args) {
        try {
            CarFactory.createAutomobile(Bicycle.class).drive();            
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
