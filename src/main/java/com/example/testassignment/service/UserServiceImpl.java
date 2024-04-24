package com.example.testassignment.service;

import com.example.testassignment.entity.UserEntity;
import com.example.testassignment.exception.BirthDateRangeException;
import com.example.testassignment.exception.UserNotAdultException;
import com.example.testassignment.exception.UserNotFoundException;
import com.example.testassignment.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Value("${minimum.adult.age}")
    private static int minimumAdultAge;

    @Override
    public UserEntity saveUser(UserEntity user) {
        if (!isUserAdult(user.getBirthDate())) {
            throw new UserNotAdultException("User must be at least 18 years old.");
        }
        return userRepository.save(user);
    }

    @Override
    public UserEntity updateUser(UserEntity user) {
        boolean isExists = userRepository.existsById(user.getId());

        if (!isExists) {
            throw new UserNotFoundException("User not found");
        }
        return userRepository.save(user);
    }

    @Override
    public Page<UserEntity> getAllUsersByBirthDateRange(LocalDate from, LocalDate to, Pageable pageable) {
        if (from.isAfter(to)) {
            throw new BirthDateRangeException("'From' date must be less than 'To' date.");
        }
        return userRepository.findByBirthDateBetween(from, to, pageable);
    }

    @Override
    public void deleteById(Integer id) {
        UserEntity obtainedUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found."));
        userRepository.deleteById(obtainedUser.getId());

    }


    public static boolean isUserAdult(LocalDate birthDate) {
        LocalDate currentDate = LocalDate.now();
        int age = Period.between(birthDate, currentDate).getYears();
        return age >= minimumAdultAge;
    }
}