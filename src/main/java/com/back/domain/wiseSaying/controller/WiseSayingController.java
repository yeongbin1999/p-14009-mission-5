package com.back.domain.wiseSaying.controller;

import com.back.domain.wiseSaying.service.WiseSayingService;
import com.back.domain.wiseSaying.entity.WiseSaying;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class WiseSayingController {
    private final Scanner sc;
    private final WiseSayingService service;

    public WiseSayingController(Scanner sc, WiseSayingService service) {
        this.sc = sc;
        this.service = service;
    }

    public void add(Map<String, String> params) {
        System.out.print("명언: ");
        String content = sc.nextLine().trim();
        System.out.print("작가: ");
        String author = sc.nextLine().trim();

        int id = service.add(author, content);
        System.out.println(id + "번 명언이 등록되었습니다.");
    }

    public void list(Map<String, String> params) {
        List<WiseSaying> lst = service.list(params);
        int totalPage = service.listSize(params);
        int currentPage = Integer.parseInt(params.getOrDefault("page", "1"));

        System.out.println("번호 / 작가 / 명언");
        System.out.println("--------------------");
        for (int i = 0 ; i < lst.size() ; i++) {
            WiseSaying ws = lst.get(i);
            System.out.println(ws);
        }

        System.out.println("--------------------");
        System.out.print("폐이지: ");
        for (int i = 1 ; i <= totalPage ; i++) {
            if (i == currentPage) System.out.print('[');
            System.out.print(i);
            if (i == currentPage) System.out.print(']');
            System.out.print(' ');
        }
        System.out.println();
    }


    public void remove(Map<String, String> params) {
        String idStr = params.get("id");

        if (idStr == null) {
            System.out.println("id 파라미터가 필요합니다.");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            if (service.remove(id)) {
                System.out.println(id + "번 명언이 삭제되었습니다.");
            } else {
                System.out.println(id + "번 명언은 존재하지 않습니다.");
            }
        } catch (NumberFormatException e) {
            System.out.println("id는 숫자여야 합니다.");
        }
    }

    public void modify(Map<String, String> params) {
        String idStr = params.get("id");

        if (idStr == null) {
            System.out.println("id 파라미터가 필요합니다.");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);

            WiseSaying ws = service.findById(id);  // 기존 데이터 조회
            if (ws == null) {
                System.out.println(id + "번 명언은 존재하지 않습니다.");
                return;
            }

            System.out.println("명언(기존): " + ws.getContent());
            System.out.print("명언: ");
            String content = sc.nextLine().trim();

            System.out.println("작가(기존): " + ws.getAuthor());
            System.out.print("작가: ");
            String author = sc.nextLine().trim();

            if (service.modify(id, author, content)) {
                System.out.println(id + "번 명언이 수정되었습니다.");
            } else {
                System.out.println("수정에 실패했습니다.");
            }
        } catch (NumberFormatException e) {
            System.out.println("id는 숫자여야 합니다.");
        }
    }

    public void build(Map<String, String> params) {
        if (service.build()) System.out.println("빌드가 완료되었습니다.");
        else System.out.println("빌드가 실패했습니다.");
    }

    public void exit(Map<String, String> params) {
        service.exit();
        System.out.println("프로그램을 종료합니다.");
    }
}
