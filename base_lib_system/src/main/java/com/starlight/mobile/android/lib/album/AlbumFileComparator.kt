package com.starlight.mobile.android.lib.album

import java.io.File
import java.util.Comparator

/**
 * Created by Raleigh on 15/10/12.
 */
class AlbumFileComparator : Comparator<File> {
    override fun compare(lhs: File, rhs: File): Int {
        var result = 0
        if (lhs.lastModified() > rhs.lastModified()) {
            result = -1
        } else if (lhs.lastModified() < rhs.lastModified()) {
            result = 1
        }
        return result
    }
}
