/**
 */
package org.eclipse.osee.orcs.script.dsl.orcsScriptDsl;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Os Tx Query Statement</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxQueryStatement#getData <em>Data</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsTxQueryStatement()
 * @model
 * @generated
 */
public interface OsTxQueryStatement extends OsQuery {
   /**
    * Returns the value of the '<em><b>Data</b></em>' containment reference. <!-- begin-user-doc -->
    * <p>
    * If the meaning of the '<em>Data</em>' containment reference isn't clear, there really should be more of a
    * description here...
    * </p>
    * <!-- end-user-doc -->
    * 
    * @return the value of the '<em>Data</em>' containment reference.
    * @see #setData(OsTxQuery)
    * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsTxQueryStatement_Data()
    * @model containment="true"
    * @generated
    */
   OsTxQuery getData();

   /**
    * Sets the value of the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsTxQueryStatement#getData
    * <em>Data</em>}' containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @param value the new value of the '<em>Data</em>' containment reference.
    * @see #getData()
    * @generated
    */
   void setData(OsTxQuery value);

} // OsTxQueryStatement
