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

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * The TestInfo Rule makes the current test name available inside test methods:
 * 
 * <pre>
 * public class Test {
 *    &#064;Rule
 *    public TestName name = new TestName();
 * 
 *    &#064;Test
 *    public void testA() {
 *       assertEquals(&quot;testA&quot;, name.getMethodName());
 *    }
 * 
 *    &#064;Test
 *    public void testB() {
 *       assertEquals(&quot;testB&quot;, name.getQualifiedTestName());
 *    }
 * 
 *    &#064;Test
 *    public void testB() {
 *       assertEquals(&quot;testB&quot;, name.getTestClassName());
 *    }
 * }
 * </pre>
 * 
 * @author Roberto E. Escobar
 */
public class TestInfo implements MethodRule {

   private String methodName;
   private String fullName;
   private String testClassName;

   public String getTestClassName() {
      return testClassName;
   }

   public String getQualifiedTestName() {
      return fullName;
   }

   public String getTestName() {
      return methodName;
   }

   @Override
   public Statement apply(final Statement base, final FrameworkMethod method, final Object target) {
      return new Statement() {
         @Override
         public void evaluate() throws Throwable {
            methodName = method.getName();
            testClassName = target.getClass().getSimpleName();
            fullName = String.format("%s_%s", testClassName, methodName);
            base.evaluate();
         }
      };
   }
}
