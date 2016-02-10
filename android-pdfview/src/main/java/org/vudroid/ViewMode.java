package org.vudroid;

/**
 * Created by alx on 2015-03-18.
 */
public class ViewMode {

    public static final int ONE_PAGE = 1;
    public static final int TWO_PAGE = 2;

    private static boolean isMagazine = false;
    private static int pageCount = 1;

    public static int get() {
        return pageCount;
    }

    public static void set(int newPageCount) {
        pageCount = newPageCount;
        if (get() == ONE_PAGE) {
            setMagazine(false);
        }
    }

    public static boolean isMagazine() {
        return isMagazine;
    }

    public static void setMagazine(boolean state) {
        isMagazine = state;
    }

}
