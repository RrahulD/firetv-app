/*
 * Copyright (c) 2017 - present Accedo Broadband AB. All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package hu.accedo.commons.vson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import hu.accedo.commons.vson.OverrideAdapterFactory.GsonOverride;
import hu.accedo.commons.vson.PrimitiveAdapterFactory.PrimitiveParser;

/**
 * A special GsonBuilder, automatically using PrimitiveAdapterFactory, PathAdapterFactory and OverrideAdapterFactory.
 * <p>
 * These Factories need to be set up in a specific order to function properly, so it is advised to use this class
 * instead of manually adding them to your GsonBuilder.
 */
public class Vson {
    protected static final PathAdapterFactory PATH_ADAPTER_FACTORY = new PathAdapterFactory();

    protected GsonBuilder gsonBuilder;

    protected OverrideAdapterFactory overrideAdapterFactory;
    protected PrimitiveAdapterFactory primitiveAdapterFactory = new PrimitiveAdapterFactory();

    protected boolean primitiveAdapterFactoryEnabled = true;
    protected boolean pathAdapterFactoryEnabled = true;

    /**
     * Calls {@link OverrideAdapterFactory}.addOverride()
     * Calling any of the addOverride() methods on {@link Vson} enables OverrideAdapterFactory.
     *
     * @param typeToken the TypeToken of the type this override should work on.
     * @param override  the override defining how the JSON should be modified.
     * @param <T>       the Type this override should work on.
     * @return this {@link Vson} instance for chaining.
     */
    public <T> Vson addOverride(TypeToken<T> typeToken, GsonOverride override) {
        if (overrideAdapterFactory == null) {
            overrideAdapterFactory = new OverrideAdapterFactory();
        }
        overrideAdapterFactory.addOverride(typeToken, override);
        return this;
    }

    /**
     * Calls {@link OverrideAdapterFactory}.addOverride()
     * Calling any of the addOverride() methods on {@link Vson} enables OverrideAdapterFactory.
     *
     * @param baseType the class this override should work on.
     * @param override the override defining how the JSON should be modified.
     * @param <T>      the Type this override should work on.
     * @return this {@link Vson} instance for chaining.
     */
    public <T> Vson addOverride(Class<?> baseType, GsonOverride override) {
        if (overrideAdapterFactory == null) {
            overrideAdapterFactory = new OverrideAdapterFactory();
        }
        overrideAdapterFactory.addOverride(baseType, override);
        return this;
    }

    /**
     * Calls {@link OverrideAdapterFactory}.addHierarchyOverride()
     * Calling any of the addOverride() methods on {@link Vson} enables OverrideAdapterFactory.
     *
     * @param typeToken the TypeToken of the type this override should work on. The override is applied to subtypes of this types aswell.
     * @param override  the override defining how the JSON should be modified.
     * @param <T>       the Type this override should work on.
     * @return this {@link Vson} instance for chaining.
     */
    public <T> Vson addHierarchyOverride(TypeToken<T> typeToken, GsonOverride override) {
        if (overrideAdapterFactory == null) {
            overrideAdapterFactory = new OverrideAdapterFactory();
        }
        overrideAdapterFactory.addHierarchyOverride(typeToken, override);
        return this;
    }

    /**
     * Calls {@link OverrideAdapterFactory}.addHierarchyOverride()
     * Calling any of the addOverride() methods on {@link Vson} enables OverrideAdapterFactory.
     *
     * @param baseType the class this override should work on. The override is applied to subclasses of this class aswell.
     * @param override the override defining how the JSON should be modified.
     * @param <T>      the Type this override should work on.
     * @return this {@link Vson} instance for chaining.
     */
    public <T> Vson addHierarchyOverride(Class<?> baseType, GsonOverride override) {
        if (overrideAdapterFactory == null) {
            overrideAdapterFactory = new OverrideAdapterFactory();
        }
        overrideAdapterFactory.addHierarchyOverride(baseType, override);
        return this;
    }

