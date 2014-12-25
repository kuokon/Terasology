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
package org.terasology.logic.console;

import org.terasology.logic.console.commands.Command;

import java.util.HashMap;

/**
 * Created by Limeth on 10.12.2014.
 *
 * Contains {@link org.terasology.logic.console.commands.AbstractCommand}s ordered by priority.
 */
public class CommandRegistry extends HashMap<String, Command> {
	@Override
	public Command get(Object key) {
		return super.get(key.toString().toLowerCase());
	}

	@Override
	public Command put(String key, Command value) {
		return super.put(key.toLowerCase(), value);
	}

	@Override
	public boolean containsKey(Object key) {
		return super.containsKey(key.toString().toLowerCase());
	}

	@Override
	public Command remove(Object key) {
		return super.remove(key.toString().toLowerCase());
	}
}
