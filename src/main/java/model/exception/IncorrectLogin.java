package model.exception;

public class IncorrectLogin extends Exception {
    public IncorrectLogin() {
    }

    public IncorrectLogin(String message) {
        super("Incorrect login");
    }
}
