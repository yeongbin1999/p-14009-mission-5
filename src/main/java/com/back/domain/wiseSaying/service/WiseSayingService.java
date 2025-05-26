package com.back.domain.wiseSaying.service;

import com.back.domain.wiseSaying.entity.WiseSaying;
import com.back.domain.wiseSaying.repository.WiseSayingRepository;

import java.util.*;
import java.util.stream.Collectors;

public class WiseSayingService {
    private final WiseSayingRepository repository;

    public WiseSayingService(WiseSayingRepository repository) {
        this.repository = repository;
    }

    public void init() {
        repository.loadLastId();
        repository.loadData();
    }

    public void exit() {
        repository.saveLastId();
    }

    public int add(String author, String content) {
        return repository.add(author, content);
    }

    public boolean remove(int id) {
        return repository.deleteById(id);
    }

    public boolean modify(int id, String newAuthor, String newContent) {
        WiseSaying ws = repository.findById(id);
        if (ws == null) return false;

        ws.setAuthor(newAuthor);
        ws.setContent(newContent);
        repository.saveToFile(ws);

        return true;
    }

    public List<WiseSaying> list(Map<String, String> params) {
        List<WiseSaying> all = repository.list();
        all = all.stream()
                .sorted(Comparator.comparing(WiseSaying::getId).reversed())
                .collect(Collectors.toList());

        String keywordType = params.get("keywordType");
        String keyword = params.get("keyword");

        if (keywordType != null && keyword != null) {
            if (!keywordType.equals("author") && !keywordType.equals("content")) {
                return Collections.emptyList();
            }

            return all.stream()
                    .filter(ws -> keywordType.equals("author") ? ws.getAuthor().contains(keyword) : ws.getContent().contains(keyword))
                    .collect(Collectors.toList());
        }

        return all;
    }


    public boolean build() {
        return repository.saveToBuildFile();
    }

    public WiseSaying findById(int id) {
        return repository.findById(id);
    }
}
