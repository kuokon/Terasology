/*
 * Copyright 2014 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.logic.console.commands.adapter;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.module.sandbox.API;
import org.terasology.world.block.family.BlockFamily;

import java.util.Map;

/**
 * @author Limeth
 */
@API
public class CommandParameterAdapterManager {
    private final Map<Class<?>, CommandParameterAdapter> adapters = Maps.newHashMap();

    /**
     * @return A manager with basic adapters for wrapped primitives and {@link String}
     */
    @SuppressWarnings("unchecked")
    public static CommandParameterAdapterManager basic() {
        CommandParameterAdapterManager manager = new CommandParameterAdapterManager();

        for (Map.Entry<Class<?>, CommandParameterAdapter> entry : BasicCommandParameterAdapter.map().entrySet()) {
            manager.registerAdapter(entry.getKey(), entry.getValue());
        }

        return manager;
    }

    /**
     * @return A manager with basic adapters and following classes:
     * {@link org.terasology.entitySystem.prefab.Prefab}
     */
    public static CommandParameterAdapterManager core() {
        CommandParameterAdapterManager manager = basic();

        manager.registerAdapter(Prefab.class, new PrefabAdapter());
        manager.registerAdapter(BlockFamily.class, new BlockFamilyAdapter());

        return manager;
    }

    /**
     * @return {@code true}, if the adapter didn't override a previously present adapter
     */
    public <T> boolean registerAdapter(Class<? extends T> clazz, CommandParameterAdapter<T> adapter) {
        return adapters.put(clazz, adapter) == null;
    }

    public boolean isAdapterRegistered(Class<?> clazz) {
        return adapters.containsKey(clazz);
    }

    /**
     * @param clazz The type of the returned object
     * @param composed The string from which to parse
     * @return The parsed object
     * @throws ClassCastException If the {@link CommandParameterAdapter} is linked with an incorrect {@link java.lang.Class}.
     */
    @SuppressWarnings("unchecked")
    public <T> T parse(Class<T> clazz, String composed) throws ClassCastException {
        Preconditions.checkNotNull(composed, "The String to parse must not be null");

        CommandParameterAdapter adapter = getAdapter(clazz);

        Preconditions.checkNotNull(adapter, "No adapter found for " + clazz.getCanonicalName());

        return (T) adapter.parse(composed);
    }

    /**
     * @param parsed The object to compose
     * @param clazz The class pointing to the desired adapter
     * @return The composed object
     * @throws ClassCastException If the {@link CommandParameterAdapter} is linked with an incorrect {@link java.lang.Class}.
     */
    @SuppressWarnings("unchecked")
    public <T> String compose(T parsed, Class<? super T> clazz) throws ClassCastException {
        Preconditions.checkNotNull(parsed, "The Object to compose must not be null");

        CommandParameterAdapter adapter = getAdapter(clazz);

        Preconditions.checkNotNull(adapter, "No adapter found for " + clazz.getCanonicalName());

        return adapter.compose(parsed);
    }

    /**
     * @param parsed The object to compose
     * @return The composed object
     * @throws ClassCastException If the {@link CommandParameterAdapter} is linked with an incorrect {@link java.lang.Class}.
     */
    @SuppressWarnings("unchecked")
    public String compose(Object parsed) throws ClassCastException {
        Class<?> clazz = parsed.getClass();

        return compose(parsed, (Class<? super Object>) clazz);
    }

    public CommandParameterAdapter getAdapter(Class<?> clazz) {
        return adapters.get(clazz);
    }
}
