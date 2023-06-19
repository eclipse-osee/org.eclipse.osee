/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.framework.core.operation;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.osee.framework.core.enums.OperationBehavior;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link OperationBuilder}
 * 
 * @author Roberto E. Escobar
 */
public class OperationBuilderTest {

   private static final double MONITOR_SCALE = 1000.0;
   private static final int ONE_THIRD_MONITOR = toTicks(1.0 / 3.0);
   private static final int QUARTER_MONITOR = toTicks(0.25);

   private static final String OPERATION1 = "Operation1";
   private static final String OPERATION2 = "Operation2";
   private static final String OPERATION3 = "Operation3";
   private static final String OPERATION4 = "Operation4";
   private static final String OPERATION5 = "Operation5";

   @Rule
   public TestName testName = new TestName();

   //@formatter:off
   @Mock private OperationLogger logger;
   @Mock private IOperation op1;
   @Mock private IOperation op2;
   @Mock private IOperation op3;
   @Mock private IOperation op4;
   @Mock private IOperation op5;
   @Mock private IProgressMonitor monitor;
   //@formatter:on

   private OperationBuilder builder;

   @Before
   public void setUp() {
      MockitoAnnotations.initMocks(this);
      builder = Operations.createBuilder(testName.getMethodName());

      when(op1.getName()).thenReturn(OPERATION1);
      when(op2.getName()).thenReturn(OPERATION2);
      when(op3.getName()).thenReturn(OPERATION3);
      when(op4.getName()).thenReturn(OPERATION4);
      when(op5.getName()).thenReturn(OPERATION5);
   }

   @Test
   public void testGetName() {
      Assert.assertEquals(testName.getMethodName(), builder.getName());

      IOperation op = builder.build();
      Assert.assertEquals(testName.getMethodName(), op.getName());
   }

   @Test
   public void testOperationLogger() {
      Assert.assertEquals(NullOperationLogger.getSingleton(), builder.getLogger());

      IOperation op = builder.build();
      Assert.assertEquals(NullOperationLogger.getSingleton(), op.getLogger());

      builder.logger(logger);
      Assert.assertEquals(logger, builder.getLogger());

      IOperation op2 = builder.build();
      Assert.assertEquals(logger, op2.getLogger());

      // Resets after build
      Assert.assertEquals(NullOperationLogger.getSingleton(), op.getLogger());
   }

   @Test
   public void testExecutionBehaviorTerminateOnError() {
      Assert.assertEquals(OperationBehavior.TerminateOnError, builder.getExecutionBehavior());

      builder.addOp(op1);
      builder.addOp(op2);
      builder.addOp(op3);

      when(op1.run(any(SubMonitor.class))).thenReturn(Status.OK_STATUS);

      IStatus errorStatus = createErrorStatus("Error on 3");
      when(op2.run(any(SubMonitor.class))).thenReturn(errorStatus);

      InOrder inOrder = inOrder(monitor);

      IOperation op = builder.build();
      IStatus actualStatus = Operations.executeWork(op, monitor);

      assertEquals(false, actualStatus.isMultiStatus());
      assertEquals(IStatus.ERROR, actualStatus.getSeverity());
      assertEquals(errorStatus, actualStatus);

      verify(op1).run(any(SubMonitor.class));
      verify(op2).run(any(SubMonitor.class));
      verify(op3, never()).run(any(SubMonitor.class));

      inOrder.verify(monitor).beginTask(testName.getMethodName(), 1000);
      inOrder.verify(monitor).subTask(OPERATION1);
      inOrder.verify(monitor).worked(ONE_THIRD_MONITOR);
      inOrder.verify(monitor).subTask(OPERATION2);
      inOrder.verify(monitor).worked(ONE_THIRD_MONITOR);
      inOrder.verify(monitor).worked(ONE_THIRD_MONITOR + 1);
      inOrder.verify(monitor).done();
   }