    /**
     * Calls {@link PrimitiveAdapterFactory}.addPrimitiveParser()
     *
     * @param clazz           the class to parse differently.
     * @param primitiveParser the parser to use to parse the given class.
     * @param <T>             the type of the class. May be primitive.
     * @return this {@link Vson} instance for chaining.
     */
    public <T> Vson addPrimitiveParser(Class<T> clazz, PrimitiveParser<T> primitiveParser) {
        primitiveAdapterFactory.addPrimitiveParser(clazz, primitiveParser);
        return this;
    }

    /**
     * @param enabled if true, {@link PrimitiveAdapterFactory} will be applied. Default is true.
     * @return this {@link Vson} instance for chaining.
     */
    public Vson setPrimitiveAdapterFactoryEnabled(boolean enabled) {
        this.primitiveAdapterFactoryEnabled = enabled;
        return this;
    }

    /**
     * @param enabled if true, {@link PathAdapterFactory} will be applied. Default is true.
     * @return this {@link Vson} instance for chaining.
     */
    public Vson setPathAdapterFactoryEnabled(boolean enabled) {
        this.pathAdapterFactoryEnabled = enabled;
        return this;
    }

    //Most common convenience setters for GsonBuilder

    /**
     * Calls {@link GsonBuilder}.registerTypeAdapter()
     *
     * @param type        the type definition for the type adapter being registered
     * @param typeAdapter This object must implement at least one of the {@link TypeAdapter}, {@link InstanceCreator}, {@link JsonSerializer}, and a {@link JsonDeserializer} interfaces.
     * @return this {@link Vson} instance for chaining.
     */
    public Vson registerTypeAdapter(Type type, Object typeAdapter) {
        gsonBuilder.registerTypeAdapter(type, typeAdapter);
        return this;
    }

    /**
     * Calls {@link GsonBuilder}.registerTypeHierarchyAdapter()
     *
     * @param baseType    the class definition for the type adapter being registered for the base class or interface
     * @param typeAdapter This object must implement at least one of {@link TypeAdapter}, {@link JsonSerializer} or {@link JsonDeserializer} interfaces.
     * @return this {@link Vson} instance for chaining.
     */
    public Vson registerTypeHierarchyAdapter(Class<?> baseType, Object typeAdapter) {
        gsonBuilder.registerTypeHierarchyAdapter(baseType, typeAdapter);
        return this;
    }

    /**
     * Calls {@link GsonBuilder}.registerTypeAdapterFactory()
     *
     * @param factory the {@link TypeAdapterFactory} to be applied. Please note that the order of adding these factories matters, and Vson will apply its own factories to the last place.
     * @return this {@link Vson} instance for chaining.
     */
    public Vson registerTypeAdapterFactory(TypeAdapterFactory factory) {
        gsonBuilder.registerTypeAdapterFactory(factory);
        return this;
    }

    public Vson() {
        this.gsonBuilder = new GsonBuilder();
    }

    /**
     * @param gsonBuilder the GsonBuilder to apply Vson config onto. May be preconfigured.
     */
    public Vson(GsonBuilder gsonBuilder) {
        this.gsonBuilder = gsonBuilder;
    }

    /**
     * * Calls {@link GsonBuilder}.create() after having applied Vson specific configuration and {@link TypeAdapterFactory}-es.
     *
     * @return an instance of Gson configured with the options currently set in this builder.
     */
    public Gson toGson() {
        if (primitiveAdapterFactoryEnabled) {
            gsonBuilder.registerTypeAdapterFactory(primitiveAdapterFactory);
        }

        if (pathAdapterFactoryEnabled) {
            gsonBuilder.registerTypeAdapterFactory(PATH_ADAPTER_FACTORY);
            gsonBuilder.addDeserializationExclusionStrategy(PathAdapterFactory.EXCLUSION_STRATEGY);
        }

        if (overrideAdapterFactory != null) {
            gsonBuilder.registerTypeAdapterFactory(overrideAdapterFactory);
        }

        return gsonBuilder.create();
    }
}
