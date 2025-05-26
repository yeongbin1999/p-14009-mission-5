package com.back;

import com.back.domain.wiseSaying.controller.WiseSayingController;
import com.back.domain.wiseSaying.repository.WiseSayingRepository;
import com.back.domain.wiseSaying.service.WiseSayingService;

import java.util.*;
import java.util.function.Consumer;

public class App {
    private final Scanner sc;

    public App() {
        this.sc = new Scanner(System.in);
    }

    public App(Scanner sc) {
        this.sc = sc;
    }

    public void run() {
        WiseSayingRepository repository = new WiseSayingRepository();
        WiseSayingService service = new WiseSayingService(repository);
        WiseSayingController controller = new WiseSayingController(sc, service);

        service.init();

        Map<String, Consumer<Map<String, String>>> commandMap = new HashMap<>();
        commandMap.put("등록", controller::add);
        commandMap.put("목록", controller::list);
        commandMap.put("삭제", controller::remove);
        commandMap.put("수정", controller::modify);
        commandMap.put("빌드", controller::build);
        commandMap.put("종료", controller::exit);

        System.out.println("== 명언 앱 ==");

        boolean running = true;
        while (running) {
            System.out.print("명령) ");
            String cmd = sc.nextLine().trim();

            String key = cmd.contains("?") ? cmd.substring(0, cmd.indexOf("?")).trim() : cmd;
            String paramStr = cmd.contains("?") ? cmd.substring(cmd.indexOf("?") + 1).trim() : "";

            if(key.equals("종료")) {
                running = false;
                System.out.println("프로그램을 종료합니다.");
                continue;
            }

            Consumer<Map<String, String>> action = commandMap.get(key);
            Map<String, String> params = parseParams(paramStr);

            if (action != null) action.accept(params);
            else System.out.println("잘못된 명령입니다.");
        }
    }

    public Map<String, String> parseParams(String paramStr) {
        Map<String, String> params = new HashMap<>();

        if (paramStr == null || paramStr.isEmpty()) {
            return params;
        }

        String[] pairs = paramStr.split("&");

        for (String pair : pairs) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                String key = kv[0].trim();
                String value = kv[1].trim();
                params.put(key, value);
            }
        }

        return params;
    }
}
