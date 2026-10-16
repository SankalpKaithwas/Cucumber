package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        System.out.printf("Hello and welcome!");

        for (int i = 1; i <= 5; i++) {
            //TIP Press <shortcut actionId="Debug"/> to start debugging your code. We have set one <icon src="AllIcons.Debugger.Db_set_breakpoint"/> breakpoint
            // for you, but you can always add more by pressing <shortcut actionId="ToggleLineBreakpoint"/>.
            System.out.println("i = " + i);
        }

        int i = 0;
        switch (i){
            case 1: System.out.println("One");
                break;
            case 2: System.out.println("Two");
                break;
            case 3: System.out.println("Three");
                break;
            default:System.out.println("Infinity");
                break;
        }
    i = 2;
        switch (i){
            case 1 -> System.out.println("One");
            case 2-> System.out.println("Two");
            case 3-> System.out.println("Three");
            default->System.out.println("Infinity");

        }

    }
}