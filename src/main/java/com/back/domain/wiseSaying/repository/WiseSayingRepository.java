package com.back.domain.wiseSaying.repository;

import com.back.domain.wiseSaying.entity.WiseSaying;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class WiseSayingRepository {
    private List<WiseSaying> lst = new ArrayList<>();
    private int lastId = 0;

    private final String dir = "db/wiseSaying/";
    private final String lastIdFile = dir + "lastId.txt";
    private final String buildFile = dir + "data.json";

    public void saveLastId() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(lastIdFile))) {
            bw.write(String.valueOf(lastId));
        } catch (IOException e) {
            System.out.println("lastId 저장 실패: " + e.getMessage());
        }
    }

    public void loadLastId() {
        File file = new File(lastIdFile);
        if (!file.exists()) {
            lastId = 0;
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();
            if (line != null) {
                lastId = Integer.parseInt(line.trim());
            }
        } catch (IOException | NumberFormatException e) {
            lastId = 0;
        }
    }

    public void loadData() {
        lst.clear();
        File folder = new File(dir);
        if (!folder.exists()) {
            folder.mkdirs();
            return;
        }

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".json") && !name.equals("data.json"));
        if (files == null) return;

        for (File file : files) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                int id = 0;
                String author = "";
                String content = "";

                String line;
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.startsWith("\"id\"")) {
                        id = Integer.parseInt(line.split(":")[1].trim().replace(",", ""));
                    } else if (line.startsWith("\"author\"")) {
                        author = line.split(":")[1].trim().replace(",", "").replace("\"", "");
                    } else if (line.startsWith("\"content\"")) {
                        content = line.split(":")[1].trim().replace("\"", "");
                    }
                }

                lst.add(new WiseSaying(id, author, content));

            } catch (IOException | NumberFormatException e) {
                System.out.println(file.getName() + " 로드 실패: " + e.getMessage());
            }
        }
    }


    public int add(String author, String content) {
        WiseSaying ws = new WiseSaying(++lastId, author, content);
        lst.add(ws);
        saveToFile(ws);

        return lastId;
    }

    public boolean deleteById(int id) {
        lst.removeIf(ws -> ws.getId() == id);
        File file = new File(dir + id + ".json");

        if(file.exists()) {
            file.delete();
            return true;
        }

        return false;
    }

    public WiseSaying findById(int id) {
        for (WiseSaying ws : lst) {
            if (ws.getId() == id) return ws;
        }
        return null;
    }

    public List<WiseSaying> list() {
        return new ArrayList<>(lst);
    }

    public void saveToFile(WiseSaying ws) {
        String fileName = dir + ws.getId() + ".json";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            bw.write("{\n");
            bw.write("  \"id\": " + ws.getId() + ",\n");
            bw.write("  \"author\": \"" + ws.getAuthor() + "\",\n");
            bw.write("  \"content\": \"" + ws.getContent() + "\"\n");
            bw.write("}");
        } catch (IOException e) {
            System.out.println(fileName + " 저장 실패: " + e.getMessage());
        }
    }

    public boolean saveToBuildFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(buildFile))) {
            bw.write("[\n");

            for (int i = 0; i < lst.size(); i++) {
                WiseSaying ws = lst.get(i);

                bw.write("  {\n");
                bw.write("    \"id\": " + ws.getId() + ",\n");
                bw.write("    \"author\": \"" + ws.getAuthor() + "\",\n");
                bw.write("    \"content\": \"" + ws.getContent() + "\"\n");
                bw.write("  }");

                if(i != lst.size() - 1) bw.write(",\n");
                else bw.write("\n");
            }

            bw.write("]\n");

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public int listSize() {
        return lst.size();
    }
}
