/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package mixin_labs.java.bot;
import mixin.java.sdk.Library;
public class App {
    public String getGreeting() {
        return "Hello world.";
    }

    public static void main(String[] args) {
        System.out.println(new App().getGreeting());
        Library libMixin = new Library();
        if (libMixin.someLibraryMethod())  System.out.println("true"); else System.out.println("false");

    }
}
