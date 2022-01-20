/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright (c) 2016 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package hu.accedo.commons.tools;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import java.util.HashMap;

/**
 * Formally called LubedPainter (Glide=Lube, Picasso=Painter), ImageLoader is a wrapper for common imageloaders like Picasso or Glide.
 * <p>
 * Why is it necessary? Its a good practise to create a static ImageLoader class in your application, and call that instead of referencing
 * your favorite imageloader directly, so that you wouldn't be tied to one ImageLoader or another. So incase you need to swap between
 * libraries you can do it quickly.
 * <p>
 * However, when you need more variations or parameters, these static classes can become quite large, making the swap or just general
 * usage not that easy anymore.
 * <p>
 * ImageLoader provides a builder interface for multiple parameters, just like Picasso or Glide, however when it comes to actually
 * loading the image, you may inject whatever implementation you may like, by implementing the very simple ImageLoader.Implementation interface.
 * <p>
 * In the case you may need parameters that the Builder does not expose, you can use the ".customParameter()" method, adding whatever key-value
 * pair for the implementation to process.
 */
public class ImageLoader {
    private static Implementation defaultImplementation;

    /**
     * Should be called in Application.onCreate()
     *
     * @param defaultImplementation the default Implementation instance to be used when load(url) is called, without specifiying an implementation.
     */
    public static void setDefaultImplementation(Implementation defaultImplementation) {
        ImageLoader.defaultImplementation = defaultImplementation;
    }

    /**
     * @param url the url to be loaded. May be null.
     * @return a Builder instance, where further loading parameters may be applied.
     */
    public static Builder load(String url) {
        return load(url != null ? Uri.parse(url) : null);
    }

    /**
     * @param url            the url to be loaded. May be null.
     * @param implementation the actual implementation to be used for this image request. Should not be null.
     * @return a Builder instance, where further loading parameters may be applied.
     */
    public static Builder load(String url, Implementation implementation) {
        return load(url != null ? Uri.parse(url) : null, implementation);
    }

    /**
     * @param uri the uri to be loaded. May be null.
     * @return a Builder instance, where further loading parameters may be applied.
     */
    public static Builder load(Uri uri) {
        if (defaultImplementation == null) {
            throw new RuntimeException("Make sure to configure defaultImplementation in your Application.onCreate().");
        }

        return new Builder(uri, defaultImplementation);
    }

    /**
     * @param uri            the uri to be loaded. May be null.
     * @param implementation the actual implementation to be used for this image request. Should not be null.
     * @return a Builder instance, where further loading parameters may be applied.
     */
    public static Builder load(Uri uri, Implementation implementation) {
        if (implementation == null) {
            throw new RuntimeException("Implementation provided cannot be null.");
        }
        return new Builder(uri, implementation);
    }

    /**
     * May be called to pause all fetching, if the given implementation supports it.
     */
    public static void pause() {
        if (defaultImplementation == null) {
            throw new RuntimeException("Make sure to configure defaultImplementation in your Application.onCreate().");
        }

        defaultImplementation.pause();
    }

    /**
     * May be called to resume all fetching, if the given implementation supports it.
     */
    public static void resume() {
        if (defaultImplementation == null) {
            throw new RuntimeException("Make sure to configure defaultImplementation in your Application.onCreate().");
        }

        defaultImplementation.resume();
    }

    public static class Builder {
        private Implementation implementation;
        private Params params = new Params();

        /**
         * How and if this parameter is used, depends on the Implementation you have provided.
         *
         * @param placeholderResId the placeholder resource to be used while the image is being loaded.
         * @return this builder instance for parameter chaining
         */
        public Builder placeholder(int placeholderResId) {
            this.params.placeholderResId = placeholderResId;
            return this;
        }

        /**
         * How and if this parameter is used, depends on the Implementation you have provided.
         *
         * @param placeholder the placeholder drawable to be used while the image is being loaded.
         * @return this builder instance for parameter chaining
         */
        public Builder placeholder(Drawable placeholder) {
            this.params.placeholder = placeholder;
            return this;
        }

        /**
         * How and if this parameter is used, depends on the Implementation you have provided.
         *
         * @param fallbackResId the placeholder resource to be used when loading has failed.
         * @return this builder instance for parameter chaining
         */
        public Builder fallback(int fallbackResId) {
            this.params.fallbackResId = fallbackResId;
            return this;
        }

