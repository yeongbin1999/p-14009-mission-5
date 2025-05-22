package com.back;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;

public class Main {
    private static List<WiseSaying> lst = new ArrayList<>();
    private static int cnt = 0;
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        cnt = loadLastId();
        loadWiseSayings();

        Map<String, Consumer<String>> commandMap = new HashMap<>();

        // 등록
        commandMap.put("등록", (cmd) -> {
            System.out.print("작가: ");
            String author = sc.nextLine().trim();
            System.out.print("명언: ");
            String content = sc.nextLine().trim();

            WiseSaying ws = new WiseSaying(++cnt, author, content);
            lst.add(ws);
            lst.sort(Comparator.comparingInt(w -> w.no));
            ws.saveToFile();
            saveLastId();
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
                    File file = new File("db/wiseSaying/" + no + ".json");
                    if(file.exists()) file.delete();
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
                    ws.saveToFile();
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

        // 빌드
        commandMap.put("빌드", (cmd) -> {
            try {
                File dir = new File("db/wiseSaying");
                if(!dir.exists()) dir.mkdirs();

                try(FileWriter fw = new FileWriter("db/wiseSaying/data.json")) {

                    fw.write("[\n");

                    for (int i = 0; i < lst.size(); i++) {
                        WiseSaying ws = lst.get(i);

                        fw.write("  {\n");
                        fw.write("    \"no\": " + ws.no + ",\n");
                        fw.write("    \"author\": \"" + ws.author + "\",\n");
                        fw.write("    \"content\": \"" + ws.content + "\"\n");
                        fw.write("  }");

                        if(i != lst.size() - 1) fw.write(",\n");
                        else fw.write("\n");
                    }

                    fw.write("]\n");
                }

                System.out.println("data.json 파일의 내용이 갱신되었습니다.");
            } catch (IOException e) {
                System.out.println("파일 저장 실패: " + e.getMessage());
            }
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

    static void saveLastId() {
        try {
            File dir = new File("db/wiseSaying");
            if(!dir.exists()) dir.mkdirs();

            FileWriter fw = new FileWriter("db/wiseSaying/lastId.txt");
            fw.write(String.valueOf(cnt));
            fw.close();
        }
        catch(IOException e) {
            System.out.println("lastId 저장 실패: " + e.getMessage());
        }
    }

    static int loadLastId() {
        try {
            File file = new File("db/wiseSaying/lastId.txt");
            if(!file.exists()) return 0;

            BufferedReader br = new BufferedReader(new FileReader(file));
            int id = Integer.parseInt(br.readLine().trim());
            br.close();
            return id;
        }
        catch(IOException | NumberFormatException e) {
            return 0;
        }
    }

    static void loadWiseSayings() {
        File dir = new File("db/wiseSaying");
        if(!dir.exists()) return;

        File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));
        if(files == null) return;

        for(File file : files) {
            if(file.getName().equals("lastId.txt")) continue;
            WiseSaying ws = WiseSaying.loadFromFile(file);
            if(ws != null) lst.add(ws);
        }

        lst.sort(Comparator.comparingInt(w -> w.no));
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

        public void saveToFile() {
            try {
                File dir = new File("db/wiseSaying");
                if(!dir.exists()) dir.mkdirs();

                FileWriter fw = new FileWriter("db/wiseSaying/" + no + ".json");
                fw.write("{\n");
                fw.write("  \"no\": " + no + ",\n");
                fw.write("  \"author\": \"" + author + "\",\n");
                fw.write("  \"content\": \"" + content + "\"\n");
                fw.write("}\n");
                fw.close();
            }
            catch(IOException e) {
                System.out.println("파일 저장 실패: " + e.getMessage());
            }
        }

        public static WiseSaying loadFromFile(File file) {
            try(BufferedReader br = new BufferedReader(new FileReader(file))) {
                int no = 0;
                String author = "";
                String content = "";

                String line;
                while((line = br.readLine()) != null) {
                    line = line.trim();
                    if(line.startsWith("\"no\"")) {
                        no = Integer.parseInt(line.split(":")[1].trim().replace(",", ""));
                    }
                    else if(line.startsWith("\"author\"")) {
                        author = line.split(":")[1].trim().replaceAll("^\"|\",?$", "");
                    }
                    else if(line.startsWith("\"content\"")) {
                        content = line.split(":")[1].trim().replaceAll("^\"|\",?$", "");
                    }
                }
                return new WiseSaying(no, author, content);
            }
            catch(IOException | NumberFormatException e) {
                System.out.println(file.getName() + " 로딩 실패: " + e.getMessage());
                return null;
            }
        }
    }
}
