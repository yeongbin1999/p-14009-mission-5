package com.back;

import java.util.*;
import java.util.function.Consumer;

public class Main {
    private static List<WiseSaying> lst = new ArrayList<>();
    private static int cnt = 0;
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        Map<String, Consumer<String>> commandMap = new HashMap<>();

        // 등록
        commandMap.put("등록", (cmd) -> {
            System.out.print("작가: ");
            String author = sc.nextLine().trim();
            System.out.print("명언: ");
            String content = sc.nextLine().trim();

            lst.add(new WiseSaying(++cnt, author, content));
            lst.sort(Comparator.comparingInt(w -> w.no));
            System.out.println(cnt + "번 명언이 등록되었습니다.");
        });

        // 목록
        commandMap.put("목록", (cmd) -> {
            System.out.println("번호  /  작가  /  명언");
            System.out.println("----------------------");
            for(WiseSaying w : lst)
                System.out.println(w);
        });

        // 삭제?id=no
        commandMap.put("삭제", (cmd) -> {
            String[] parts = cmd.split("=");
            if(parts.length != 2) {
                System.out.println("잘못된 명령입니다.");
                return;
            }

            try {
                int no = Integer.parseInt(parts[1]);
                int idx = binarySearch(lst, no);

                if(idx == -1) {
                    System.out.println(no + "번 명언은 존재하지 않습니다.");
                }
                else {
                    lst.remove(idx);
                    System.out.println(no + "번 명언이 삭제되었습니다.");
                }
            }
            catch(NumberFormatException e) {
                System.out.println("잘못된 번호 형식입니다.");
            }
        });

        // 수정?id=no
        commandMap.put("수정", (cmd) -> {
            String[] parts = cmd.split("=");
            if(parts.length != 2) {
                System.out.println("잘못된 명령입니다.");
                return;
            }

            try {
                int no = Integer.parseInt(parts[1]);
                int idx = binarySearch(lst, no);

                if(idx == -1) {
                    System.out.println(no + "번 명언은 존재하지 않습니다.");
                }
                else {
                    WiseSaying ws = lst.get(idx);
                    System.out.println("작가(기존): " + ws.author);
                    System.out.print("작가: ");
                    ws.author = sc.nextLine().trim();
                    System.out.println("명언(기존): " + ws.content);
                    System.out.print("명언: ");
                    ws.content = sc.nextLine().trim();
                    System.out.println(no + "번 명언이 수정되었습니다.");
                }
            }
            catch (NumberFormatException e) {
                System.out.println("잘못된 번호 형식입니다.");
            }
        });

        // 종료
        commandMap.put("종료", (cmd) -> {
            System.out.println("프로그램을 종료합니다.");
            System.exit(0);
        });


        System.out.println("== 명언 앱 ==");

        while(true) {
            System.out.print("명령) ");
            String cmd = sc.nextLine().trim();

            String key = cmd.contains("?") ? cmd.split("\\?")[0].trim() : cmd;

            Consumer<String> action = commandMap.get(key);

            if(action != null) action.accept(cmd);
            else System.out.println("잘못된 명령입니다.");
        }
    }

    static int binarySearch(List<WiseSaying> list, int target) {
        int left = 0;
        int right = list.size() - 1;

        while(left <= right) {
            int mid = (left + right) / 2;
            int no = list.get(mid).no;

            if(no == target) return mid;
            else if(no < target) left = mid + 1;
            else right = mid - 1;
        }

        return -1;
    }

    static class WiseSaying {
        int no;
        String author;
        String content;

        WiseSaying(int no, String author, String content) {
            this.no = no;
            this.author = author;
            this.content = content;
        }

        @Override
        public String toString() {
            return no + " / " + author + " / " + content;
        }
    }
}
