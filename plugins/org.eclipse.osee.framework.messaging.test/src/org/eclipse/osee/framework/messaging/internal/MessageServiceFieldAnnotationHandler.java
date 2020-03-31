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
package org.eclipse.osee.framework.messaging.internal;

import java.lang.reflect.Field;
import org.eclipse.osee.framework.jdk.core.annotation.AbstractFieldAnnotationHandler;
import org.eclipse.osee.framework.messaging.rules.InjectMessageService;
import org.junit.Assert;

/**
 * @author Roberto E. Escobar
 */
public class MessageServiceFieldAnnotationHandler extends AbstractFieldAnnotationHandler<InjectMessageService> {

   private final HasMessageService hasMessageService;

   public MessageServiceFieldAnnotationHandler(HasMessageService hasMessageService) {
      this.hasMessageService = hasMessageService;
   }

   @Override
   public void handleAnnotation(InjectMessageService annotation, Object object, Field field) throws Exception {
      Object service = hasMessageService.getMessageService();
      Assert.assertNotNull(service);
      injectToFields(annotation, object, field, service);
   }
}