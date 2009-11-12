/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.test.exchange;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.data.DefaultBasicArtifact;
import org.eclipse.osee.framework.core.data.IBasicArtifact;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exchange.BasicArtifactDataTranslator;
import org.eclipse.osee.framework.core.exchange.IDataTranslator;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case For {@link BasicArtifactDataTranslator}
 * 
 * @author Roberto E. Escobar
 */
@RunWith(Parameterized.class)
public class BasicArtifactDataTranslatorTest {

   private final IDataTranslator<IBasicArtifact<?>> translator;
   private final IBasicArtifact<?> data;

   public BasicArtifactDataTranslatorTest(int artId, String guid, String name) {
      this.data = new DefaultBasicArtifact(artId, guid, name);
      this.translator = new BasicArtifactDataTranslator();
   }

   @Test
   public void testTranslation() throws OseeCoreException {
      PropertyStore propertyStore = translator.convert(data);
      IBasicArtifact<?> actual = translator.convert(propertyStore);

      Assert.assertNotSame(data, actual);

      Assert.assertEquals(data.getArtId(), actual.getArtId());
      Assert.assertEquals(data.getGuid(), actual.getGuid());
      Assert.assertEquals(data.getName(), actual.getName());
      Assert.assertEquals(data.getFullArtifact(), actual.getFullArtifact());
   }

   @Parameters
   public static Collection<Object[]> data() {
      List<Object[]> data = new ArrayList<Object[]>();
      for (int index = 1; index <= 5; index++) {
         data.add(new Object[] {index * 10, GUID.create(), "art: " + index});
      }
      data.add(new Object[] {-1, null, null});
      return data;
   }
}
