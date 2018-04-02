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

import android.graphics.Matrix
import android.graphics.RectF
import android.view.View
import android.widget.ImageView


interface IPhotoView {
    /**
     * Returns true if the PhotoView is set to allow zooming of Photos.
     *
     * @return true if the PhotoView allows zooming.
     */
    fun canZoom(): Boolean?

    /**
     * Gets the Display Rectangle of the currently displayed Drawable. The
     * Rectangle is relative to this View and includes all scaling and
     * translations.
     *
     * @return - RectF of Displayed Drawable
     */
    val displayRect: RectF?

    /**
     * Sets the Display Matrix of the currently displayed Drawable. The
     * Rectangle is considered relative to this View and includes all scaling and
     * translations.
     *
     * @return - true if rectangle was applied successfully
     */
    fun setDisplayMatrix(finalMatrix: Matrix): Boolean?

    /**
     * Gets the Display Matrix of the currently displayed Drawable. The
     * Rectangle is considered relative to this View and includes all scaling and
     * translations.
     *
     * @return - true if rectangle was applied successfully
     */
    val displayMatrix: Matrix?

    /**
     * Use [.getMinimumScale] instead, this will be removed in future release
     *
     * @return The current minimum scale level. What this value represents depends on the current [ImageView.ScaleType].
     */


    /**
     * @return The current minimum scale level. What this value represents depends on the current [ImageView.ScaleType].
     */
    /**
     * Sets the minimum scale level. What this value represents depends on the current [ImageView.ScaleType].
     */
    var minimumScale: Float?




    /**
     * @return The current medium scale level. What this value represents depends on the current [ImageView.ScaleType].
     */
    /*
     * Sets the medium scale level. What this value represents depends on the current {@link android.widget.ImageView.ScaleType}.
     */
    var mediumScale: Float?

    /**
     * Use [.getMaximumScale] instead, this will be removed in future release
     *
     * @return The current maximum scale level. What this value represents depends on the current [ImageView.ScaleType].
     */


    /**
     * @return The current maximum scale level. What this value represents depends on the current [ImageView.ScaleType].
     */
    /**
     * Sets the maximum scale level. What this value represents depends on the current [ImageView.ScaleType].
     */
    var maximumScale: Float?

    /**
     * Returns the current scale value
     *
     * @return float - current scale value
     */
    /**
     * Changes the current scale to the specified value.
     *
     * @param scale - Value to scale to
     */
    var scale: Float?

    /**
     * Return the current scale type in use by the ImageView.
     */
    /**
     * Controls how the image should be resized or moved to match the size of
     * the ImageView. Any scaling or panning will happen within the confines of
     * this [ImageView.ScaleType].
     *
     * @param scaleType - The desired scaling mode.
     */
    var scaleType: ImageView.ScaleType?

    /**
     * Whether to allow the ImageView's parent to intercept the touch event when the photo is scroll to it's horizontal edge.
     */
    fun setAllowParentInterceptOnEdge(allow: Boolean)

    /**
     * Register a callback to be invoked when the Photo displayed by this view is long-pressed.
     *
     * @param listener - Listener to be registered.
     */
    fun setOnLongClickListener(listener: View.OnLongClickListener)

    /**
     * Register a callback to be invoked when the Matrix has changed for this
     * View. An example would be the user panning or scaling the Photo.
     *
     * @param listener - Listener to be registered.
     */
    fun setOnMatrixChangeListener(listener: PhotoViewAttacher.OnMatrixChangedListener)

    /**
     * Register a callback to be invoked when the Photo displayed by this View
     * is tapped with a single tap.
     *
     * @param listener - Listener to be registered.
     */
    fun setOnPhotoTapListener(listener: PhotoViewAttacher.OnPhotoTapListener)

    /**
     * Register a callback to be invoked when the View is tapped with a single
     * tap.
     *
     * @param listener - Listener to be registered.
     */
    fun setOnViewTapListener(listener: PhotoViewAttacher.OnViewTapListener)

    /**
     * Changes the current scale to the specified value.
     *
     * @param scale   - Value to scale to
     * @param animate - Whether to animate the scale
     */
    fun setScale(scale: Float, animate: Boolean)

    /**
     * Changes the current scale to the specified value, around the given focal point.
     *
     * @param scale   - Value to scale to
     * @param focalX  - X Focus Point
     * @param focalY  - Y Focus Point
     * @param animate - Whether to animate the scale
     */
    fun setScale(scale: Float, focalX: Float, focalY: Float, animate: Boolean)

    /**
     * Allows you to enable/disable the zoom functionality on the ImageView.
     * When disable the ImageView reverts to using the FIT_CENTER matrix.
     *
     * @param zoomable - Whether the zoom functionality is enabled.
     */
    fun setZoomable(zoomable: Boolean)

    /**
     * Enables rotation via PhotoView internal functions.
     * Name is chosen so it won't collide with View.setRotation(float) in API since 11
     *
     * @param rotationDegree - Degree to rotate PhotoView by, should be in range 0 to 360
     */
    fun setPhotoViewRotation(rotationDegree: Float)

}
