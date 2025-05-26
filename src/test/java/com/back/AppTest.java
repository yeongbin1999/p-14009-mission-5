package com.back;

import com.back.standard.util.TestUtil;

import java.io.ByteArrayOutputStream;
import java.util.Scanner;

public class AppTest {
    public static String run(String input) {
        Scanner sc = TestUtil.genScanner(input);
        ByteArrayOutputStream output = TestUtil.setOutToByteArray();

        new App(sc).run();

        String result = output.toString().trim();
        TestUtil.clearSetOutToByteArray(output);

        return result;
    }

    public static void clear() {

    }
}