   @Test
   public void testExecutionBehaviorContinueOnError() {
      builder.executionBehavior(OperationBehavior.ContinueOnError);
      assertEquals(OperationBehavior.ContinueOnError, builder.getExecutionBehavior());

      builder.addOp(op1);
      builder.addOp(op2);
      builder.addOp(op3);

      when(op1.run(any(SubMonitor.class))).thenReturn(Status.OK_STATUS);

      IStatus errorStatus = createErrorStatus("Error on 3");
      when(op2.run(any(SubMonitor.class))).thenReturn(errorStatus);
      when(op3.run(any(SubMonitor.class))).thenReturn(Status.OK_STATUS);

      InOrder inOrder = inOrder(monitor);

      IOperation op = builder.build();
      // Resets to defaults after build
      assertEquals(OperationBehavior.TerminateOnError, builder.getExecutionBehavior());

      IStatus actualStatus = Operations.executeWork(op, monitor);

      assertEquals(false, actualStatus.isMultiStatus());
      assertEquals(IStatus.ERROR, actualStatus.getSeverity());
      assertEquals(errorStatus, actualStatus);

      verify(op1).run(any(SubMonitor.class));
      verify(op2).run(any(SubMonitor.class));
      verify(op3).run(any(SubMonitor.class));

      inOrder.verify(monitor).beginTask(testName.getMethodName(), 1000);
      inOrder.verify(monitor).subTask(OPERATION1);
      inOrder.verify(monitor).worked(ONE_THIRD_MONITOR);

      inOrder.verify(monitor).subTask(OPERATION2);
      inOrder.verify(monitor).worked(ONE_THIRD_MONITOR);

      inOrder.verify(monitor).subTask(OPERATION3);
      inOrder.verify(monitor).worked(ONE_THIRD_MONITOR);

      inOrder.verify(monitor).worked(1);
      inOrder.verify(monitor).done();
   }

   @Test
   public void testOperationsWithWeights() {
      builder.addOp(0.25, op1);
      builder.addOp(0.25, op2);
      builder.addOp(0.50, op3);

      when(op1.run(any(SubMonitor.class))).thenReturn(Status.OK_STATUS);
      when(op2.run(any(SubMonitor.class))).thenReturn(Status.OK_STATUS);
      when(op3.run(any(SubMonitor.class))).thenReturn(Status.OK_STATUS);

      InOrder inOrder = inOrder(monitor);

      IOperation op = builder.build();
      IStatus actualStatus = Operations.executeWork(op, monitor);

      assertEquals(false, actualStatus.isMultiStatus());
      assertEquals(IStatus.OK, actualStatus.getSeverity());

      verify(op1).run(any(SubMonitor.class));
      verify(op2).run(any(SubMonitor.class));
      verify(op3).run(any(SubMonitor.class));

      inOrder.verify(monitor).beginTask(testName.getMethodName(), 1000);
      inOrder.verify(monitor).subTask(OPERATION1);
      inOrder.verify(monitor).worked(toTicks(0.25) - 1);

      inOrder.verify(monitor).subTask(OPERATION2);
      inOrder.verify(monitor).worked(toTicks(0.25));

      inOrder.verify(monitor).subTask(OPERATION3);
      inOrder.verify(monitor).worked(toTicks(0.50));

      inOrder.verify(monitor).worked(1);
      inOrder.verify(monitor).done();
   }

   @Test
   public void testOperationsSomeWithWeights() {
      builder.addOp(0.25, op1);
      builder.addOp(op2);
      builder.addOp(op3);
      builder.addOp(0.30, op4);
      builder.addOp(0.10, op5);

      when(op1.run(any(SubMonitor.class))).thenReturn(Status.OK_STATUS);
      when(op2.run(any(SubMonitor.class))).thenReturn(Status.OK_STATUS);
      when(op3.run(any(SubMonitor.class))).thenReturn(Status.OK_STATUS);
      when(op4.run(any(SubMonitor.class))).thenReturn(Status.OK_STATUS);
      when(op5.run(any(SubMonitor.class))).thenReturn(Status.OK_STATUS);

      InOrder inOrder = inOrder(monitor);

      IOperation op = builder.build();
      IStatus actualStatus = Operations.executeWork(op, monitor);

      assertEquals(false, actualStatus.isMultiStatus());
      assertEquals(IStatus.OK, actualStatus.getSeverity());

      verify(op1).run(any(SubMonitor.class));
      verify(op2).run(any(SubMonitor.class));
      verify(op3).run(any(SubMonitor.class));
      verify(op4).run(any(SubMonitor.class));
      verify(op5).run(any(SubMonitor.class));

      inOrder.verify(monitor).beginTask(testName.getMethodName(), 1000);
      inOrder.verify(monitor).subTask(OPERATION1);
      inOrder.verify(monitor).worked(toTicks(0.23));

      inOrder.verify(monitor).subTask(OPERATION2);
      inOrder.verify(monitor).worked(toTicks(0.20));

      inOrder.verify(monitor).subTask(OPERATION3);
      inOrder.verify(monitor).worked(toTicks(0.20));

      inOrder.verify(monitor).subTask(OPERATION4);
      inOrder.verify(monitor).worked(toTicks(0.277));

      inOrder.verify(monitor).subTask(OPERATION5);
      inOrder.verify(monitor).worked(toTicks(0.0923));

      inOrder.verify(monitor).worked(1);
      inOrder.verify(monitor).done();
   }

