package client_code.addition_test;

import java.util.function.Function;
import client_code.config.ConfigLoader;
import functions.FunctionGenerator;
import strategies.openai.OpenAIFunctionGenerator;

import java.lang.Package;

public class Main {
    public static void main(String[] args) {
        Package pkg = Main.class.getPackage();
        OpenAIFunctionGenerator functionGenerator = OpenAIFunctionGenerator.builder().withApiKey(ConfigLoader.getInstance().getApiKey()).build();
        Function<Integer[], Integer> add = FunctionGenerator.builder(Integer[].class,Integer.class)
            .withStrategy(functionGenerator)
            .withTestPackage(pkg)
            .build();
        
        Integer[] test1 = new Integer[]{2, 2};
        Integer[] test2 = new Integer[]{5, 4};

        // Test the generated function
        System.out.println("\nTesting Generated Function:");
        System.out.println("2 + 2 = " + add.apply(test1));
        System.out.println("5 + 4 = " + add.apply(test2));
    }
}
