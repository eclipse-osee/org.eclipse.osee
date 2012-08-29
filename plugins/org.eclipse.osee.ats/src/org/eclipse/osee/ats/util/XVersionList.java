package org.eclipse.osee.ats.util;

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
      ArrayList<Object> objs = new ArrayList<Object>();
      objs.addAll(arts);
      setInput(objs);
   }

}