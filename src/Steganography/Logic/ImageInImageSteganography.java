package Steganography.Logic;

import Steganography.Exceptions.CannotDecodeException;
import Steganography.Exceptions.CannotEncodeException;
import Steganography.Exceptions.UnsupportedImageTypeException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageInImageSteganography extends ImageSteganography {

    public ImageInImageSteganography(File input) throws IOException, UnsupportedImageTypeException {
        super(input);
    }

    byte[] setHeader(File file) throws IOException {
        List<Byte> header = new ArrayList<>();
        header.add((byte) 'I');
        BufferedImage bimg = ImageIO.read(file);
        String width = String.format("%16s", Long.toBinaryString(bimg.getWidth())).replace(' ', '0');
        String height = String.format("%16s", Long.toBinaryString(bimg.getHeight())).replace(' ', '0');
        for (int i = 0; i < width.length(); i += 8)
            header.add((byte) Integer.parseInt(width.substring(i, i + 8), 2));
        for (int i = 0; i < height.length(); i += 8)
            header.add((byte) Integer.parseInt(height.substring(i, i + 8), 2));
        header.add((byte) '!');
        this.header = Utils.toByteArray(header);
        return Utils.toByteArray(header);
    }

    public void encode(File img, File output) throws IOException, CannotEncodeException {
        this.writeHeader(this.setHeader(img));
        BufferedImage imageToHide = ImageIO.read(img);
        int sourceWidth = this.image.getWidth(), sourceHeight = this.image.getHeight();
        int embedWidth = imageToHide.getWidth(), embedHeight = imageToHide.getHeight();
        int pos = ((this.header.length) / sourceWidth) + 1;
        if (embedHeight + pos > sourceHeight || embedWidth > sourceWidth)
            throw new CannotEncodeException("Secret image larger than source image.");
        for (int k = pos; k < embedHeight + pos; k++) {
            for (int l = 0; l < embedWidth; l++) {
                this.image.setRGB(l, k, hidePixel(this.image.getRGB(l, k), imageToHide.getRGB(l, k - pos)));
            }
        }
        ImageIO.write(this.image, "png", output);
    }

    public void decode(File result) throws IOException, CannotDecodeException {
        reset();
        int sourceWidth = this.image.getWidth(), sourceHeight = this.image.getHeight();
        this.setSecretInfo(new HiddenData(this.getHeader()));
        int pos = (this.header.length / sourceWidth) + 1;
        BufferedImage hiddenImage = new BufferedImage(secretInfo.width, secretInfo.height, BufferedImage.TYPE_INT_RGB);
        for (int k = pos; k < secretInfo.height + pos; k++) {
            for (int l = 0; l < secretInfo.width; l++) {
                int pixel = revealPixel(this.image.getRGB(l, k));
                hiddenImage.setRGB(l, k - pos, pixel);
            }
        }
        ImageIO.write(hiddenImage, "png", result);
    }

    private int hidePixel(int pixelA, int pixelB) {
        return pixelA & 0xFFF8F8F8 | (pixelB & 0x00E0E0E0) >> 5;
    }

    private int revealPixel(int pixel) {
        return (pixel & 0xFF070707) << 5;
    }
}
