/**
 * <copyright>
 * </copyright>
 *

 */
package org.eclipse.osee.framework.core.dsl.oseeDsl;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Artifact Instance Restriction</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactInstanceRestriction#getArtifactName <em>Artifact Name
 * </em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getArtifactInstanceRestriction()
 * @model
 * @generated
 */
public interface ArtifactInstanceRestriction extends ObjectRestriction {
   /**
    * Returns the value of the '<em><b>Artifact Name</b></em>' reference. <!-- begin-user-doc -->
    * <p>
    * If the meaning of the '<em>Artifact Name</em>' reference isn't clear, there really should be more of a description
    * here...
    * </p>
    * <!-- end-user-doc -->
    * 
    * @return the value of the '<em>Artifact Name</em>' reference.
    * @see #setArtifactName(XArtifactRef)
    * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getArtifactInstanceRestriction_ArtifactName()
    * @model
    * @generated
    */
   XArtifactRef getArtifactName();

   /**
    * Sets the value of the '
    * {@link org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactInstanceRestriction#getArtifactName
    * <em>Artifact Name</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @param value the new value of the '<em>Artifact Name</em>' reference.
    * @see #getArtifactName()
    * @generated
    */
   void setArtifactName(XArtifactRef value);

} // ArtifactInstanceRestriction
