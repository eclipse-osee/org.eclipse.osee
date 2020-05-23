/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.orcs.db.mock.internal;

import java.lang.reflect.Field;
import org.eclipse.osee.framework.jdk.core.annotation.AbstractFieldAnnotationHandler;
import org.eclipse.osee.orcs.db.mock.OsgiService;

/**
 * @author Roberto E. Escobar
 */
public class OsgiServiceFieldAnnotationHandler extends AbstractFieldAnnotationHandler<OsgiService> {

   @Override
   public void handleAnnotation(OsgiService annotation, Object object, Field field) throws Exception {
      Thread.sleep(1000);
      Object service = OsgiUtil.getService(field.getType());
      injectToFields(annotation, object, field, service);
   }

}
