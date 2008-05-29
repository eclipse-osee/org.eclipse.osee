/*
 * Created on May 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets.workflow;

import java.util.Collection;

/**
 * @author Donald G. Dunne
 */
public interface IWorkDefinitionProvider {

   public Collection<WorkItemDefinition> getWorkItemDefinitions();

}
