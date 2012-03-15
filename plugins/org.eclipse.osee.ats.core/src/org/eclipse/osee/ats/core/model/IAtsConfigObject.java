/*
 * Created on Feb 13, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.model;

/**
 * @author Donald G. Dunne
 */
public interface IAtsConfigObject extends IAtsObject {

   public IAtsUserGroup getPrivilegedEditors();

   public IAtsUserGroup getLeads();

   public IAtsUserGroup getMembers();
}
