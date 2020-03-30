package Exceptions;

public class IncorrectLogin extends Exception {
    public IncorrectLogin() {
    }

    public IncorrectLogin(String message) {
        super(message);
    }
}
