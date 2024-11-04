package example

import net.cakemc.format.CakeProperties
import java.net.InetSocketAddress
import java.util.*

object Example {
    /**
     * Main method to demonstrate the usage of CakeProperties.
     *
     * @param args command line arguments (unused)
     */
    @JvmStatic
    fun main(args: Array<String>) {
        val cakeProperties = CakeProperties("test.properties")

        val value = cakeProperties.getOrCreate("test.message.third", "3")

        println("test.message.third = $value")

        cakeProperties.append("test.object", TestObject("test", 1, false, 12.12))
        cakeProperties.append("test.uuid", UUID.randomUUID())
        cakeProperties.append("test.address", InetSocketAddress(12312))

        cakeProperties.saveProperties()
        cakeProperties.loadProperties()

        println(cakeProperties.get("test.uuid", UUID::class.java))
        println(cakeProperties.get("test.address", InetSocketAddress::class.java))
        println(cakeProperties.get("test.object", TestObject::class.java))
    }

    class TestObject {
        var name: String? = null
            private set
        var age: Int = 0
            private set

        private var update = false
        var time: Double = 0.0
            private set

        constructor()

        constructor(name: String?, age: Int, update: Boolean, time: Double) {
            this.name = name
            this.age = age
            this.update = update
            this.time = time
        }

        override fun toString(): String {
            return "TestObject{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    ", update=" + update +
                    ", time=" + time +
                    '}'
        }
    }
}
