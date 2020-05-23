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

package org.eclipse.osee.framework.ui.skynet.widgets.xmerge;

import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.Collections;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osee.framework.core.enums.ConflictStatus;
import org.eclipse.osee.framework.skynet.core.conflict.ArtifactConflict;
import org.eclipse.osee.framework.skynet.core.conflict.AttributeConflict;
import org.eclipse.osee.framework.skynet.core.conflict.Conflict;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author David W. Miller
 */
public class MenuSelectionEnabledHandlerTest {

   private static StructuredSelection emptySelection;
   private static StructuredSelection oneAttributeSelection;
   private static StructuredSelection oneArtifactSelection;
   private static StructuredSelection manySelection;
   private final ConflictStatus status = ConflictStatus.UNTOUCHED;

   // @formatter:off
   @Mock private AttributeConflict attributeC;
   @Mock private ArtifactConflict artifactC;
   // @formatter:on

   private static MenuSelectionEnabledHandler handler = new MenuSelectionEnabledHandler() {

      @Override
      protected Object executeWithException(ExecutionEvent event, IStructuredSelection selection) {
         // empty for testing
         return null;
      }

      @Override
      void executeWithException(AttributeConflict attributeConflict) {
         // empty for testing
      }
   };

   @Before
   public void init() {
      MockitoAnnotations.initMocks(this);
   }

   @Test
   public void testEmptySelection() {
      emptySelection = new StructuredSelection(Collections.EMPTY_LIST);
      boolean result = handler.isEnabledWithException(emptySelection);
      Assert.assertFalse(result);
   }

   @Test
   public void testSingleAttributeConflictSelection() {
      when(attributeC.getAdapter(Conflict.class)).thenReturn(attributeC);
      when(attributeC.getStatus()).thenReturn(status);
      oneAttributeSelection = new StructuredSelection(attributeC);
      boolean result = handler.isEnabledWithException(oneAttributeSelection);
      Assert.assertTrue(result);
   }

   @Test
   public void testSingleArtifactConflictSelection() {
      when(artifactC.getAdapter(Conflict.class)).thenReturn(artifactC);
      when(artifactC.getStatus()).thenReturn(status);
      oneArtifactSelection = new StructuredSelection(artifactC);
      boolean result = handler.isEnabledWithException(oneArtifactSelection);
      Assert.assertFalse(result);
   }

   @Test
   public void testManySelection() {
      manySelection = new StructuredSelection(Arrays.asList("one", "two"));
      boolean result = handler.isEnabledWithException(manySelection);
      Assert.assertFalse(result);
   }
}