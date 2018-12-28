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
package org.eclipse.osee.framework.messaging.rules;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.util.annotation.AnnotationProcessor;
import org.eclipse.osee.framework.jdk.core.util.annotation.FieldAnnotationHandler;
import org.eclipse.osee.framework.messaging.internal.HasMessageService;
import org.eclipse.osee.framework.messaging.internal.MessageConnectionFieldAnnotationHandler;
import org.eclipse.osee.framework.messaging.internal.MessageServiceController;
import org.eclipse.osee.framework.messaging.internal.MessageServiceController.BrokerType;
import org.eclipse.osee.framework.messaging.internal.MessageServiceFieldAnnotationHandler;
import org.junit.Assert;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * @author Roberto E. Escobar
 */
public class MessageBroker implements MethodRule {

   private final String brokerURI;
   private final Object[] objects;
   private final BrokerType brokerType;

   public MessageBroker(BrokerType brokerType, String brokerURI, Object... objects) {
      this.brokerType = brokerType;
      this.brokerURI = brokerURI;
      this.objects = objects;
   }

   @Override
   public Statement apply(final Statement base, final FrameworkMethod method, final Object target) {
      return new Statement() {
         @Override
         public void evaluate() throws Throwable {
            Assert.assertNotNull("BrokerType cannot be null", brokerType);
            Assert.assertNotNull("BrokerURI cannot be null", brokerURI);

            String brokerName = target.getClass().getSimpleName();
            MessageServiceController testMessageBroker =
               new MessageServiceController(brokerType, brokerName, brokerURI);

            AnnotationProcessor processor = createProcessor(testMessageBroker);
            try {
               testMessageBroker.start();
               processor.initAnnotations(objects);
               base.evaluate();
            } finally {
               testMessageBroker.stop();
            }
         }

      };
   }

   private AnnotationProcessor createProcessor(HasMessageService hasMessageService) {
      Map<Class<? extends Annotation>, FieldAnnotationHandler<?>> annotationHandlers =
         new HashMap<>();
      annotationHandlers.put(InjectMessageService.class, new MessageServiceFieldAnnotationHandler(hasMessageService));
      annotationHandlers.put(MessageConnection.class, new MessageConnectionFieldAnnotationHandler(hasMessageService));
      return new AnnotationProcessor(annotationHandlers);
   }
}
