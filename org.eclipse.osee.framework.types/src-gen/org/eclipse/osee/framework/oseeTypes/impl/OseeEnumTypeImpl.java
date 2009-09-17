/**
 * <copyright>
 * </copyright>
 *

 */
package org.eclipse.osee.framework.oseeTypes.impl;

import java.util.Collection;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.osee.framework.oseeTypes.OseeEnumEntry;
import org.eclipse.osee.framework.oseeTypes.OseeEnumType;
import org.eclipse.osee.framework.oseeTypes.OseeTypesPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Osee Enum Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.osee.framework.oseeTypes.impl.OseeEnumTypeImpl#getEnumEntries <em>Enum Entries</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class OseeEnumTypeImpl extends OseeTypeImpl implements OseeEnumType {
   /**
    * The cached value of the '{@link #getEnumEntries() <em>Enum Entries</em>}' containment reference list.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @see #getEnumEntries()
    * @generated
    * @ordered
    */
   protected EList<OseeEnumEntry> enumEntries;

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @generated
    */
   protected OseeEnumTypeImpl() {
      super();
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   protected EClass eStaticClass() {
      return OseeTypesPackage.Literals.OSEE_ENUM_TYPE;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @generated
    */
   public EList<OseeEnumEntry> getEnumEntries() {
      if (enumEntries == null) {
         enumEntries =
               new EObjectContainmentEList<OseeEnumEntry>(OseeEnumEntry.class, this,
                     OseeTypesPackage.OSEE_ENUM_TYPE__ENUM_ENTRIES);
      }
      return enumEntries;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
      switch (featureID) {
         case OseeTypesPackage.OSEE_ENUM_TYPE__ENUM_ENTRIES:
            return ((InternalEList<?>) getEnumEntries()).basicRemove(otherEnd, msgs);
      }
      return super.eInverseRemove(otherEnd, featureID, msgs);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public Object eGet(int featureID, boolean resolve, boolean coreType) {
      switch (featureID) {
         case OseeTypesPackage.OSEE_ENUM_TYPE__ENUM_ENTRIES:
            return getEnumEntries();
      }
      return super.eGet(featureID, resolve, coreType);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @generated
    */
   @SuppressWarnings("unchecked")
   @Override
   public void eSet(int featureID, Object newValue) {
      switch (featureID) {
         case OseeTypesPackage.OSEE_ENUM_TYPE__ENUM_ENTRIES:
            getEnumEntries().clear();
            getEnumEntries().addAll((Collection<? extends OseeEnumEntry>) newValue);
            return;
      }
      super.eSet(featureID, newValue);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public void eUnset(int featureID) {
      switch (featureID) {
         case OseeTypesPackage.OSEE_ENUM_TYPE__ENUM_ENTRIES:
            getEnumEntries().clear();
            return;
      }
      super.eUnset(featureID);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public boolean eIsSet(int featureID) {
      switch (featureID) {
         case OseeTypesPackage.OSEE_ENUM_TYPE__ENUM_ENTRIES:
            return enumEntries != null && !enumEntries.isEmpty();
      }
      return super.eIsSet(featureID);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @not generated
    */
   @Override
   public boolean equals(Object obj) {
      if (obj instanceof OseeEnumType) {
         return super.equals(obj);
      }
      return false;
   }
} //OseeEnumTypeImpl
