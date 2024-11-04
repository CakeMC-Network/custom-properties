package example;

import net.cakemc.format.CakeProperties;

import java.net.InetSocketAddress;
import java.util.UUID;

public class Example {

    /**
     * Main method to demonstrate the usage of CakeProperties.
     *
     * @param args command line arguments (unused)
     */
    public static void main(String[] args) {
        CakeProperties cakeProperties = new CakeProperties("test.properties");

        String value = cakeProperties.getOrCreate("test.message.third", "3");

        System.out.println("test.message.third = " + value);

        cakeProperties.append("test.object", new TestObject("test", 1, false, 12.12));
        cakeProperties.append("test.uuid", UUID.randomUUID());
        cakeProperties.append("test.address", new InetSocketAddress(12312));

        cakeProperties.saveProperties();
        cakeProperties.loadProperties();

        System.out.println(cakeProperties.get("test.uuid", UUID.class));
        System.out.println(cakeProperties.get("test.address", InetSocketAddress.class));
        System.out.println(cakeProperties.get("test.object", TestObject.class));
    }

    public static class TestObject {

        private String name;
        private int age;

        private boolean update;
        private double time;

        public TestObject() {
        }

        public TestObject(String name, int age, boolean update, double time) {
            this.name = name;
            this.age = age;
            this.update = update;
            this.time = time;
        }

        public String getName() {
            return name;
        }

        public double getTime() {
            return time;
        }

        public int getAge() {
            return age;
        }

        @Override
        public String toString() {
            return "TestObject{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    ", update=" + update +
                    ", time=" + time +
                    '}';
        }
    }

}
