/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.util;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.ui.skynet.widgets.XListViewer;

/**
 * @author Donald G. Dunne
 */
public class XVersionList extends XListViewer {

   public static final String WIDGET_ID = "XVersionList";

   public XVersionList() {
      this("Versions");
   }

   public XVersionList(String displayLabel) {
      super(displayLabel);
      setLabelProvider(new AtsObjectLabelProvider());
      setContentProvider(new ArrayContentProvider());
   }

   public Collection<IAtsVersion> getSelectedAtsObjects() {
      return Collections.castMatching(IAtsVersion.class, getSelected());
   }

   public void setInputAtsObjects(Collection<? extends IAtsVersion> arts) {
      ArrayList<Object> objs = new ArrayList<>();
      objs.addAll(arts);
      setInput(objs);
   }

}