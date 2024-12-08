package carsharing;

import java.util.Scanner;

public class UserInputHandler {
    private final MenuPrinter menuPrinter;

    UserInputHandler(){
        menuPrinter = new MenuPrinter();
    }

    void manageInput(){
        Scanner scanner = new Scanner(System.in);
        while (true){
            menuPrinter.printMainMenu();
            String userInput = scanner.nextLine();
            System.out.println();

            if (userInput.equals("0")){
                return;
            }

            managerMenu(scanner);
        }
    }

    private void managerMenu(Scanner scanner) {
        while (true) {
            menuPrinter.printManagerMenu();
            String userInput = scanner.nextLine();

            switch (userInput){
                case "0" -> {
                    System.out.println();
                    return;
                }
                case "1" -> {
                    System.out.println("print Company list!!");
                    System.out.println();
                }
                case "2" -> {
                    System.out.println("We create a company named XXX");
                    System.out.println();
                }
            }
        }
    }
}
