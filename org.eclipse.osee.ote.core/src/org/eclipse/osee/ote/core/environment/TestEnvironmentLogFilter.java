/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.core.environment;

import java.util.logging.Level;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.logging.ILoggerFilter;

public class TestEnvironmentLogFilter implements ILoggerFilter {
	private static Level level;
	   static {
		   level = Level.parse(System.getProperty("ote.testenv.filter.level", "INFO"));
	   }
	public Pattern bundleId() {
		return null;
	}

	public Level getLoggerLevel() {
		return level;
	}

	public Pattern name() {
		return null;
	}

}
