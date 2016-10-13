/**
 */
package org.eclipse.osee.framework.core.dsl.oseeDsl;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Artifact Match Restriction</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactMatchRestriction#getArtifactMatcherRef <em>Artifact Matcher Ref</em>}</li>
 * </ul>
 *
 * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getArtifactMatchRestriction()
 * @model
 * @generated
 */
public interface ArtifactMatchRestriction extends ObjectRestriction
{
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
   * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getArtifactMatchRestriction_ArtifactMatcherRef()
   * @model
   * @generated
   */
  XArtifactMatcher getArtifactMatcherRef();

  /**
   * Sets the value of the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactMatchRestriction#getArtifactMatcherRef <em>Artifact Matcher Ref</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Artifact Matcher Ref</em>' reference.
   * @see #getArtifactMatcherRef()
   * @generated
   */
  void setArtifactMatcherRef(XArtifactMatcher value);

} // ArtifactMatchRestriction
