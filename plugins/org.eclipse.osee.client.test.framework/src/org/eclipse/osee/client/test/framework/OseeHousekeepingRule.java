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
package org.eclipse.osee.client.test.framework;

import java.util.Collection;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.junit.Assert;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * Not related to {@link org.eclipse.osee.framework.jdk.core.text.Rule}. <br/>
 * <br/>
 * Checks Artifact Cache for dirty artifacts. Executes after a passing test. Fails tests that have passed but left dirty
 * artifacts in the cache. <br/>
 * <br/>
 * In the future the behavior of this class could be modified to clean up after a test, regardless of test context.
 * 
 * @author Roberto E. Escobar
 */
public final class OseeHousekeepingRule implements MethodRule {

   private static final String MSG = "\n[%s] of type [%s] found while executing: %s.%s()";
   private static final String DIRTY_ARTIFACTS_IN_ARTIFACT_CACHE =
      OseeHousekeepingRule.class.getSimpleName() + " Dirty artifacts in Artifact Cache:";

   @Override
   public Statement apply(final Statement base, final FrameworkMethod method, final Object target) {
      return new Statement() {
         @Override
         public void evaluate() throws Throwable {
            base.evaluate();
            verify(method.getName(), target.getClass().getName());
         }
      };
   }

   private void verify(String methodName, String className) throws Throwable {
      Collection<Artifact> dirtyArtifacts = ArtifactCache.getDirtyArtifacts();

      if (!dirtyArtifacts.isEmpty()) {
         StringBuilder entireMessage = new StringBuilder(DIRTY_ARTIFACTS_IN_ARTIFACT_CACHE);
         for (Artifact artifact : dirtyArtifacts) {
            String message = String.format(MSG, artifact.getName(), artifact.getArtifactType(), className, methodName);
            entireMessage.append(message);

            entireMessage.append("\nDirty report:[");
            entireMessage.append(Artifacts.getDirtyReport(artifact));
            entireMessage.append(")]\r\n");
         }
         Assert.fail(entireMessage.toString());
      }
   }
}
