public class ValidatorePassword {
    public boolean eSicura(String pwd) {
        if (pwd == null || pwd.length() < 8) {
            return false;
        }
        boolean haNumero = pwd.matches(".*\\d.*");
        boolean haSpeciale = pwd.matches(".*[!@#$%^&*].*");
        return haNumero && haSpeciale;
    }
    // metodo privato
    private void stampaDebug(String msg) {
        System.out.println("DEBUG: " + msg);
    }
}