package com.joanzapata.pdfview;

import android.graphics.Bitmap;
import android.graphics.RectF;
import android.net.Uri;

import org.vudroid.ViewMode;
import org.vudroid.core.DecodeService;
import org.vudroid.core.DecodeServiceBase;
import org.vudroid.pdfdroid.codec.PdfContext;

import java.io.File;

/**
 * Context-independent PDF renderer for Android based on modified VuDroid
 */
public class PDFRenderer {

    private DecodeService decodeService = null;

    public PDFRenderer(File file) {
        decodeService = new DecodeServiceBase(new PdfContext());
        decodeService.open(Uri.fromFile(file));
    }

    /**
     * Gets page count
     * @return page count
     */
    public int getPageCount() {
        return decodeService.getPageCount();
    }

    /**
     * Sets the 2-page view.
     * Page numeration is (1, 2), (3, 4), (5, 6)...
     */
    public void setTwoPageView() {
        ViewMode.set(ViewMode.TWO_PAGE);
    }

    /**
     * Sets the 1-page view.
     * Page numeration is (1), (2), (3)...
     */
    public void setOnePageView() {
        ViewMode.set(ViewMode.ONE_PAGE);
    }

    /**
     * Sets the 2-page view with separate centered first page.
     * Page numeration is (1), (2,3), (4,5)...
     */
    public void setMagazineTwoPageView() {
        ViewMode.set(ViewMode.TWO_PAGE);
        ViewMode.setMagazine(true);
    }

    /**
     * Renders the bitmap of a page with specified width and ratio-preserving height
     * @param pageNumber number of page to render, starting from 0
     * @param pageWidth width of obtained bitmap
     * @return bitmap of the page
     */
    public Bitmap renderPage(int pageNumber, int pageWidth) {
        int pageHeight = pageWidth * decodeService.getPage(pageNumber).getHeight()
                / decodeService.getPage(pageNumber).getWidth();
        return renderPage(pageNumber, pageWidth, pageHeight);
    }

    /**
     * Renders the bitmap of a page with specified width and height
     * @param pageNumber number of page to render
     * @param pageWidth width of obtained bitmap
     * @param pageHeight height of obtained bitmap
     * @return bitmap of the page
     */
    public Bitmap renderPage(int pageNumber, int pageWidth, int pageHeight) {
        return decodeService.getPage(pageNumber).renderBitmap(pageWidth, pageHeight,
                new RectF(0, 0, 1, 1));
    }

    /**
     * Recycles the renderer
     */
    public void recycle() {
        decodeService.recycle();
        decodeService = null;
    }

}
