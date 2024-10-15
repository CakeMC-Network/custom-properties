package example;

import net.cakemc.format.CakeProperties;

import java.io.IOException;

public class Example {

    /**
     * Main method to demonstrate the usage of CakeProperties.
     *
     * @param args command line arguments (unused)
     */
    public static void main(String[] args) {
        try {
            CakeProperties cakeProperties = new CakeProperties("test.properties");

            // Add or update properties
            cakeProperties.append("test.message.first", "1");
            cakeProperties.append("test.message.second", "2");

            // Retrieve or create a new property
            String value = cakeProperties.getOrCreate("test.message.third", "3");

            cakeProperties.append("owo.message.first", "test");

            System.out.println("test.message.third = " + value);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
