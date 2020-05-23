/*********************************************************************
 * Copyright (c) 2012 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

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