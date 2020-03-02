/**
 * Provides classes that implement the branch interface. In particular, we are adding design information to the package
 * info for this package.
 * <p>
 * <br/>
 * The following rules describe the cases for relation changes that should be added to the change set when a change
 * delta is calculated. <br/>
 * Given: <br/>
 * source = the working change set <br/>
 * destination = a representation of a change that has already been accepted, from a common point aligned with the
 * source <br/>
 * Relations should be shown in the merge branch for the following cases: <br/>
 * <ul>
 * <li>New relation to an artifact in source where destination has that artifact on either SideA or SideB deleted. <br/>
 * Resolution: info item stating that the new relation in the source will be deleted once the merge is completed. <br/>
 * </li>
 * <li>New relation to an artifact in destination where the source has that artifact on either SideA or SideB deleted.
 * <br/>
 * Resolution: require user to fix the relation so it does not link to a deleted artifact in the source. <br/>
 * </li>
 * <li>New relation to artifact in the source of the same type as a new relation to the same artifact in the
 * destination, <br/>
 * where the relation is the same (same relation = sideA artifact, sideB artifact and relation type are the same). <br/>
 * Resolution: show all duplicate relations in the merge manager to allow the user to delete the duplicated relations
 * <br/>
 * </li>
 * <li>New relation to artifact in the source of the same type as a new relation to the same artifact in the
 * destination, <br/>
 * where the relation is not the same (same relation = sideA artifact, sideB artifact and relation type are the same).
 * <br/>
 * Resolution: allow relations without conflict as long as multiplicity checks are correct. <br/>
 * </li>
 * <li>New relation to an artifact in the source where the destination would fail a multiplicity check or vice versa.
 * <br/>
 * Resolution: show the relation with a note stating that there is a multiplicity check conflict. <br/>
 * (one to many failure, many to one failure, one to one failure) <br/>
 * </li>
 * <li>New relation to an artifact in the source where the destination would fail a multiplicity check, <br/>
 * but for a deletion that is also in the source. (anti conflict case) <br/>
 * Resolution: no action in merge manager, but test to make sure that it does not come up as a conflict. <br/>
 * </li>
 * </ul>
 */
package org.eclipse.osee.orcs.core.internal;