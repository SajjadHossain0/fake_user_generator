package com.fake_user_generator.Services;

import com.fake_user_generator.Entities.User;
import com.github.javafaker.Faker;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

@Service
public class UserService {

    public List<User> generateFakeUsers(int count, String region, int errorPerRecord, long seed, int page) {

        List<User> users = new ArrayList<>();
        Faker faker = new Faker(new Locale(region.toLowerCase()), new Random(seed));
        Random random = new Random(seed);  // Use only seed for consistent generation

        for (int i = 0; i < count; i++) {
            User user = new User();
            user.setIdentifier(generateIdentifierFromData(generatedName(faker), generatedPhoneNumber(faker), generatedAddress(faker)));
            user.setName(applyErrors(generatedName(faker), errorPerRecord, random));
            user.setAddress(applyErrors(generatedAddress(faker), errorPerRecord, random));
            user.setPhone(applyErrors(generatedPhoneNumber(faker), errorPerRecord, random));
            user.setRegion(region);
            users.add(user);
        }
        return users;
    }
    public String generateIdentifierFromData(String name, String phone, String address) {
        String data = name + phone + address;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < 16; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating identifier", e);
        }
    }
    private String generatedName(Faker faker){
        return faker.name().firstName()+" "
                + faker.name().firstName()+" "
                + faker.name().lastName();
    }
    private String generatedAddress(Faker faker){
        return faker.address().cityName()+" "
                +faker.address().streetName()+" "
                +faker.address().streetAddress()+" "
                +faker.address().buildingNumber();
    }
    private String generatedPhoneNumber(Faker faker){
        return faker.phoneNumber().phoneNumber();
    }
    private String applyErrors(String data, int errorPerRecord, Random random) {
        // Ensure data is non-empty and errorPerRecord is positive
        if (errorPerRecord <= 0 || data.length() == 0) {
            return data; // No errors to apply
        }

        // Ensure error count does not exceed a sensible limit (e.g., length of the data)
        int errorCount = Math.min(errorPerRecord, data.length() / 2); // Apply errors only up to half the length of data
        StringBuilder sb = new StringBuilder(data);

        for (int i = 0; i < errorCount; i++) {
            int errorType = random.nextInt(3); // Random error type
            int position = random.nextInt(sb.length()); // Ensure position is within data length
            switch (errorType) {
                case 0: // delete character
                    sb.deleteCharAt(position);
                    break;
                case 1: // add random character
                    char randomChar = (char) (random.nextInt(26) + 'a');
                    sb.insert(position, randomChar);
                    break;
                case 2: // swap characters
                    if (position < sb.length() - 1) {
                        char temp = sb.charAt(position);
                        sb.setCharAt(position, sb.charAt(position + 1));
                        sb.setCharAt(position + 1, temp);
                    }
                    break;
            }
        }
        return sb.toString();
    }
}
