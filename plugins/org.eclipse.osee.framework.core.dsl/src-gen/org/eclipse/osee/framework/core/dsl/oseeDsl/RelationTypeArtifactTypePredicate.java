/**
 */
package org.eclipse.osee.framework.core.dsl.oseeDsl;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Relation Type Artifact Type Predicate</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeArtifactTypePredicate#getArtifactTypeRef <em>Artifact Type Ref</em>}</li>
 * </ul>
 *
 * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getRelationTypeArtifactTypePredicate()
 * @model
 * @generated
 */
public interface RelationTypeArtifactTypePredicate extends RelationTypePredicate
{
  /**
   * Returns the value of the '<em><b>Artifact Type Ref</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Artifact Type Ref</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Artifact Type Ref</em>' reference.
   * @see #setArtifactTypeRef(XArtifactType)
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getRelationTypeArtifactTypePredicate_ArtifactTypeRef()
   * @model
   * @generated
   */
  XArtifactType getArtifactTypeRef();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeArtifactTypePredicate#getArtifactTypeRef <em>Artifact Type Ref</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Artifact Type Ref</em>' reference.
   * @see #getArtifactTypeRef()
   * @generated
   */
  void setArtifactTypeRef(XArtifactType value);

} // RelationTypeArtifactTypePredicate
