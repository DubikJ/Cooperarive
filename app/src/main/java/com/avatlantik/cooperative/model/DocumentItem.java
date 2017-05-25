package com.avatlantik.cooperative.model;

import java.io.File;

public class DocumentItem {
    private String title;
    private File file;
    private boolean exist;

    public DocumentItem(String title, File file, boolean isExist) {
        this.title = title;
        this.file = file;
        this.exist = isExist;
    }

    public String getTitle() {
        return title;
    }

    public File getFile() {
        return file;
    }

    public boolean isExist() {
        return exist;
    }
}
