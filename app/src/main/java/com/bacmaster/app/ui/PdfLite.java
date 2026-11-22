package com.bacmaster.app.ui;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Minimal PDF writer: embeds bitmaps as JPEG (DCTDecode) pages, one per page.
 * Avoids shipping pre-built booklet PDFs inside the APK.
 */
public final class PdfLite {

    private PdfLite() { }

    public static void write(List<Bitmap> pages, OutputStream out) throws IOException {
        List<byte[]> objs = new ArrayList<>();
        List<Integer> offsets = new ArrayList<>();
        int n = pages.size();
        StringBuilder kids = new StringBuilder();
        for (int i = 0; i < n; i++) kids.append(3 + 3 * i).append(" 0 R ");
        objs.add("<< /Type /Catalog /Pages 2 0 R >>".getBytes("US-ASCII"));
        objs.add(("<< /Type /Pages /Kids [" + kids.toString().trim() + "] /Count " + n + " >>")
                .getBytes("US-ASCII"));
        for (int i = 0; i < n; i++) {
            Bitmap bmp = pages.get(i);
            ByteArrayOutputStream jpegBuf = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 82, jpegBuf);
            byte[] jpeg = jpegBuf.toByteArray();
            int w = bmp.getWidth();
            int h = bmp.getHeight();
            int pageNo = 3 + 3 * i;
            int contNo = pageNo + 1;
            int imgNo = pageNo + 2;
            objs.add(("<< /Type /Page /Parent 2 0 R /MediaBox [0 0 " + w + " " + h
                    + "] /Resources << /XObject << /Im0 " + imgNo
                    + " 0 R >> /ProcSet [/PDF /ImageC] >> /Contents " + contNo + " 0 R >>")
                    .getBytes("US-ASCII"));
            byte[] content = ("q " + w + " 0 0 " + h + " 0 0 cm /Im0 Do Q").getBytes("US-ASCII");
            objs.add(concat(("<< /Length " + content.length + " >>\nstream\n").getBytes("US-ASCII"),
                    content, "\nendstream".getBytes("US-ASCII")));
            objs.add(concat(("<< /Type /XObject /Subtype /Image /Width " + w + " /Height " + h
                    + " /ColorSpace /DeviceRGB /BitsPerComponent 8 /Filter /DCTDecode /Length "
                    + jpeg.length + " >>\nstream\n").getBytes("US-ASCII"), jpeg,
                    "\nendstream".getBytes("US-ASCII")));
        }
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        buf.write("%PDF-1.4\n".getBytes("US-ASCII"));
        for (int i = 0; i < objs.size(); i++) {
            offsets.add(buf.size());
            buf.write(((i + 1) + " 0 obj\n").getBytes("US-ASCII"));
            buf.write(objs.get(i));
            buf.write("\nendobj\n".getBytes("US-ASCII"));
        }
        int xref = buf.size();
        buf.write(("xref\n0 " + (objs.size() + 1) + "\n").getBytes("US-ASCII"));
        buf.write("0000000000 65535 f \n".getBytes("US-ASCII"));
        for (int off : offsets) {
            buf.write((String.format(java.util.Locale.US, "%010d", off) + " 00000 n \n").getBytes("US-ASCII"));
        }
        buf.write(("trailer\n<< /Size " + (objs.size() + 1)
                + " /Root 1 0 R >>\nstartxref\n" + xref + "\n%%EOF").getBytes("US-ASCII"));
        out.write(buf.toByteArray());
        out.flush();
    }

    private static byte[] concat(byte[] a, byte[] b, byte[] c) {
        byte[] r = new byte[a.length + b.length + c.length];
        System.arraycopy(a, 0, r, 0, a.length);
        System.arraycopy(b, 0, r, a.length, b.length);
        System.arraycopy(c, 0, r, a.length + b.length, c.length);
        return r;
    }
}
