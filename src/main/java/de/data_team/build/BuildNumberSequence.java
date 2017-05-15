package de.data_team.build;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

public class BuildNumberSequence {

    private final AtomicLong buildNumber;

    private File buildNumberFile;

    public BuildNumberSequence() {
        try {
            buildNumberFile = new File(BuildNumberSequence.class.getResource("/.buildnumber").toURI());
            final String id = FileUtils.readFileToString(buildNumberFile, StandardCharsets.UTF_8);
            buildNumber = new AtomicLong(StringUtils.isNumeric(id) ? Long.parseLong(id) : 0L);
        } catch (final IOException | URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    public synchronized Long getNext() {
        final Long id = buildNumber.incrementAndGet();
        try {
            FileUtils.write(buildNumberFile, id.toString(), StandardCharsets.UTF_8);
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
        return id;
    }

}