   @Test
   public void testRescalingOnProgress() {
      builder.addOp(0.75, op1);
      builder.addOp(0.40, op2);
      builder.addOp(op3);

      when(op1.run(any(SubMonitor.class))).thenReturn(Status.OK_STATUS);
      when(op2.run(any(SubMonitor.class))).thenReturn(Status.OK_STATUS);
      when(op3.run(any(SubMonitor.class))).thenReturn(Status.OK_STATUS);

      InOrder inOrder = inOrder(monitor);

      IOperation op = builder.build();
      IStatus actualStatus = Operations.executeWork(op, monitor);

      assertEquals(false, actualStatus.isMultiStatus());
      assertEquals(IStatus.OK, actualStatus.getSeverity());

      verify(op1).run(any(SubMonitor.class));
      verify(op2).run(any(SubMonitor.class));
      verify(op3).run(any(SubMonitor.class));

      inOrder.verify(monitor).beginTask(testName.getMethodName(), 1000);
      inOrder.verify(monitor).subTask(OPERATION1);
      inOrder.verify(monitor).worked(toTicks(0.434));

      inOrder.verify(monitor).subTask(OPERATION2);
      inOrder.verify(monitor).worked(toTicks(0.232));

      inOrder.verify(monitor).subTask(OPERATION3);
      inOrder.verify(monitor).worked(ONE_THIRD_MONITOR);

      inOrder.verify(monitor).worked(1);
      inOrder.verify(monitor).done();
   }

   @Test
   public void testMultiStatus() {
      builder.executionBehavior(OperationBehavior.ContinueOnError);

      builder.addOp(op1);
      builder.addOp(op2);
      builder.addOp(op3);
      builder.addOp(op4);

      IStatus status1 = createStatus(IStatus.OK, "Status 1");
      IStatus status2 = createStatus(IStatus.WARNING, "Status 2");
      IStatus status3 = createStatus(IStatus.OK, "Status 3");
      IStatus status4 = createStatus(IStatus.ERROR, "Status 4");

      when(op1.run(any(SubMonitor.class))).thenReturn(status1);
      when(op2.run(any(SubMonitor.class))).thenReturn(status2);
      when(op3.run(any(SubMonitor.class))).thenReturn(status3);
      when(op4.run(any(SubMonitor.class))).thenReturn(status4);

      InOrder inOrder = inOrder(monitor);

      IOperation op = builder.build();
      IStatus actualStatus = Operations.executeWork(op, monitor);

      assertEquals(true, actualStatus.isMultiStatus());
      IStatus[] stats = actualStatus.getChildren();
      assertEquals(2, stats.length);

      assertEquals(status2, stats[0]);
      assertEquals(status4, stats[1]);

      verify(op1).run(any(SubMonitor.class));
      verify(op2).run(any(SubMonitor.class));
      verify(op3).run(any(SubMonitor.class));
      verify(op4).run(any(SubMonitor.class));

      inOrder.verify(monitor).beginTask(testName.getMethodName(), 1000);
      inOrder.verify(monitor).subTask(OPERATION1);
      inOrder.verify(monitor).worked(QUARTER_MONITOR - 1);

      inOrder.verify(monitor).subTask(OPERATION2);
      inOrder.verify(monitor).worked(QUARTER_MONITOR);

      inOrder.verify(monitor).subTask(OPERATION3);
      inOrder.verify(monitor).worked(QUARTER_MONITOR);

      inOrder.verify(monitor).subTask(OPERATION4);
      inOrder.verify(monitor).worked(QUARTER_MONITOR);

      inOrder.verify(monitor).done();

   }

   private static int toTicks(double value) {
      return (int) (MONITOR_SCALE * value);
   }

   private static IStatus createErrorStatus(String message) {
      return createStatus(IStatus.ERROR, message);
   }

   private static IStatus createStatus(int severity, String message) {
      return new Status(severity, "pluginId", message);
   }
}
