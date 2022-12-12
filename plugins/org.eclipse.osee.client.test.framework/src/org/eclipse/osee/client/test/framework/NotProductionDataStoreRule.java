/*********************************************************************
 * Copyright (c) 2022 Boeing
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

import static org.junit.Assert.assertFalse;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * This JUnit test rule can be used to prevent a test suite from being run on upon a production database.
 * <dl>
 * <dt>ClassRule</dt>
 * <dd>Using the class as a {@link ClassRule} will protect the method annotated with {@link BeforeClass} from doing
 * something disastrous to a production database.</dd>
 * <dt>Rule</dt>
 * <dd>Using the class as a {@link Rule} will protect the methods annotated with {@link Test} from doing something
 * disastrous to a production database.</dd>
 * </dl>
 *
 * <pre>
 * public class TestA {
 *
 *    &#064;ClassRule
 *    public static NotProductionDataStoreRule classRule = new NotProductionDataStoreRule();
 *
 *    &#064;Rule
 *    public NotProductionDataStoreRule testRule = new NotProductionDataStoreRule();
 * 
 *    &#064;BeforeClass
 *    public void testSetup() {
 *       Artifact artifact =
 *          ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, DemoSawBuilds.SAW_Bld_1, &quot;Folder&quot;);
 *       artifact.persist(&quot;write to db&quot;);
 *    }
 * 
 *    &#064;Test
 *    public void testA() {
 *       Artifact artifact =
 *          ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, DemoSawBuilds.SAW_Bld_1, &quot;Folder&quot;);
 *       artifact.persist(&quot;write to db&quot;);
 *    }
 * }
 * </pre>
 *
 * @author Loren K. Ashley
 */

public class NotProductionDataStoreRule implements TestRule {

   public NotProductionDataStoreRule() {
   }

   @Override
   public Statement apply(Statement base, Description description) {

      return new Statement() {
         @Override
         public void evaluate() throws Throwable {

            NotProductionDataStoreRule.checkNotProductionDataStore();

            base.evaluate();
         }
      };
   }

   /**
    * Verifies the server is not connected to a production database.
    *
    * @throws AssertionError when the server is connected to a production database.
    */

   private static void checkNotProductionDataStore() {
      assertFalse("Not to be run on a production database.", ClientSessionManager.isProductionDataStore());
   }

}

/* EOF */
