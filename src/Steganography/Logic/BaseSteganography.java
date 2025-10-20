package Steganography.Logic;

import Steganography.Exceptions.CannotDecodeException;
import Steganography.Exceptions.CannotEncodeException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseSteganography {

    long capacity;
    byte[] header;
    HiddenData secretInfo;
    int i = 0;
    int j = 0;

    BaseSteganography() {}

    public abstract void encode(byte[] message, File output) throws IOException, CannotEncodeException;

    public abstract void encode(File doc, File output) throws IOException, CannotEncodeException;

    public abstract void decode(File file) throws IOException, CannotDecodeException;

    public abstract byte[] getHeader() throws CannotDecodeException;

    protected abstract void increment();

    protected abstract void reset();

    public long getCapacity() {
        return this.capacity;
    }

    protected HiddenData getSecretInfo() {
        return this.secretInfo;
    }

    void setSecretInfo(HiddenData info) {
        this.secretInfo = info;
    }

    byte[] setHeader(byte[] message) throws CannotEncodeException {
        if (message.length == 0)
            throw new CannotEncodeException("Message is empty");
        if (message.length > 16777215)
            throw new CannotEncodeException("Message is larger than maximum allowed capacity (16777215 bytes)");
        List<Byte> header = new ArrayList<>();
        header.add((byte) 'M');
        String messageLength = String.format("%24s", Integer.toBinaryString(message.length)).replace(' ', '0');
        for (int i = 0; i < messageLength.length(); i += 8) {
            header.add((byte) Integer.parseInt(messageLength.substring(i, i + 8), 2));
        }
        header.add((byte) '!');
        this.header = Utils.toByteArray(header);
        if (capacity - this.header.length < message.length)
            throw new CannotEncodeException("Message is larger than image capacity by " + (message.length - capacity + this.header.length) + " bytes.");
        return Utils.toByteArray(header);
    }

    byte[] setHeader(File file) throws IOException, CannotEncodeException {
        if (file.length() == 0)
            throw new CannotEncodeException("File is empty.");
        if (file.length() > 16777215)
            throw new CannotEncodeException("File is larger than maximum allowed capacity (16777215 bytes)");
        List<Byte> header = new ArrayList<>();
        String extension = Utils.getFileExtension(file).toLowerCase();
        header.add((byte) 'D');
        String fileLength = String.format("%24s", Long.toBinaryString(file.length())).replace(' ', '0');
        for (int i = 0; i < fileLength.length(); i += 8) {
            header.add((byte) Integer.parseInt(fileLength.substring(i, i + 8), 2));
        }
        byte[] fileExtension = extension.getBytes(StandardCharsets.UTF_8);
        for (byte b : fileExtension)
            header.add(b);
        header.add((byte) '!');
        this.header = Utils.toByteArray(header);
        if (capacity - this.header.length < file.length())
            throw new CannotEncodeException("File is larger than maximum capacity by " + (file.length() - capacity + this.header.length) + " bytes.");
        return Utils.toByteArray(header);
    }
}
