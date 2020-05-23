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