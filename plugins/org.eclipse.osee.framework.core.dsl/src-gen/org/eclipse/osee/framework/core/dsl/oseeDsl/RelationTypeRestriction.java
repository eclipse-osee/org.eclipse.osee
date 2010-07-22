/**
 * <copyright>
 * </copyright>
 *

 */
package org.eclipse.osee.framework.core.dsl.oseeDsl;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Relation Type Restriction</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeRestriction#getRelationType <em>Relation Type</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeRestriction#getRestrictedTo <em>Restricted To</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getRelationTypeRestriction()
 * @model
 * @generated
 */
public interface RelationTypeRestriction extends ObjectRestriction
{
  /**
   * Returns the value of the '<em><b>Relation Type</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Relation Type</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Relation Type</em>' reference.
   * @see #setRelationType(XRelationType)
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getRelationTypeRestriction_RelationType()
   * @model
   * @generated
   */
  XRelationType getRelationType();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeRestriction#getRelationType <em>Relation Type</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Relation Type</em>' reference.
   * @see #getRelationType()
   * @generated
   */
  void setRelationType(XRelationType value);

  /**
   * Returns the value of the '<em><b>Restricted To</b></em>' attribute list.
   * The list contents are of type {@link org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeSideRestriction}.
   * The literals are from the enumeration {@link org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeSideRestriction}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Restricted To</em>' attribute list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Restricted To</em>' attribute list.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeSideRestriction
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getRelationTypeRestriction_RestrictedTo()
   * @model unique="false"
   * @generated
   */
  EList<RelationTypeSideRestriction> getRestrictedTo();

} // RelationTypeRestriction
