/**
 */
package org.eclipse.osee.orcs.script.dsl.orcsScriptDsl;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Os Query</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsQuery#getName <em>Name</em>}</li>
 * <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsQuery#getCollect <em>Collect</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsQuery()
 * @model
 * @generated
 */
public interface OsQuery extends EObject {
   /**
    * Returns the value of the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc -->
    * <p>
    * If the meaning of the '<em>Name</em>' attribute isn't clear, there really should be more of a description here...
    * </p>
    * <!-- end-user-doc -->
    * 
    * @return the value of the '<em>Name</em>' attribute.
    * @see #setName(String)
    * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsQuery_Name()
    * @model
    * @generated
    */
   String getName();

   /**
    * Sets the value of the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsQuery#getName <em>Name</em>}'
    * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @param value the new value of the '<em>Name</em>' attribute.
    * @see #getName()
    * @generated
    */
   void setName(String value);

   /**
    * Returns the value of the '<em><b>Collect</b></em>' containment reference. <!-- begin-user-doc -->
    * <p>
    * If the meaning of the '<em>Collect</em>' containment reference isn't clear, there really should be more of a
    * description here...
    * </p>
    * <!-- end-user-doc -->
    * 
    * @return the value of the '<em>Collect</em>' containment reference.
    * @see #setCollect(OsCollectClause)
    * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsQuery_Collect()
    * @model containment="true"
    * @generated
    */
   OsCollectClause getCollect();

   /**
    * Sets the value of the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsQuery#getCollect <em>Collect</em>}'
    * containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @param value the new value of the '<em>Collect</em>' containment reference.
    * @see #getCollect()
    * @generated
    */
   void setCollect(OsCollectClause value);

} // OsQuery
