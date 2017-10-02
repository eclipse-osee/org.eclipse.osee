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
package org.eclipse.osee.framework.ui.skynet.panels;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.skynet.core.importing.ArtifactExtractorContributionManager;
import org.eclipse.osee.framework.skynet.core.importing.parsers.IArtifactExtractor;
import org.eclipse.osee.framework.skynet.core.importing.parsers.IArtifactExtractorDelegate;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactExtractorSelectPanel {

   private Combo extractorCombo;
   private Combo delegateCombo;
   private Text parserInformation;
   private final ArtifactExtractorContributionManager importContributionManager;
   private final Set<Listener> listeners;
   private IArtifactExtractor selectedParser;

   public ArtifactExtractorSelectPanel(ArtifactExtractorContributionManager importContributionManager) {
      this.listeners = new HashSet<>();
      this.importContributionManager = importContributionManager;
   }

   public IArtifactExtractor getArtifactExtractor() {
      return selectedParser;
   }

   public void setArtifactExtractor(IArtifactExtractor selectedParser) {
      this.selectedParser = selectedParser;
   }

   public void addListener(Listener listener) {
      synchronized (listeners) {
         listeners.add(listener);
      }
   }

   public void removeListener(Listener listener) {
      synchronized (listeners) {
         listeners.remove(listener);
      }
   }

   private void fireSelectionEvent(Event event) {
      for (Listener listener : listeners) {
         listener.handleEvent(event);
      }
   }

   public void createControl(Composite parent) {
      Composite composite = new Composite(parent, SWT.NONE);
      composite.setLayout(ALayout.getZeroMarginLayout(1, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      extractorCombo = new Combo(composite, SWT.SINGLE | SWT.BORDER | SWT.DROP_DOWN);
      extractorCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      delegateCombo = new Combo(composite, SWT.SINGLE | SWT.BORDER | SWT.DROP_DOWN);
      delegateCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      extractorCombo.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            handleExtractorSelection();
         }
      });

      delegateCombo.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            handleDelegateSelection();
         }
      });

      parserInformation = new Text(parent, SWT.MULTI | SWT.H_SCROLL | SWT.WRAP);
      GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
      gd.heightHint = 30;
      gd.widthHint = 50;
      parserInformation.setLayoutData(gd);
      parserInformation.setEditable(false);

      populateData();
   }

   private void populateData() {
      for (IArtifactExtractor extractor : importContributionManager.getExtractors()) {
         String extractorName = extractor.getName();
         extractorCombo.add(extractorName);
         extractorCombo.setData(extractorName, extractor);
      }
      extractorCombo.select(extractorCombo.getItemCount() - 3);
      handleExtractorSelection();
   }

   private void handleExtractorSelection() {
      IArtifactExtractor extractor = null;
      int selected = extractorCombo.getSelectionIndex();
      if (selected >= 0) {
         String key = extractorCombo.getItem(selected);
         Object object = extractorCombo.getData(key);
         if (object instanceof IArtifactExtractor) {
            extractor = (IArtifactExtractor) object;
         }
      }
      if (extractor != null) {
         parserInformation.setText(extractor.getDescription());
         setArtifactExtractor(extractor);
      } else {
         parserInformation.setText("Select a source artifact extractor");
      }

      java.util.List<IArtifactExtractorDelegate> delegates = importContributionManager.getDelegates(extractor);
      if (!delegates.isEmpty()) {
         delegateCombo.removeAll();
         for (int index = 0; index < delegates.size(); index++) {
            IArtifactExtractorDelegate delegate = delegates.get(index);

            delegateCombo.add(delegate.getName());
            delegateCombo.setData(delegate.getName(), delegate);

            if (extractor != null && extractor.isDelegateRequired()) {
               extractor.setDelegate(delegate);
            }
         }
         delegateCombo.select(0);
         if (extractor != null) {
            extractor.setDelegate(delegates.get(0));
         }
      }
      delegateCombo.setEnabled(!delegates.isEmpty());

      Event event = new Event();
      event.widget = extractorCombo;
      fireSelectionEvent(event);
   }

   private void handleDelegateSelection() {
      IArtifactExtractorDelegate delegate = null;
      if (delegateCombo.isVisible()) {
         int index = delegateCombo.getSelectionIndex();
         if (index >= 0) {
            String key = delegateCombo.getItem(index);
            Object object = delegateCombo.getData(key);
            if (object instanceof IArtifactExtractorDelegate) {
               delegate = (IArtifactExtractorDelegate) object;
            }
         }
      }

      IArtifactExtractor extractor = getArtifactExtractor();
      if (extractor != null) {
         if (extractor.isDelegateRequired()) {
            extractor.setDelegate(delegate);
         } else {
            extractor.setDelegate(null);
         }
      }

      Event event = new Event();
      event.widget = delegateCombo;
      fireSelectionEvent(event);
   }
}
