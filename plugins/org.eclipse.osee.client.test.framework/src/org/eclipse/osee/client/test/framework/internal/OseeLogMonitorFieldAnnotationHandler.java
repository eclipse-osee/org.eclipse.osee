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
package org.eclipse.osee.client.test.framework.internal;

import java.lang.reflect.Field;
import org.eclipse.osee.client.test.framework.OseeLogMonitor;
import org.eclipse.osee.framework.jdk.core.annotation.AbstractFieldAnnotationHandler;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;

/**
 * @author Roberto E. Escobar
 */
public class OseeLogMonitorFieldAnnotationHandler extends AbstractFieldAnnotationHandler<OseeLogMonitor> {

   private final SevereLoggingMonitor logMonitor;

   public OseeLogMonitorFieldAnnotationHandler(SevereLoggingMonitor logMonitor) {
      this.logMonitor = logMonitor;
   }

   @Override
   public void handleAnnotation(OseeLogMonitor annotation, Object object, Field field) throws Exception {
      injectToFields(annotation, object, field, logMonitor);
   }
}