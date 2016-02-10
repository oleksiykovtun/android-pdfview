/**
 * Copyright 2014 Joan Zapata
 *
 * This file is part of Android-pdfview.
 *
 * Android-pdfview is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Android-pdfview is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Android-pdfview.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.joanzapata;

import android.graphics.Bitmap;

import com.actionbarsherlock.app.SherlockActivity;
import com.googlecode.androidannotations.annotations.*;
import com.joanzapata.pdfview.PDFRenderer;
import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnPageChangeListener;
import com.joanzapata.pdfview.listener.OnTapListener;
import com.joanzapata.pdfview.sample.R;
import com.joanzapata.pdfview.util.FileUtils;

import java.io.File;
import java.io.IOException;

import static java.lang.String.format;

@EActivity(R.layout.activity_main)
@OptionsMenu(R.menu.actionbar)
public class PDFViewActivity extends SherlockActivity implements OnPageChangeListener,
        OnTapListener {

    public static final String SAMPLE_FILE = "sample.pdf";

    public static final String ABOUT_FILE = "about.pdf";

    @ViewById
    PDFView pdfView;
    PDFView pdfViewDebug;

    @NonConfigurationInstance
    String pdfName = SAMPLE_FILE;

    @NonConfigurationInstance
    Integer pageNumber = 1;

    @AfterViews
    void afterViews() {
        //loadThumbnails(pdfName);
        display(pdfName, false);
    }

    @OptionsItem
    public void about() {
        if (!displaying(ABOUT_FILE))
            display(ABOUT_FILE, true);
    }

    private void loadThumbnails(String assetFileName) {
        File pdfFile = null;
        try {
            pdfFile = FileUtils.fileFromAsset(this, assetFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        PDFRenderer thumbnailPdfRenderer = new PDFRenderer(pdfFile);
        thumbnailPdfRenderer.setMagazineTwoPageView();

        for (int i = 0; i < thumbnailPdfRenderer.getPageCount(); ++i) {
            Bitmap thumbnailBitmap = thumbnailPdfRenderer.renderPage(i, 100);
        }

    }

    private void display(String assetFileName, boolean jumpToFirstPage) {
        if (jumpToFirstPage) pageNumber = 1;
        setTitle(pdfName = assetFileName);

        pdfView.fromAsset(assetFileName)
                .defaultPage(1)
                .onPageChange(this)
                .onTap(this)
                .setOnePageView()
                .load();
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
        setTitle(format("%s %s / %s", pdfName, page, pageCount));
    }

    @Override
    public void onTap(int tapX, int tapY) {

    }

    @Override
    public void onBackPressed() {
        if (ABOUT_FILE.equals(pdfName)) {
            display(SAMPLE_FILE, true);
        } else {
            super.onBackPressed();
        }
    }

    private boolean displaying(String fileName) {
        return fileName.equals(pdfName);
    }

}
