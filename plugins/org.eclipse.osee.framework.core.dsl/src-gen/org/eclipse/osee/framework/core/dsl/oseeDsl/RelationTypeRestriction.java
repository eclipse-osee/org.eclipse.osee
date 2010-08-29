/**
 * <copyright>
 * </copyright>
 *

 */
package org.eclipse.osee.framework.core.dsl.oseeDsl;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Relation Type Restriction</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeRestriction#getRelationTypeRef <em>Relation Type
 * Ref</em>}</li>
 * <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeRestriction#getRestrictedToSide <em>Restricted To
 * Side</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getRelationTypeRestriction()
 * @model
 * @generated
 */
public interface RelationTypeRestriction extends ObjectRestriction {
   /**
    * Returns the value of the '<em><b>Relation Type Ref</b></em>' reference. <!-- begin-user-doc -->
    * <p>
    * If the meaning of the '<em>Relation Type Ref</em>' reference isn't clear, there really should be more of a
    * description here...
    * </p>
    * <!-- end-user-doc -->
    * 
    * @return the value of the '<em>Relation Type Ref</em>' reference.
    * @see #setRelationTypeRef(XRelationType)
    * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getRelationTypeRestriction_RelationTypeRef()
    * @model
    * @generated
    */
   XRelationType getRelationTypeRef();

   /**
    * Sets the value of the '
    * {@link org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeRestriction#getRelationTypeRef
    * <em>Relation Type Ref</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @param value the new value of the '<em>Relation Type Ref</em>' reference.
    * @see #getRelationTypeRef()
    * @generated
    */
   void setRelationTypeRef(XRelationType value);

   /**
    * Returns the value of the '<em><b>Restricted To Side</b></em>' attribute. The literals are from the enumeration
    * {@link org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationSideEnum}. <!-- begin-user-doc -->
    * <p>
    * If the meaning of the '<em>Restricted To Side</em>' attribute isn't clear, there really should be more of a
    * description here...
    * </p>
    * <!-- end-user-doc -->
    * 
    * @return the value of the '<em>Restricted To Side</em>' attribute.
    * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationSideEnum
    * @see #setRestrictedToSide(XRelationSideEnum)
    * @see org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage#getRelationTypeRestriction_RestrictedToSide()
    * @model
    * @generated
    */
   XRelationSideEnum getRestrictedToSide();

   /**
    * Sets the value of the '
    * {@link org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeRestriction#getRestrictedToSide
    * <em>Restricted To Side</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @param value the new value of the '<em>Restricted To Side</em>' attribute.
    * @see org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationSideEnum
    * @see #getRestrictedToSide()
    * @generated
    */
   void setRestrictedToSide(XRelationSideEnum value);

} // RelationTypeRestriction
