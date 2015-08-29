package com.ozzy.bcsproject.cdrgenerator;

import java.io.InputStream;
import java.nio.file.NoSuchFileException;
import java.util.Scanner;

public class ResourceLoader
{
    private String filePath;

    public ResourceLoader(String filePath)
    {
        this.filePath = filePath;

        if(filePath.startsWith("/"))
        {
            throw new IllegalArgumentException("Relative paths may not have a leading slash!");
        }
    }

    public InputStream getResourceStream() throws NoSuchFileException
    {
        ClassLoader classLoader = this.getClass().getClassLoader();

        InputStream inputStream = classLoader.getResourceAsStream(filePath);

        if(inputStream == null)
        {
            throw new NoSuchFileException("Resource file not found. Note that the current directory is the source folder!");
        }

        return inputStream;
    }

    public static String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is);
        return s.hasNext() ? s.next() : "";
    }

    public static String convertStreamToString(InputStream is, String charsetName) {
        Scanner s = new Scanner(is, charsetName);
        return s.hasNext() ? s.next() : "";
    }
}
