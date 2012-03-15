/*
 * Created on Mar 6, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.model;

import java.util.List;

/**
 * @author Donald G. Dunne
 */
public interface WorkStateFactory {

   WorkState createStateData(String name, List<? extends IAtsUser> assignees);

   WorkState createStateData(String name);

   WorkState createStateData(String name, List<? extends IAtsUser> assignees, double hoursSpent, int percentComplete);

}
