package com.fake_user_generator.Services;


import com.fake_user_generator.Entities.User;
import com.fake_user_generator.Repository.UserRepository;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> generateFakeUsers(int count, String region, int errorPerRecord, long seed) {
        Faker faker = new Faker(new Random(seed));
        List<User> users = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            User user = new User();
            user.setIdentifier(generateIdentifierFromData(generatedName(faker), generatedPhoneNumber(faker), generatedAddress(faker)));
            user.setName(generatedName(faker));
            user.setAddress(generatedAddress(faker));
            user.setPhone(generatedPhoneNumber(faker));
            user.setRegion(region);

            // Introduce errors based on errorPerRecord
            if (i % errorPerRecord == 0) {
                user.setPhone("invalid-phone");
            }
            users.add(user);
        }
        return userRepository.saveAll(users);
    }

    // generating ids directly from  data (names, phones, address) using some hash.
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
    public List<User> getUsers(int page, int size) {
        if (size < 1) size = 1; // Ensure the page size is at least 1
        return userRepository.findAll(PageRequest.of(page, size)).getContent();
    }

}