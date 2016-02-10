package org.vudroid.pdfdroid.codec;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.util.Log;

import org.vudroid.core.codec.CodecPage;

import java.nio.ByteBuffer;

public class PdfPage implements CodecPage
{
    private long pageHandle1;
    private long pageHandle2;
    private long docHandle;
    private int pageCount;
    private boolean singleFirstPage = false;

    private PdfPage(long pageHandle1, long docHandle)
    {
        this.pageHandle1 = pageHandle1;
        this.docHandle = docHandle;
        pageCount = 1;
    }

    private PdfPage(long pageHandle1, long docHandle, boolean isMagazineFirstPage)
    {
        this.pageHandle1 = pageHandle1;
        this.pageHandle2 = 0;
        this.docHandle = docHandle;
        pageCount = 2;
        singleFirstPage = isMagazineFirstPage;
    }

    private PdfPage(long pageHandle1, long pageHandle2, long docHandle)
    {
        this.pageHandle1 = pageHandle1;
        this.pageHandle2 = pageHandle2;
        this.docHandle = docHandle;
        pageCount = 2;
    }

    public boolean isDecoding()
    {
        return false;  //TODO
    }

    public void waitForDecode()
    {
        //TODO
    }

    public int getWidth()
    {
        return (int) getMediaBox(pageHandle1).width() * pageCount;
    }

    public int getHeight()
    {
        return (int) getMediaBox(pageHandle1).height();
    }

    static PdfPage createPage(long dochandle, int pageno)
    {
        return new PdfPage(open(dochandle, pageno), dochandle);
    }

    static PdfPage createPage(long dochandle, int pageno1, int pageno2)
    {
        long page1 = open(dochandle, pageno1);
        long page2;
        try {
            page2 = open(dochandle, pageno2);
        } catch (Throwable e) {
            Log.d("", "End of document", e);
            page2 = 0;
        }
        return new PdfPage(page1, page2, dochandle);
    }

    static PdfPage createMagazineCoverPage(long dochandle, int pageno)
    {
        long page = open(dochandle, pageno);
        return new PdfPage(page, dochandle, true);
    }

    @Override
    protected void finalize() throws Throwable
    {
        recycle();
        super.finalize();
    }

    public synchronized void recycle() {
        if (pageHandle1 != 0) {
            free(pageHandle1);
            pageHandle1 = 0;
        }
        if (pageHandle2 != 0) {
            free(pageHandle2);
            pageHandle2 = 0;
        }
    }

    private RectF getMediaBox(long pageHandle)
    {
        float[] box = new float[4];
        getMediaBox(pageHandle, box);
        return new RectF(box[0], box[1], box[2], box[3]);
    }

    private float[] getMatrixArray(Matrix matrix) {
        float[] matrixSource = new float[9];
        float[] matrixArray = new float[6];
        matrix.getValues(matrixSource);
        matrixArray[0] = matrixSource[0];
        matrixArray[1] = matrixSource[3];
        matrixArray[2] = matrixSource[1];
        matrixArray[3] = matrixSource[4];
        matrixArray[4] = matrixSource[2];
        matrixArray[5] = matrixSource[5];
        return matrixArray;
    }

    private Matrix getMatrix(long pageHandle, int renderAreaWidth, int renderAreaHeight,
                             float boundsLeft, float boundsTop, float boundsWidth, float boundsHeight) {
        Matrix matrix = new Matrix();
        matrix.postScale(renderAreaWidth / getMediaBox(pageHandle).width(),
                -renderAreaHeight / getMediaBox(pageHandle).height());
        matrix.postTranslate(0, renderAreaHeight);
        matrix.postTranslate(-boundsLeft*renderAreaWidth,
                -boundsTop*renderAreaHeight);
        matrix.postScale(1/boundsWidth, 1/boundsHeight);
        return matrix;
    }

    private float normalizeBound(float bound) {
        if (bound < 0) {
            return 0;
        } else if (bound > 1) {
            return 1;
        }
        return bound;
    }

