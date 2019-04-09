/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.mock.internal;

import java.lang.reflect.Field;
import org.eclipse.osee.framework.jdk.core.util.annotation.AbstractFieldAnnotationHandler;
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
