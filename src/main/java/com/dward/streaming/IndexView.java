package com.dward.streaming;

import com.google.common.collect.Lists;
import io.dropwizard.views.View;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class IndexView extends View {

    protected IndexView() {
        super("watch.html.mustache");
    }

    public List<String> getFiles() {
        final String[] files = new File(MediaCache.MEDIA_DIR).list();
        final List<String> validFiles = Lists.newArrayList();
        for (String file : files) {
            try {
                VideoMediaTypes.parse(file);
                validFiles.add(file);
            } catch(Exception ignore) {
                //ignore
            }
        }
        Collections.sort(validFiles);
        return validFiles;
    }
}
