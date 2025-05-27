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

    // 현재 구조는 App이 자체적으로 의존성을 주입해서 외부에서 기존 데이터에 영향 안가게 처리가 불가능
    // 설계가 잘못됨 테스트 코드를 작성하는게 처음이라 테스트가 불가능한 방식으로 작성된 코드 구조
    public static void clear() {

    }
}