        /**
         * How and if this parameter is used, depends on the Implementation you have provided.
         *
         * @param fallback the placeholder drawable to be used when loading has failed.
         * @return this builder instance for parameter chaining
         */
        public Builder fallback(Drawable fallback) {
            this.params.fallback = fallback;
            return this;
        }

        /**
         * How and if this parameter is used, depends on the Implementation you have provided.
         *
         * @param bitmapConfig the Bitmap.Config to use while loading. Eg: Bitmap.Config.RGB_565
         * @return this builder instance for parameter chaining
         */
        public Builder bitmapConfig(Bitmap.Config bitmapConfig) {
            this.params.bitmapConfig = bitmapConfig;
            return this;
        }

        /**
         * How and if this parameter is used, depends on the Implementation you have provided.
         *
         * @param shouldResize true, if the Implementation should scale the fetched image, to the View provided.
         * @return this builder instance for parameter chaining
         */
        public Builder resize(boolean shouldResize) {
            this.params.resize = shouldResize;
            return this;
        }

        /**
         * How and if this parameter is used, depends on the Implementation you have provided.
         *
         * @param shouldFade true, if the Implementation should apply a fade-in animation to the image loaded.
         * @return this builder instance for parameter chaining
         */
        public Builder fade(boolean shouldFade) {
            this.params.fade = shouldFade;
            return this;
        }

        /**
         * How and if this parameter is used, depends on the Implementation you have provided.
         *
         * @param key   the name of the parameter to add.
         * @param value the value of the parameter to add.
         * @return this builder instance for parameter chaining
         */
        public Builder customParam(String key, Object value) {
            params.customParams.put(key, value);
            return this;
        }

        Builder(Uri uri, Implementation implementation) {
            this.params.uri = uri;
            this.implementation = implementation;
        }

        /**
         * Calls your implementations fetch(params, imageView) method.
         *
         * @param imageView the ImageView to load into.
         */
        public void into(ImageView imageView) {
            implementation.fetch(params, imageView, null);
        }


        /**
         * Calls your implementations fetch(params, imageView, callback) method.
         *
         * @param imageView the ImageView to load into.
         * @param callback the callback with boolean to call when the image is loaded or not.
         */
        public void into(ImageView imageView, Callback<Boolean> callback) {
            implementation.fetch(params, imageView, callback);
        }

        /**
         * Calls your implementations fetch(params, callback, null) method.
         *
         * @param callback the callback to call when the image is loaded.
         */
        public void into(Callback<Bitmap> callback) {
            implementation.fetch(params, callback, null);
        }

        /**
         * Calls your implementations fetch(params, onSuccess, onFailure) method.
         *
         * @param onSuccess the success callback to call when the image is loaded.
         * @param onFailure the failure callback to call when the image is loaded.
         */
        public void into(Callback<Bitmap> onSuccess, Callback<Exception> onFailure) {
            implementation.fetch(params, onSuccess, onFailure);
        }
    }

    /**
     * Defines how the actual ImageLoading should happen. ImageLoader just gathers request parameters, so you may define
     * how you actually load your images with your favorite image loader at a central place. (here)
     */
    public static interface Implementation {
        /**
         * May be called to pause all fetching, if the given implementation supports it.
         */
        public void pause();

        /**
         * May be called to resume all fetching, if the given implementation supports it.
         */
        public void resume();

        /**
         * Should fetch the image specified by the parameters into the given ImageView.
         *
         * @param params    simple holder class with all the params gathered.
         * @param imageView the ImageView to append the loaded image onto. May be null.
         * @param callback the callback with boolean to be called when the image is loaded or not. May be null
         */
        public void fetch(Params params, ImageView imageView, Callback<Boolean> callback);

        /**
         * Should fetch the image specified by the parameters into the given ImageView.
         *
         * @param params    simple holder class with all the params gathered.
         * @param onSuccess the callback to be called when the image is loaded. May be null.
         * @param onFailure the callback to be called when loading has failed. May be null.
         */
        public void fetch(Params params, Callback<Bitmap> onSuccess, Callback<Exception> onFailure);
    }

    /**
     * Holder for any request parameters gathered by the ImageLoader.Builder.
     */
    public static class Params {
        public Uri uri;
        public int placeholderResId = 0;
        public int fallbackResId = 0;
        public Drawable placeholder = null;
        public Drawable fallback = null;
        public Bitmap.Config bitmapConfig = Config.RGB_565;
        public Boolean resize;
        public Boolean fade;
        public HashMap<String, Object> customParams = new HashMap<>();
    }
}
