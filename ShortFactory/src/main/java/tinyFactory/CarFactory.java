package tinyFactory;

import automobiles.Automobile;

public interface CarFactory {
    public static Automobile createAutomobile(Class<?> autoToCreate) throws InstantiationException, IllegalAccessException {
        if (autoToCreate != null && !autoToCreate.isInterface() && Automobile.class.isAssignableFrom(autoToCreate)) {
            return (Automobile) autoToCreate.newInstance();
        }

        return null;
    }
}
