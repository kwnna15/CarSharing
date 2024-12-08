package carsharing;

import java.util.Scanner;

public class HandleUserInput {
    private PrintMenu CompanyMenu;

    HandleUserInput(){
        CompanyMenu = new PrintMenu();
    }

    void manageInput(){
        Scanner scanner = new Scanner(System.in);
        boolean continueApp = true;
        while (continueApp){
            CompanyMenu.mainMenu();
            String userInput = scanner.nextLine();
            System.out.println();
            if (userInput.equals("0")){
                continueApp = false;
            }
            while (continueApp) {
                CompanyMenu.managerMenu();
                userInput = scanner.nextLine();
                if (userInput.equals("0")) {
                    System.out.println();
                    break;
                }
                if (userInput.equals("1")) {
                    System.out.println("print Company list!!");
                    System.out.println();
                }
                if (userInput.equals("2")) {
                    System.out.println("We create a company named XXX");
                    System.out.println();
                }
            }

        }
    }
}
