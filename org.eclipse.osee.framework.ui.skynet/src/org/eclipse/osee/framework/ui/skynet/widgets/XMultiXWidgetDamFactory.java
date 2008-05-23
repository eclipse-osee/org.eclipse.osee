/*
 * Created on May 22, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.List;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.Result;

/**
 * @author Donald G. Dunne
 */
public abstract class XMultiXWidgetDamFactory {

   protected final Artifact artifact;

   public XMultiXWidgetDamFactory(Artifact artifact) {
      this.artifact = artifact;
   }

   /**
    * Create new XWidget with default value in response to new attribute request
    * 
    * @param artifact
    * @return
    */
   public abstract XWidget addXWidgetDam();

   /**
    * Create XWidgets off artifact attributes that already exist
    * 
    * @param artifact
    * @return
    */
   public abstract List<XWidget> createXWidgets();

   /**
    * Call to save xWidget data to artifact. Up to implementation to determine which already exist, which are new and
    * which need to be added.
    * 
    * @param xWidgets
    */
   public abstract void saveToArtifact(List<XWidget> xWidgets);

   /**
    * Determine if artifact is dirty
    * 
    * @return
    * @throws Exception
    */
   public abstract Result isDirty(List<XWidget> xWidgets) throws Exception;
}
