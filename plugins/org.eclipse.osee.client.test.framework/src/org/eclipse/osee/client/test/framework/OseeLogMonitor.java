/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.client.test.framework;

import static java.lang.annotation.ElementType.FIELD;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;

/**
 * Injects instance of {@link SevereLoggingMonitor} object used by {@link OseeLogMonitorRule}
 * 
 * @author Roberto E. Escobar
 */
@Target({FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OseeLogMonitor {
   //
}
