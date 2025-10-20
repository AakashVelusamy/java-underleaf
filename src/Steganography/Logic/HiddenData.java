package Steganography.Logic;

import Steganography.Types.DataFormat;

public class HiddenData {

    public DataFormat format;
    public String extension;
    public long length;
    public int width;
    public int height;

    public HiddenData(byte[] header) {
        switch ((char) header[0]) {
            case 'M':
                this.format = DataFormat.MESSAGE;
                this.extension = "txt";
                break;
            case 'D':
                this.format = DataFormat.DOCUMENT;
                StringBuilder extension = new StringBuilder();
                for (int j = 4; j < header.length - 1; j++)
                    extension.append((char) header[j]);
                this.extension = extension.toString();
                break;
            case 'I':
                this.format = DataFormat.IMAGE;
                this.extension = "png";
                StringBuilder width = new StringBuilder();
                StringBuilder height = new StringBuilder();
                for (int i = 1; i < 3; i++) {
                    String w = String.format("%8s", Integer.toBinaryString(header[i])).replace(' ', '0');
                    String h = String.format("%8s", Integer.toBinaryString(header[i + 2])).replace(' ', '0');
                    width.append(w.substring(w.length() - 8, w.length()));
                    height.append(h.substring(h.length() - 8, h.length()));
                }
                this.width = Integer.parseInt(width.toString(), 2);
                this.height = Integer.parseInt(height.toString(), 2);
                break;
        }
        if (this.format != DataFormat.IMAGE) {
            StringBuilder length = new StringBuilder();
            for (int i = 1; i < 4; i++) {
                String l = String.format("%8s", Integer.toBinaryString(header[i])).replace(' ', '0');
                length.append(l.substring(l.length() - 8, l.length()));
            }
            this.length = Integer.parseInt(length.toString(), 2);
        }
    }
}
