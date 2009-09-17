/**
 * <copyright>
 * </copyright>
 *

 */
package org.eclipse.osee.framework.oseeTypes.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.osee.framework.oseeTypes.ArtifactType;
import org.eclipse.osee.framework.oseeTypes.OseeTypesPackage;
import org.eclipse.osee.framework.oseeTypes.RelationMultiplicityEnum;
import org.eclipse.osee.framework.oseeTypes.RelationType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Relation Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.osee.framework.oseeTypes.impl.RelationTypeImpl#getSideAName <em>Side AName</em>}</li>
 * <li>{@link org.eclipse.osee.framework.oseeTypes.impl.RelationTypeImpl#getSideAArtifactType <em>Side AArtifact Type
 * </em>}</li>
 * <li>{@link org.eclipse.osee.framework.oseeTypes.impl.RelationTypeImpl#getSideBName <em>Side BName</em>}</li>
 * <li>{@link org.eclipse.osee.framework.oseeTypes.impl.RelationTypeImpl#getSideBArtifactType <em>Side BArtifact Type
 * </em>}</li>
 * <li>{@link org.eclipse.osee.framework.oseeTypes.impl.RelationTypeImpl#getDefaultOrderType <em>Default Order Type
 * </em>}</li>
 * <li>{@link org.eclipse.osee.framework.oseeTypes.impl.RelationTypeImpl#getMultiplicity <em>Multiplicity</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class RelationTypeImpl extends OseeTypeImpl implements RelationType {
   /**
    * The default value of the '{@link #getSideAName() <em>Side AName</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @see #getSideAName()
    * @generated
    * @ordered
    */
   protected static final String SIDE_ANAME_EDEFAULT = null;

   /**
    * The cached value of the '{@link #getSideAName() <em>Side AName</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @see #getSideAName()
    * @generated
    * @ordered
    */
   protected String sideAName = SIDE_ANAME_EDEFAULT;

   /**
    * The cached value of the '{@link #getSideAArtifactType() <em>Side AArtifact Type</em>}' reference.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @see #getSideAArtifactType()
    * @generated
    * @ordered
    */
   protected ArtifactType sideAArtifactType;

   /**
    * The default value of the '{@link #getSideBName() <em>Side BName</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @see #getSideBName()
    * @generated
    * @ordered
    */
   protected static final String SIDE_BNAME_EDEFAULT = null;

   /**
    * The cached value of the '{@link #getSideBName() <em>Side BName</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @see #getSideBName()
    * @generated
    * @ordered
    */
   protected String sideBName = SIDE_BNAME_EDEFAULT;

   /**
    * The cached value of the '{@link #getSideBArtifactType() <em>Side BArtifact Type</em>}' reference.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @see #getSideBArtifactType()
    * @generated
    * @ordered
    */
   protected ArtifactType sideBArtifactType;

   /**
    * The default value of the '{@link #getDefaultOrderType() <em>Default Order Type</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @see #getDefaultOrderType()
    * @generated
    * @ordered
    */
   protected static final String DEFAULT_ORDER_TYPE_EDEFAULT = null;

   /**
    * The cached value of the '{@link #getDefaultOrderType() <em>Default Order Type</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @see #getDefaultOrderType()
    * @generated
    * @ordered
    */
   protected String defaultOrderType = DEFAULT_ORDER_TYPE_EDEFAULT;

   /**
    * The default value of the '{@link #getMultiplicity() <em>Multiplicity</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @see #getMultiplicity()
    * @generated
    * @ordered
    */
   protected static final RelationMultiplicityEnum MULTIPLICITY_EDEFAULT = RelationMultiplicityEnum.ONE_TO_ONE;

   /**
    * The cached value of the '{@link #getMultiplicity() <em>Multiplicity</em>}' attribute.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @see #getMultiplicity()
    * @generated
    * @ordered
    */
   protected RelationMultiplicityEnum multiplicity = MULTIPLICITY_EDEFAULT;

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @generated
    */
   protected RelationTypeImpl() {
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
      return OseeTypesPackage.Literals.RELATION_TYPE;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @generated
    */
   public String getSideAName() {
      return sideAName;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @generated
    */
   public void setSideAName(String newSideAName) {
      String oldSideAName = sideAName;
      sideAName = newSideAName;
      if (eNotificationRequired()) {
         eNotify(new ENotificationImpl(this, Notification.SET, OseeTypesPackage.RELATION_TYPE__SIDE_ANAME,
               oldSideAName, sideAName));
      }
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @generated
    */
   public ArtifactType getSideAArtifactType() {
      if (sideAArtifactType != null && sideAArtifactType.eIsProxy()) {
         InternalEObject oldSideAArtifactType = (InternalEObject) sideAArtifactType;
         sideAArtifactType = (ArtifactType) eResolveProxy(oldSideAArtifactType);
         if (sideAArtifactType != oldSideAArtifactType) {
            if (eNotificationRequired()) {
               eNotify(new ENotificationImpl(this, Notification.RESOLVE,
                     OseeTypesPackage.RELATION_TYPE__SIDE_AARTIFACT_TYPE, oldSideAArtifactType, sideAArtifactType));
            }
         }
      }
      return sideAArtifactType;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @generated
    */
   public ArtifactType basicGetSideAArtifactType() {
      return sideAArtifactType;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @generated
    */
   public void setSideAArtifactType(ArtifactType newSideAArtifactType) {
      ArtifactType oldSideAArtifactType = sideAArtifactType;
      sideAArtifactType = newSideAArtifactType;
      if (eNotificationRequired()) {
         eNotify(new ENotificationImpl(this, Notification.SET, OseeTypesPackage.RELATION_TYPE__SIDE_AARTIFACT_TYPE,
               oldSideAArtifactType, sideAArtifactType));
      }
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @generated
    */
   public String getSideBName() {
      return sideBName;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @generated
    */
   public void setSideBName(String newSideBName) {
      String oldSideBName = sideBName;
      sideBName = newSideBName;
      if (eNotificationRequired()) {
         eNotify(new ENotificationImpl(this, Notification.SET, OseeTypesPackage.RELATION_TYPE__SIDE_BNAME,
               oldSideBName, sideBName));
      }
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @generated
    */
   public ArtifactType getSideBArtifactType() {
      if (sideBArtifactType != null && sideBArtifactType.eIsProxy()) {
         InternalEObject oldSideBArtifactType = (InternalEObject) sideBArtifactType;
         sideBArtifactType = (ArtifactType) eResolveProxy(oldSideBArtifactType);
         if (sideBArtifactType != oldSideBArtifactType) {
            if (eNotificationRequired()) {
               eNotify(new ENotificationImpl(this, Notification.RESOLVE,
                     OseeTypesPackage.RELATION_TYPE__SIDE_BARTIFACT_TYPE, oldSideBArtifactType, sideBArtifactType));
            }
         }
      }
      return sideBArtifactType;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @generated
    */
   public ArtifactType basicGetSideBArtifactType() {
      return sideBArtifactType;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @generated
    */
   public void setSideBArtifactType(ArtifactType newSideBArtifactType) {
      ArtifactType oldSideBArtifactType = sideBArtifactType;
      sideBArtifactType = newSideBArtifactType;
      if (eNotificationRequired()) {
         eNotify(new ENotificationImpl(this, Notification.SET, OseeTypesPackage.RELATION_TYPE__SIDE_BARTIFACT_TYPE,
               oldSideBArtifactType, sideBArtifactType));
      }
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @generated
    */
   public String getDefaultOrderType() {
      return defaultOrderType;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @generated
    */
   public void setDefaultOrderType(String newDefaultOrderType) {
      String oldDefaultOrderType = defaultOrderType;
      defaultOrderType = newDefaultOrderType;
      if (eNotificationRequired()) {
         eNotify(new ENotificationImpl(this, Notification.SET, OseeTypesPackage.RELATION_TYPE__DEFAULT_ORDER_TYPE,
               oldDefaultOrderType, defaultOrderType));
      }
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @generated
    */
   public RelationMultiplicityEnum getMultiplicity() {
      return multiplicity;
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @generated
    */
   public void setMultiplicity(RelationMultiplicityEnum newMultiplicity) {
      RelationMultiplicityEnum oldMultiplicity = multiplicity;
      multiplicity = newMultiplicity == null ? MULTIPLICITY_EDEFAULT : newMultiplicity;
      if (eNotificationRequired()) {
         eNotify(new ENotificationImpl(this, Notification.SET, OseeTypesPackage.RELATION_TYPE__MULTIPLICITY,
               oldMultiplicity, multiplicity));
      }
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
         case OseeTypesPackage.RELATION_TYPE__SIDE_ANAME:
            return getSideAName();
         case OseeTypesPackage.RELATION_TYPE__SIDE_AARTIFACT_TYPE:
            if (resolve) {
               return getSideAArtifactType();
            }
            return basicGetSideAArtifactType();
         case OseeTypesPackage.RELATION_TYPE__SIDE_BNAME:
            return getSideBName();
         case OseeTypesPackage.RELATION_TYPE__SIDE_BARTIFACT_TYPE:
            if (resolve) {
               return getSideBArtifactType();
            }
            return basicGetSideBArtifactType();
         case OseeTypesPackage.RELATION_TYPE__DEFAULT_ORDER_TYPE:
            return getDefaultOrderType();
         case OseeTypesPackage.RELATION_TYPE__MULTIPLICITY:
            return getMultiplicity();
      }
      return super.eGet(featureID, resolve, coreType);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public void eSet(int featureID, Object newValue) {
      switch (featureID) {
         case OseeTypesPackage.RELATION_TYPE__SIDE_ANAME:
            setSideAName((String) newValue);
            return;
         case OseeTypesPackage.RELATION_TYPE__SIDE_AARTIFACT_TYPE:
            setSideAArtifactType((ArtifactType) newValue);
            return;
         case OseeTypesPackage.RELATION_TYPE__SIDE_BNAME:
            setSideBName((String) newValue);
            return;
         case OseeTypesPackage.RELATION_TYPE__SIDE_BARTIFACT_TYPE:
            setSideBArtifactType((ArtifactType) newValue);
            return;
         case OseeTypesPackage.RELATION_TYPE__DEFAULT_ORDER_TYPE:
            setDefaultOrderType((String) newValue);
            return;
         case OseeTypesPackage.RELATION_TYPE__MULTIPLICITY:
            setMultiplicity((RelationMultiplicityEnum) newValue);
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
         case OseeTypesPackage.RELATION_TYPE__SIDE_ANAME:
            setSideAName(SIDE_ANAME_EDEFAULT);
            return;
         case OseeTypesPackage.RELATION_TYPE__SIDE_AARTIFACT_TYPE:
            setSideAArtifactType((ArtifactType) null);
            return;
         case OseeTypesPackage.RELATION_TYPE__SIDE_BNAME:
            setSideBName(SIDE_BNAME_EDEFAULT);
            return;
         case OseeTypesPackage.RELATION_TYPE__SIDE_BARTIFACT_TYPE:
            setSideBArtifactType((ArtifactType) null);
            return;
         case OseeTypesPackage.RELATION_TYPE__DEFAULT_ORDER_TYPE:
            setDefaultOrderType(DEFAULT_ORDER_TYPE_EDEFAULT);
            return;
         case OseeTypesPackage.RELATION_TYPE__MULTIPLICITY:
            setMultiplicity(MULTIPLICITY_EDEFAULT);
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
         case OseeTypesPackage.RELATION_TYPE__SIDE_ANAME:
            return SIDE_ANAME_EDEFAULT == null ? sideAName != null : !SIDE_ANAME_EDEFAULT.equals(sideAName);
         case OseeTypesPackage.RELATION_TYPE__SIDE_AARTIFACT_TYPE:
            return sideAArtifactType != null;
         case OseeTypesPackage.RELATION_TYPE__SIDE_BNAME:
            return SIDE_BNAME_EDEFAULT == null ? sideBName != null : !SIDE_BNAME_EDEFAULT.equals(sideBName);
         case OseeTypesPackage.RELATION_TYPE__SIDE_BARTIFACT_TYPE:
            return sideBArtifactType != null;
         case OseeTypesPackage.RELATION_TYPE__DEFAULT_ORDER_TYPE:
            return DEFAULT_ORDER_TYPE_EDEFAULT == null ? defaultOrderType != null : !DEFAULT_ORDER_TYPE_EDEFAULT.equals(defaultOrderType);
         case OseeTypesPackage.RELATION_TYPE__MULTIPLICITY:
            return multiplicity != MULTIPLICITY_EDEFAULT;
      }
      return super.eIsSet(featureID);
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public String toString() {
      if (eIsProxy()) {
         return super.toString();
      }

      StringBuffer result = new StringBuffer(super.toString());
      result.append(" (sideAName: ");
      result.append(sideAName);
      result.append(", sideBName: ");
      result.append(sideBName);
      result.append(", defaultOrderType: ");
      result.append(defaultOrderType);
      result.append(", multiplicity: ");
      result.append(multiplicity);
      result.append(')');
      return result.toString();
   }

   /**
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @not generated
    */
   @Override
   public boolean equals(Object obj) {
      if (obj instanceof RelationType) {
         return super.equals(obj);
      }
      return false;
   }
} //RelationTypeImpl
