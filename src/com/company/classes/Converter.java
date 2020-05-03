package com.company.classes;

import javazoom.spi.mpeg.sampled.file.MpegAudioFileReader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public final class Converter {

    private InputStream input;
    private AudioFormat audioFormat;
    private boolean close;

    public Converter(InputStream input, boolean close) {
        this.input = input;
        this.close = close;
    }

    public static Converter convertFrom(InputStream input) throws IOException, UnsupportedAudioFileException {
        return new Converter(input, false);
    }

    public Converter withTargetFormat(AudioFormat targetAudioFormat) {
        this.audioFormat = targetAudioFormat;
        return this;
    }

    public void to(OutputStream output) {
        try{
            final ByteArrayOutputStream rawOutputStream = new ByteArrayOutputStream();
            convert(input, rawOutputStream, getTargetFormat());
            final byte[] rawResult = rawOutputStream.toByteArray();
            final AudioInputStream audioInputStream = new AudioInputStream(new ByteArrayInputStream(rawResult),
                    getTargetFormat(), rawResult.length);
            AudioSystem.write(audioInputStream, Type.WAVE, output);
        } catch (Exception e) {
            throw new ConversionException(e);
        } finally {
            closeInput();
        }
    }

    private void closeInput() {
        if (this.close) {
            try {
                input.close();
            } catch (IOException e) {

            }
        }
    }

    private void convert(InputStream input, OutputStream output, AudioFormat targetFormat) throws Exception {
        try {
            final AudioInputStream audioInputStream = new MpegAudioFileReader().getAudioInputStream(input);
            final AudioFormat sourceFormat = audioInputStream.getFormat();
            final AudioFormat convertFormat = getAudioFormat(sourceFormat);

            try {
                final AudioInputStream sourceStream = AudioSystem.getAudioInputStream(convertFormat, audioInputStream);
                final AudioInputStream convertStream = AudioSystem.getAudioInputStream(targetFormat, sourceStream);
                int read;
                final byte[] buffer = new byte[8192];
                while ((read = convertStream.read(buffer, 0, buffer.length)) >= 0) {
                    output.write(buffer, 0, read);
                }
            } catch (IOException ex) {}
        } catch (IOException ex) {}
    }

    private AudioFormat getTargetFormat() {
        return this.audioFormat == null
                ? new AudioFormat(44100, 8, 1, true, false)
                : audioFormat;
    }

    private AudioFormat getAudioFormat(AudioFormat sourceFormat) {
        return new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                sourceFormat.getSampleRate(),
                16,
                sourceFormat.getChannels(),

                sourceFormat.getChannels() * 2,
                sourceFormat.getSampleRate(),
                false);
    }
}