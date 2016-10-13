/**
 */
package org.eclipse.osee.framework.core.dsl.oseeDsl;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Legacy Relation Type Restriction</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.LegacyRelationTypeRestriction#getPermission <em>Permission</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.LegacyRelationTypeRestriction#getRelationTypeRef <em>Relation Type Ref</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.LegacyRelationTypeRestriction#getRestrictedToSide <em>Restricted To Side</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.LegacyRelationTypeRestriction#getArtifactMatcherRef <em>Artifact Matcher Ref</em>}</li>
 * </ul>
 *
 * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getLegacyRelationTypeRestriction()
 * @model
 * @generated
 */
public interface LegacyRelationTypeRestriction extends EObject
{
  /**
   * Returns the value of the '<em><b>Permission</b></em>' attribute.
   * The literals are from the enumeration {@link org.eclipse.osee.framework.core.dsl.oseeDsl.AccessPermissionEnum}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Permission</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Permission</em>' attribute.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.AccessPermissionEnum
   * @see #setPermission(AccessPermissionEnum)
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getLegacyRelationTypeRestriction_Permission()
   * @model
   * @generated
   */
  AccessPermissionEnum getPermission();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.LegacyRelationTypeRestriction#getPermission <em>Permission</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Permission</em>' attribute.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.AccessPermissionEnum
   * @see #getPermission()
   * @generated
   */
  void setPermission(AccessPermissionEnum value);

  /**
   * Returns the value of the '<em><b>Relation Type Ref</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Relation Type Ref</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Relation Type Ref</em>' reference.
   * @see #setRelationTypeRef(XRelationType)
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getLegacyRelationTypeRestriction_RelationTypeRef()
   * @model
   * @generated
   */
  XRelationType getRelationTypeRef();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.LegacyRelationTypeRestriction#getRelationTypeRef <em>Relation Type Ref</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Relation Type Ref</em>' reference.
   * @see #getRelationTypeRef()
   * @generated
   */
  void setRelationTypeRef(XRelationType value);

  /**
   * Returns the value of the '<em><b>Restricted To Side</b></em>' attribute.
   * The literals are from the enumeration {@link org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationSideEnum}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Restricted To Side</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Restricted To Side</em>' attribute.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationSideEnum
   * @see #setRestrictedToSide(XRelationSideEnum)
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getLegacyRelationTypeRestriction_RestrictedToSide()
   * @model
   * @generated
   */
  XRelationSideEnum getRestrictedToSide();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.LegacyRelationTypeRestriction#getRestrictedToSide <em>Restricted To Side</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Restricted To Side</em>' attribute.
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationSideEnum
   * @see #getRestrictedToSide()
   * @generated
   */
  void setRestrictedToSide(XRelationSideEnum value);

  /**
   * Returns the value of the '<em><b>Artifact Matcher Ref</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Artifact Matcher Ref</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Artifact Matcher Ref</em>' reference.
   * @see #setArtifactMatcherRef(XArtifactMatcher)
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getLegacyRelationTypeRestriction_ArtifactMatcherRef()
   * @model
   * @generated
   */
  XArtifactMatcher getArtifactMatcherRef();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.LegacyRelationTypeRestriction#getArtifactMatcherRef <em>Artifact Matcher Ref</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Artifact Matcher Ref</em>' reference.
   * @see #getArtifactMatcherRef()
   * @generated
   */
  void setArtifactMatcherRef(XArtifactMatcher value);

} // LegacyRelationTypeRestriction
