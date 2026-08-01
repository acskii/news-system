package com.acskii.common.services;

import com.acskii.common.exceptions.SourceNotFoundException;
import com.acskii.common.models.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.acskii.common.repos.SourceRepository;

@Service
public class SourceService {
    private final Logger log = LoggerFactory.getLogger(SourceService.class);
    private final SourceRepository sourceRepository;

    public SourceService(SourceRepository sourceRepository) {
        this.sourceRepository = sourceRepository;
    }

    /* Create */
    public Source create(String name, String url, String description) {
        if (name == null) throw new IllegalArgumentException("first argument 'name' is required");
        if (url == null) throw new IllegalArgumentException("second argument 'url' is required");

        Source src = new Source();
        src.setName(name);
        src.setUrl(url);
        src.setDescription((description != null) ? description : "N/A");

        Source saved = sourceRepository.save(src);
        log.info("(create) source of ID [{}] with name [{}] was created", saved.getId(), name);
        return saved;
    }

    /* Read */
    public Source get(Integer id) {
        return sourceRepository.findById(id)
                .orElseThrow(() -> new SourceNotFoundException(id));
    }

    public Source getByName(String name) {
        return sourceRepository.findByName(name)
                .orElseThrow(() -> new SourceNotFoundException(name));
    }

    public Source getByUrl(String url) {
        return sourceRepository.findByUrl(url)
                .orElseThrow(() -> new SourceNotFoundException(url));
    }

    /* Delete */
    public void delete(Integer id) {
        Source src = get(id);
        sourceRepository.delete(src);
        log.info("(delete) source [{} : {}] was deleted", id, src.getName());
    }
}
