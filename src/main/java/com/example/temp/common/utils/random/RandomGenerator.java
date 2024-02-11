package com.example.temp.common.utils.random;

public interface RandomGenerator {

    String generate(int size);

    String generate();

    String generateWithSeed(String seed, int length);

}
