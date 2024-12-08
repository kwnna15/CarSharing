package carsharing;

public class MenuPrinter {

    public void printMainMenu(){
        System.out.println("1. Log in as a manager\n" +
                           "0. Exit");
    }

    public void printManagerMenu(){
        System.out.println("1. Company list\n" +
                           "2. Create a company\n" +
                           "0. Back");
    }

}
