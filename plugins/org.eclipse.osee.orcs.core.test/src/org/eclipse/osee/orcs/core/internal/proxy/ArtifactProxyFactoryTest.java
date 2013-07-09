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
package org.eclipse.osee.orcs.core.internal.proxy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactFactory;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.ArtifactWriteable;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link ArtifactProxyFactory}
 * 
 * @author Roberto E. Escobar
 */
public class ArtifactProxyFactoryTest {

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   // @formatter:off
   @Mock private ArtifactReadable readable;
   @Mock private ArtifactFactory artifactFactory;
   @Mock private Artifact artifact;
   @Mock private Artifact otherArtifact;
   // @formatter:on

   private String guid;
   private final IOseeBranch branch = CoreBranches.COMMON;
   private final IArtifactType artifactType = CoreArtifactTypes.Folder;
   private ArtifactProxyFactory factory;

   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);

      guid = GUID.create();
      factory = new ArtifactProxyFactory(artifactFactory);
   }

   @Test
   public void testCreate() throws Throwable {
      String expectedName = "NAME";

      when(artifactFactory.createArtifact(branch, artifactType, guid)).thenReturn(artifact);
      when(artifactFactory.clone(artifact)).thenReturn(otherArtifact);

      ArtifactWriteable actual = factory.create(branch, artifactType, guid, expectedName);

      verify(artifactFactory).createArtifact(branch, artifactType, guid);
      verify(otherArtifact).setName(expectedName);
      verify(artifact, times(0)).setName(expectedName);

      assertTrue(actual instanceof ProxyWriteable);
      assertTrue(ProxyUtil.isProxy(actual));
      assertEquals(otherArtifact, factory.getProxiedObject(actual));
      assertEquals(artifact, factory.getOriginalObject(actual));
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testCopy() throws Throwable {
      Collection<? extends IAttributeType> types = Mockito.mock(Collection.class);
      when(artifactFactory.copyArtifact(artifact, types, branch)).thenReturn(otherArtifact);

      ArtifactWriteable actual = factory.copy(artifact, types, branch);

      verify(artifactFactory).copyArtifact(artifact, types, branch);

      assertTrue(actual instanceof ProxyWriteable);
      assertTrue(ProxyUtil.isProxy(actual));
      assertEquals(otherArtifact, factory.getProxiedObject(actual));
   }

   @Test
   public void testIntroduce() throws OseeCoreException {
      when(artifactFactory.introduceArtifact(artifact, branch)).thenReturn(otherArtifact);

      ArtifactWriteable actual = factory.introduce(artifact, branch);

      verify(artifactFactory).introduceArtifact(artifact, branch);

      assertTrue(actual instanceof ProxyWriteable);
      assertTrue(ProxyUtil.isProxy(actual));
      assertEquals(otherArtifact, factory.getProxiedObject(actual));
   }

   @Test
   public void testCreateReadable() {
      ArtifactReadable actual = factory.createReadable(artifact);

      assertFalse(actual instanceof ProxyWriteable);
      assertTrue(actual instanceof HasProxiedObject);
      assertTrue(ProxyUtil.isProxy(actual));
      assertEquals(artifact, factory.getProxiedObject(actual));
   }

   @Test
   public void testCreateWriteable() throws OseeCoreException {
      ArtifactWriteable actual = factory.createWriteable(artifact);

      assertTrue(actual instanceof ProxyWriteable);
      assertTrue(ProxyUtil.isProxy(actual));
      assertEquals(artifact, factory.getProxiedObject(actual));
      assertEquals(artifact, factory.getOriginalObject(actual));
   }

   @Test
   public void testAsProxyWriteable() throws OseeCoreException {
      ArtifactWriteable proxied = factory.createWriteable(artifact);
      ProxyWriteable<Artifact> actual = factory.asProxyWriteable(proxied);
      assertNotNull(actual);

      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage(new RegExMatcher("Unable to convert from \\[Artifact(.*?)\\] to ProxyWriteable"));
      factory.asProxyWriteable(artifact);
   }

   @Test
   public void testAsWriteable() throws OseeCoreException {
      ArtifactWriteable actual = factory.asWriteable(artifact);
      assertTrue(actual != artifact);
      assertTrue(ProxyUtil.isProxy(actual));

      ArtifactWriteable actual2 = factory.asWriteable(actual);
      assertTrue(actual2 == actual);

      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage(new RegExMatcher("Unable to convert from \\[ArtifactReadable(.*?)\\] to Writeable"));
      factory.asWriteable(readable);
   }

   @Test
   public void testAsWriteableWithNull() throws OseeCoreException {
      thrown.expect(OseeArgumentException.class);
      thrown.expectMessage("Unable to convert from [null] to Writeable");
      factory.asWriteable(null);
   }

   private static final class RegExMatcher extends BaseMatcher<String> {

      private final Matcher matcher;

      public RegExMatcher(String regEx) {
         this.matcher = Pattern.compile(regEx).matcher("");
      }

      @Override
      public void describeTo(Description description) {
         // nothing
      }

      @Override
      public boolean matches(Object item) {
         String value = (String) item;
         matcher.reset(value);
         return matcher.find();
      }
   };
}
