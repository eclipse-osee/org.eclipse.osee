/*********************************************************************
 * Copyright (c) 2025 Boeing
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
package org.eclipse.osee.ats.ide.integration.tests.ui.skynet;

/**
 * @author Jaden W. Puckett
 */
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.eclipse.osee.framework.ui.skynet.preferences.EditorsPreferencePage;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.swt.widgets.Button;
import org.junit.Before;
import org.junit.Test;

public class EditorsPreferencePageTest {

   private EditorsPreferencePage preferencePage;
   private Button artifactEditorButton;
   private Button editButton;
   private Button useServerLinks;

   @Before
   public void setUp() {
      preferencePage = new EditorsPreferencePage();
      artifactEditorButton = mock(Button.class);
      preferencePage.setArtifactEditorButton(artifactEditorButton);
      editButton = mock(Button.class);
      preferencePage.setEditButton(editButton);
      useServerLinks = mock(Button.class);
      preferencePage.setUseServerLinks(useServerLinks);
   }

   @Test
   public void testArtifactEditorToggle() {
      // Simulate enabling Artifact Editor
      when(artifactEditorButton.getSelection()).thenReturn(true);
      preferencePage.performOk();
      assertTrue(RendererManager.isDefaultArtifactEditor());

      // Simulate disabling Artifact Editor
      when(artifactEditorButton.getSelection()).thenReturn(false);
      preferencePage.performOk();
      assertFalse(RendererManager.isDefaultArtifactEditor());
   }
}