    public Bitmap renderBitmap(int width, int height, RectF pageBounds) {
        if (pageCount == 2 && singleFirstPage) {
            RectF leftPageBounds = new RectF(pageBounds);
            leftPageBounds.right = Math.min(4 * pageBounds.right, 1);
            leftPageBounds.left = Math.min(4 * pageBounds.left, 1);
            int leftPageWidth = (int) (leftPageBounds.width() / pageBounds.width() / 4 * width);
            Bitmap leftPageBitmap = render(pageHandle2, leftPageWidth, height, leftPageBounds);

            RectF rightPageBounds = new RectF(pageBounds);
            rightPageBounds.right = Math.max(4 * pageBounds.right - 3, 0);
            rightPageBounds.left = Math.max(4 * pageBounds.left - 3, 0);
            int rightPageWidth = (int) (rightPageBounds.width() / pageBounds.width() / 4 * width);
            Bitmap rightPageBitmap = render(pageHandle2, rightPageWidth, height, rightPageBounds);

            float rightBound = normalizeBound(2 * pageBounds.right - 0.5f);
            float leftBound = normalizeBound(2 * pageBounds.left - 0.5f);
            if (rightBound < leftBound) {
                Log.e("", "center page width neg ");
            }
            RectF centerPageBounds = new RectF(leftBound, pageBounds.top,
                    rightBound, pageBounds.bottom);
            int pageWidth = (int) (centerPageBounds.width() / pageBounds.width() / 2 * width);
            Bitmap pageBitmap = render(pageHandle1, pageWidth, height, centerPageBounds);

            return combineBitmaps(leftPageBitmap, combineBitmaps(pageBitmap, rightPageBitmap));
        } else if (pageCount == 2) {
            RectF leftPageBounds = new RectF(pageBounds);
            leftPageBounds.right = Math.min(2 * pageBounds.right, 1);
            leftPageBounds.left = Math.min(2 * pageBounds.left, 1);
            int leftPageWidth = (int) (leftPageBounds.width() / pageBounds.width() / 2 * width);
            Bitmap leftPageBitmap = render(pageHandle1, leftPageWidth, height, leftPageBounds);

            RectF rightPageBounds = new RectF(pageBounds);
            rightPageBounds.right = Math.max(2 * pageBounds.right - 1, 0);
            rightPageBounds.left = Math.max(2 * pageBounds.left - 1, 0);
            int rightPageWidth = (int) (rightPageBounds.width() / pageBounds.width() / 2 * width);
            Bitmap rightPageBitmap = render(pageHandle2, rightPageWidth, height, rightPageBounds);

            return combineBitmaps(leftPageBitmap, rightPageBitmap);
        } else {
            return render(pageHandle1, width, height, pageBounds);
        }
    }

    public Bitmap render(long pageHandle, int width, int height, RectF pageBounds) {
        if (width == 0) {
            return null;
        }
        // an invisible even page near the last odd page when layout is 2-page
        if (pageHandle == 0) {
            return getTransparentBitmap(width, height);
        }
        int[] mRect = new int[] {0, 0, width, height};
        int[] bufferarray = new int[width * height];
        float[] matrixArray = getMatrixArray(getMatrix(pageHandle, width, height,
                pageBounds.left, pageBounds.top, pageBounds.width(), pageBounds.height()));
        nativeCreateView(docHandle, pageHandle, mRect, matrixArray, bufferarray);
        return Bitmap.createBitmap(bufferarray, width, height, Bitmap.Config.RGB_565);
    }

    private Bitmap getTransparentBitmap(int width, int height) {
        Bitmap transparentBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas transparentCanvas = new Canvas(transparentBitmap);
        transparentCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        return transparentBitmap;
    }

    private Bitmap combineBitmaps(Bitmap leftBitmap, Bitmap rightBitmap) {
        if (leftBitmap == null) {
            if (rightBitmap == null) {
                return null;
            }
            return rightBitmap;
        }
        if (rightBitmap == null) {
            return leftBitmap;
        }
        int combinedBitmapWidth = leftBitmap.getWidth() + rightBitmap.getWidth();
        int combinedBitmapHeight = leftBitmap.getHeight();

        Bitmap combinedBitmap = Bitmap.createBitmap(combinedBitmapWidth, combinedBitmapHeight,
                Bitmap.Config.ARGB_8888);

        Canvas combinedCanvas = new Canvas(combinedBitmap);
        combinedCanvas.drawBitmap(leftBitmap, 0f, 0f, null);
        combinedCanvas.drawBitmap(rightBitmap, leftBitmap.getWidth(), 0f, null);
        return combinedBitmap;
    }

    private static native void getMediaBox(long handle, float[] mediabox);

    private static native void free(long handle);

    private static native long open(long dochandle, int pageno);

    private static native void render(long dochandle, long pagehandle,
		int[] viewboxarray, float[] matrixarray,
		ByteBuffer byteBuffer, ByteBuffer tempBuffer);

    private native void nativeCreateView(long dochandle, long pagehandle,
		int[] viewboxarray, float[] matrixarray,
		int[] bufferarray);
}
