/**
 * <copyright>
 * </copyright>
 *

 */
package org.eclipse.osee.framework.core.dsl.oseeDsl;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Artifact Type Restriction</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactTypeRestriction#getArtifactType <em>Artifact Type
 * </em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getArtifactTypeRestriction()
 * @model
 * @generated
 */
public interface ArtifactTypeRestriction extends ObjectRestriction {
   /**
    * Returns the value of the '<em><b>Artifact Type</b></em>' reference. <!-- begin-user-doc -->
    * <p>
    * If the meaning of the '<em>Artifact Type</em>' reference isn't clear, there really should be more of a description
    * here...
    * </p>
    * <!-- end-user-doc -->
    * 
    * @return the value of the '<em>Artifact Type</em>' reference.
    * @see #setArtifactType(XArtifactType)
    * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getArtifactTypeRestriction_ArtifactType()
    * @model
    * @generated
    */
   XArtifactType getArtifactType();

   /**
    * Sets the value of the '{@link org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactTypeRestriction#getArtifactType
    * <em>Artifact Type</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @param value the new value of the '<em>Artifact Type</em>' reference.
    * @see #getArtifactType()
    * @generated
    */
   void setArtifactType(XArtifactType value);

} // ArtifactTypeRestriction
