/**
 */
package org.eclipse.osee.orcs.script.dsl.orcsScriptDsl;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Os Branch Archived Criteria</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchArchivedCriteria#getFilter <em>Filter</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsBranchArchivedCriteria()
 * @model
 * @generated
 */
public interface OsBranchArchivedCriteria extends OsBranchCriteria {
   /**
    * Returns the value of the '<em><b>Filter</b></em>' attribute. The literals are from the enumeration
    * {@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchArchiveFilter}. <!-- begin-user-doc -->
    * <p>
    * If the meaning of the '<em>Filter</em>' attribute isn't clear, there really should be more of a description
    * here...
    * </p>
    * <!-- end-user-doc -->
    * 
    * @return the value of the '<em>Filter</em>' attribute.
    * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchArchiveFilter
    * @see #setFilter(OsBranchArchiveFilter)
    * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsBranchArchivedCriteria_Filter()
    * @model
    * @generated
    */
   OsBranchArchiveFilter getFilter();

   /**
    * Sets the value of the '{@link org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchArchivedCriteria#getFilter
    * <em>Filter</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @param value the new value of the '<em>Filter</em>' attribute.
    * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OsBranchArchiveFilter
    * @see #getFilter()
    * @generated
    */
   void setFilter(OsBranchArchiveFilter value);

} // OsBranchArchivedCriteria
