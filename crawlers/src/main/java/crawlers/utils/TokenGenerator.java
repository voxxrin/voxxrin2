package crawlers.utils;

public class TokenGenerator {

    private static final int TOKEN_LENGTH = 32;

    public String generate() {
        return new RandomString(TOKEN_LENGTH).nextString().toUpperCase();
    }

    public static void main(String[] args) {
        System.out.println(new TokenGenerator().generate());
    }
}
