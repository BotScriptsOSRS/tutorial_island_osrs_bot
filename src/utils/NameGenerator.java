package utils;

import java.util.Random;

public class NameGenerator {

    private static final String[] WORDS = {"Sky", "Blue", "Red", "Green", "Star", "Moon", "Sun", "Cloud"};
    private final Random random = new Random();

    public String generateRandomName() {
        String word1 = WORDS[random.nextInt(WORDS.length)];
        String word2 = WORDS[random.nextInt(WORDS.length)];
        int number = random.nextInt(100);
        return word1 + word2 + number;
    }
}