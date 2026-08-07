package com.acskii.analyse.services;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class StopWordLoader {
    private static final Logger log = LoggerFactory.getLogger(StopWordLoader.class);

    private List<String> stopWords = new ArrayList<>();

    @PostConstruct
    public void load() {
        InputStream stream = getClass().getResourceAsStream("/stop_words.txt");

        if (stream != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String word = line.trim();
                    if (!word.isEmpty()) {
                        stopWords.add(word);
                    }
                }
                log.info("Loaded {} stop words", stopWords.size());

            } catch (IOException e) {
                log.warn("Loading stop words failed: {}", e.getMessage());
                stopWords = new ArrayList<>();
            }
        } else {
            log.warn("stop_words.txt not found in classpath");
        }
    }

    public List<String> getStopWords() {
        return stopWords;
    }
}