/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.starlight.mobile.android.lib.view.photoview


import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.view.MotionEvent
import android.view.View

object Compat {

    private val SIXTY_FPS_INTERVAL = 1000 / 60

    fun postOnAnimation(view: View, runnable: Runnable) {
        //        if (VERSION.SDK_INT >= 16) {
        //            postOnAnimationJellyBean(view, runnable);
        //        } else {
        view.postDelayed(runnable, SIXTY_FPS_INTERVAL.toLong())
        // }
    }

    //    @TargetApi(16)
    //    private static void postOnAnimationJellyBean(View view, Runnable runnable) {
    //      //  view.postOnAnimation(runnable);
    //    }

    fun getPointerIndex(action: Int): Int {
        return if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB)
            getPointerIndexHoneyComb(action)
        else
            getPointerIndexEclair(action)
    }

    private //    @TargetApi(Build.VERSION_CODES.ECLAIR)
    fun getPointerIndexEclair(action: Int): Int {
        return action and MotionEvent.ACTION_POINTER_ID_MASK shr MotionEvent.ACTION_POINTER_ID_SHIFT
    }

    //    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private fun getPointerIndexHoneyComb(action: Int): Int {
        return action and MotionEvent.ACTION_POINTER_INDEX_MASK shr MotionEvent.ACTION_POINTER_INDEX_SHIFT
    }

}
