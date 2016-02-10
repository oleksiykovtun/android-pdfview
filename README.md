[![Screenshot of the sample app](https://raw.github.com/JoanZapata/android-pdfview/master/device.png)](https://play.google.com/store/apps/details?id=com.joanzapata.pdfview.sample)

**PDF-View-Android-Vu** - a library which provides a fast PDFView component for Android, with ```animations```, ```gestures```, and ```zoom```. This fork of **Android PDFView** https://github.com/JoanZapata/android-pdfview is based on [VuDroid](https://code.google.com/p/vudroid/) for decoding the PDF file.

This fork supports 2-page viewing.

# Include PDFView in your layout

```xml
<com.joanzapata.pdfview.PDFView
        android:id="@+id/pdfview"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
```

# Load a PDF file

```java
pdfView.fromAsset(pdfName)
    .setTwoPageView()
    .defaultPage(1)
    .showMinimap(false)
    .enableSwipe(true)
    .onDraw(onDrawListener)
    .onLoad(onLoadCompleteListener)
    .onPageChange(onPageChangeListener)
    .load();
```

2-page view options:

* ```setOnePageView``` (default) 1 page of the document per screen
* ```setTwoPageView``` 2 pages per screen, starting from the beginning of the document
* ```setMagazineTwoPageView``` the 1st page of the document alone, then 2 pages per screen, starting from the 2nd and 3rd pages

Other options:

* **note:** ```pages``` may not work (originally: is optional, it allows you to filter and order the pages of the PDF as you need)
* ```onDraw``` is also optional, and allows you to draw something on a provided canvas, above the current page

# License

```
Copyright 2013 Joan Zapata

This file is part of Android-pdfview.

Android-pdfview is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Android-pdfview is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Android-pdfview.  If not, see <http://www.gnu.org/licenses/>.
```
