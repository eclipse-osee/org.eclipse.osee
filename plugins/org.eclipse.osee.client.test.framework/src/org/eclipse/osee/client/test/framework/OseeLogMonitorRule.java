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

package org.eclipse.osee.client.test.framework;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.client.test.framework.internal.AssertLib;
import org.eclipse.osee.client.test.framework.internal.OseeLogMonitorFieldAnnotationHandler;
import org.eclipse.osee.framework.jdk.core.annotation.AnnotationProcessor;
import org.eclipse.osee.framework.jdk.core.annotation.FieldAnnotationHandler;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.junit.Assert;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * The OseeLogMonitor Rule creates an instance of {@link SevereLoggingMonitor} for each test case. The monitor observes
 * all log events and if it detects an even with a severity level higher than {@link Level.INFO}, it logs a test
 * failure. Can be used with @OseeLogMonitor annotation to get a reference to the monitor during the test.
 * 
 * <pre>
 * public class TestA {
 * 
 *    &#064;Rule
 *    public OseeLogMonitorRule rule = new OseeLogMonitorRule();
 * 
 *    &#064;Test
 *    public void testA() {
 *       OseeLog.log(Test.class, Level.SEVERE, &quot;Log a severe message. Monitor will fail the test&quot;);
 *    }
 * }
 * </pre>
 * 
 * <pre>
 * public class TestB {
 * 
 *    &#064;Rule
 *    public OseeLogMonitorRule rule = new OseeLogMonitorRule();
 * 
 *    &#064;OseeLogMonitor
 *    private SevereLoggingMonitor monitor;
 * 
 *    &#064;Test
 *    public void testA() {
 *       monitor.pause();
 *       OseeLog.log(Test.class, Level.SEVERE, &quot;Log a severe message. Monitor ignores event. Test Passes.&quot;);
 *       monitor.resume();
 *    }
 * }
 * </pre>
 * 
 * @author Roberto E. Escobar
 */
public final class OseeLogMonitorRule implements MethodRule {

   private final String[] logsToIgnore;

   public OseeLogMonitorRule(String... logsToIgnore) {
      this.logsToIgnore = logsToIgnore;
   }

   @Override
   public Statement apply(final Statement base, final FrameworkMethod method, final Object target) {
      return new Statement() {
         @Override
         public void evaluate() throws Throwable {
            SevereLoggingMonitor monitorLog = new SevereLoggingMonitor();
            AnnotationProcessor processor = createProcessor(monitorLog);
            processor.initAnnotations(target);
            try {
               OseeLog.registerLoggerListener(monitorLog);
               base.evaluate();
            } finally {
               OseeLog.unregisterLoggerListener(monitorLog);
               try {
                  AssertLib.assertLogEmpty(monitorLog, logsToIgnore);
               } catch (Throwable ex) {
                  String message = String.format("Log Error detected for [%s:%s]\n%s",
                     target.getClass().getSimpleName(), method.getName(), ex.getLocalizedMessage());
                  Assert.fail(message);
               }
            }
         }
      };
   }

   private AnnotationProcessor createProcessor(final SevereLoggingMonitor monitor) {
      Map<Class<? extends Annotation>, FieldAnnotationHandler<?>> annotationHandlers =
         Collections.<Class<? extends Annotation>, FieldAnnotationHandler<?>> singletonMap(OseeLogMonitor.class,
            new OseeLogMonitorFieldAnnotationHandler(monitor));
      return new AnnotationProcessor(annotationHandlers);
   }
}
