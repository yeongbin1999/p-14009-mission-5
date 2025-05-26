package com.back.domain.wiseSaying.controller;

import com.back.AppTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class WiseSayingControllerTest {

    @BeforeEach
    void beforeEach() {
        AppTest.clear();
    }

    @Test
    @DisplayName("명언 등록")
    void t1() {
        String out = AppTest.run("""
                등록
                실패는 성공의 어머니
                작자미상
                종료
                """);

        assertThat(out)
                .contains("명언:")
                .contains("작가:")
                .contains("1번 명언이 등록되었습니다.");
    }

    @Test
    @DisplayName("명언 리스트 출력")
    void t2() {
        // 등록 후 종료 -> 리스트 출력 후 종료 순으로 진행
        String out = AppTest.run("""
                등록
                나는 생각한다 고로 존재한다.
                데카르트
                등록
                모든 인간은 죽는다.
                소크라테스
                목록
                종료
                """);

        assertThat(out)
                .contains("2 / 소크라테스 / 모든 인간은 죽는다.")
                .contains("1 / 데카르트 / 나는 생각한다 고로 존재한다.");
    }

    @Test
    @DisplayName("명언 삭제")
    void t3() {
        AppTest.run("""
                등록
                인생은 짧고 예술은 길다.
                히포크라테스
                종료
                """);

        String out = AppTest.run("""
                삭제?id=1
                종료
                """);

        assertThat(out)
                .contains("1번 명언이 삭제되었습니다.");
    }

    @Test
    @DisplayName("명언 수정")
    void t4() {
        // 수정 테스트 시 명언 등록 후, 한 번의 run에서 수정 입력 모두 수행하도록 변경
        AppTest.run("""
                등록
                실패는 성공의 어머니
                작자미상
                종료
                """);

        String out = AppTest.run("""
                수정?id=1
                고난은 성장의 기회다.
                익명
                종료
                """);

        assertThat(out)
                .contains("1번 명언이 수정되었습니다.");
    }

    @Test
    @DisplayName("존재하지 않는 명언 삭제 요청 시 메시지 출력")
    void t5() {
        String out = AppTest.run("""
                삭제?id=999
                종료
                """);

        assertThat(out)
                .contains("999번 명언은 존재하지 않습니다.");
    }

    @Test
    @DisplayName("존재하지 않는 명언 수정 요청 시 메시지 출력")
    void t6() {
        String out = AppTest.run("""
                수정?id=999
                종료
                """);

        assertThat(out)
                .contains("999번 명언은 존재하지 않습니다.");
    }
}
