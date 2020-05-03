package com.company;

import com.company.classes.Converter;
import javazoom.spi.mpeg.sampled.file.MpegAudioFileReader;

import javax.sound.sampled.*;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws IOException, UnsupportedAudioFileException {
        try {
            final InputStream inputStream = Main.class.getResourceAsStream("/com/company/resources/test.mp3");
            if (inputStream != null) {
                final ByteArrayOutputStream output = new ByteArrayOutputStream();
                final AudioFormat audioFormat = new AudioFormat(44100, 8, 1, false, false);
                Converter.convertFrom(inputStream).withTargetFormat(audioFormat).to(output);
                final byte[] wavContent = output.toByteArray();
                Files.write(Paths.get("src/com/company/resources/test.wav"), wavContent);
            }
        } catch (IOException | UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }
}
