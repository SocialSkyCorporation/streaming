package com.dward.streaming;

import java.io.File;

public class MediaCache {
    static final String MEDIA_DIR = "media-assets";

    public File getFile(String fileName) {
        return new File(String.format("%s/%s", MEDIA_DIR, fileName));
    }
}
