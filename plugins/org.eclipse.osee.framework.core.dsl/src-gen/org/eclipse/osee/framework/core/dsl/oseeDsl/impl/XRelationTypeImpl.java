/**
 */
package org.eclipse.osee.framework.core.dsl.oseeDsl.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage;
import org.eclipse.osee.framework.core.dsl.oseeDsl.RelationMultiplicityEnum;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>XRelation Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XRelationTypeImpl#getSideAName <em>Side AName</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XRelationTypeImpl#getSideAArtifactType <em>Side AArtifact Type</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XRelationTypeImpl#getSideBName <em>Side BName</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XRelationTypeImpl#getSideBArtifactType <em>Side BArtifact Type</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XRelationTypeImpl#getDefaultOrderType <em>Default Order Type</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.XRelationTypeImpl#getMultiplicity <em>Multiplicity</em>}</li>
 * </ul>
 *
 * @generated
 */
public class XRelationTypeImpl extends OseeTypeImpl implements XRelationType
{
  /**
   * The default value of the '{@link #getSideAName() <em>Side AName</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSideAName()
   * @generated
   * @ordered
   */
  protected static final String SIDE_ANAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getSideAName() <em>Side AName</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSideAName()
   * @generated
   * @ordered
   */
  protected String sideAName = SIDE_ANAME_EDEFAULT;

  /**
   * The cached value of the '{@link #getSideAArtifactType() <em>Side AArtifact Type</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSideAArtifactType()
   * @generated
   * @ordered
   */
  protected XArtifactType sideAArtifactType;

  /**
   * The default value of the '{@link #getSideBName() <em>Side BName</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSideBName()
   * @generated
   * @ordered
   */
  protected static final String SIDE_BNAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getSideBName() <em>Side BName</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSideBName()
   * @generated
   * @ordered
   */
  protected String sideBName = SIDE_BNAME_EDEFAULT;

  /**
   * The cached value of the '{@link #getSideBArtifactType() <em>Side BArtifact Type</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSideBArtifactType()
   * @generated
   * @ordered
   */
  protected XArtifactType sideBArtifactType;

  /**
   * The default value of the '{@link #getDefaultOrderType() <em>Default Order Type</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getDefaultOrderType()
   * @generated
   * @ordered
   */
  protected static final String DEFAULT_ORDER_TYPE_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getDefaultOrderType() <em>Default Order Type</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getDefaultOrderType()
   * @generated
   * @ordered
   */
  protected String defaultOrderType = DEFAULT_ORDER_TYPE_EDEFAULT;

  /**
   * The default value of the '{@link #getMultiplicity() <em>Multiplicity</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getMultiplicity()
   * @generated
   * @ordered
   */
  protected static final RelationMultiplicityEnum MULTIPLICITY_EDEFAULT = RelationMultiplicityEnum.ONE_TO_ONE;

  /**
   * The cached value of the '{@link #getMultiplicity() <em>Multiplicity</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getMultiplicity()
   * @generated
   * @ordered
   */
  protected RelationMultiplicityEnum multiplicity = MULTIPLICITY_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected XRelationTypeImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return OseeDslPackage.Literals.XRELATION_TYPE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getSideAName()
  {
    return sideAName;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setSideAName(String newSideAName)
  {
    String oldSideAName = sideAName;
    sideAName = newSideAName;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, OseeDslPackage.XRELATION_TYPE__SIDE_ANAME, oldSideAName, sideAName));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public XArtifactType getSideAArtifactType()
  {
    if (sideAArtifactType != null && sideAArtifactType.eIsProxy())
    {
      InternalEObject oldSideAArtifactType = (InternalEObject)sideAArtifactType;
      sideAArtifactType = (XArtifactType)eResolveProxy(oldSideAArtifactType);
      if (sideAArtifactType != oldSideAArtifactType)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, OseeDslPackage.XRELATION_TYPE__SIDE_AARTIFACT_TYPE, oldSideAArtifactType, sideAArtifactType));
      }
    }
    return sideAArtifactType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public XArtifactType basicGetSideAArtifactType()
  {
    return sideAArtifactType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setSideAArtifactType(XArtifactType newSideAArtifactType)
  {
    XArtifactType oldSideAArtifactType = sideAArtifactType;
    sideAArtifactType = newSideAArtifactType;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, OseeDslPackage.XRELATION_TYPE__SIDE_AARTIFACT_TYPE, oldSideAArtifactType, sideAArtifactType));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getSideBName()
  {
    return sideBName;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setSideBName(String newSideBName)
  {
    String oldSideBName = sideBName;
    sideBName = newSideBName;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, OseeDslPackage.XRELATION_TYPE__SIDE_BNAME, oldSideBName, sideBName));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public XArtifactType getSideBArtifactType()
  {
    if (sideBArtifactType != null && sideBArtifactType.eIsProxy())
    {
      InternalEObject oldSideBArtifactType = (InternalEObject)sideBArtifactType;
      sideBArtifactType = (XArtifactType)eResolveProxy(oldSideBArtifactType);
      if (sideBArtifactType != oldSideBArtifactType)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, OseeDslPackage.XRELATION_TYPE__SIDE_BARTIFACT_TYPE, oldSideBArtifactType, sideBArtifactType));
      }
    }
    return sideBArtifactType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public XArtifactType basicGetSideBArtifactType()
  {
    return sideBArtifactType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setSideBArtifactType(XArtifactType newSideBArtifactType)
  {
    XArtifactType oldSideBArtifactType = sideBArtifactType;
    sideBArtifactType = newSideBArtifactType;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, OseeDslPackage.XRELATION_TYPE__SIDE_BARTIFACT_TYPE, oldSideBArtifactType, sideBArtifactType));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getDefaultOrderType()
  {
    return defaultOrderType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setDefaultOrderType(String newDefaultOrderType)
  {
    String oldDefaultOrderType = defaultOrderType;
    defaultOrderType = newDefaultOrderType;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, OseeDslPackage.XRELATION_TYPE__DEFAULT_ORDER_TYPE, oldDefaultOrderType, defaultOrderType));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public RelationMultiplicityEnum getMultiplicity()
  {
    return multiplicity;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setMultiplicity(RelationMultiplicityEnum newMultiplicity)
  {
    RelationMultiplicityEnum oldMultiplicity = multiplicity;
    multiplicity = newMultiplicity == null ? MULTIPLICITY_EDEFAULT : newMultiplicity;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, OseeDslPackage.XRELATION_TYPE__MULTIPLICITY, oldMultiplicity, multiplicity));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
      case OseeDslPackage.XRELATION_TYPE__SIDE_ANAME:
        return getSideAName();
      case OseeDslPackage.XRELATION_TYPE__SIDE_AARTIFACT_TYPE:
        if (resolve) return getSideAArtifactType();
        return basicGetSideAArtifactType();
      case OseeDslPackage.XRELATION_TYPE__SIDE_BNAME:
        return getSideBName();
      case OseeDslPackage.XRELATION_TYPE__SIDE_BARTIFACT_TYPE:
        if (resolve) return getSideBArtifactType();
        return basicGetSideBArtifactType();
      case OseeDslPackage.XRELATION_TYPE__DEFAULT_ORDER_TYPE:
        return getDefaultOrderType();
      case OseeDslPackage.XRELATION_TYPE__MULTIPLICITY:
        return getMultiplicity();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case OseeDslPackage.XRELATION_TYPE__SIDE_ANAME:
        setSideAName((String)newValue);
        return;
      case OseeDslPackage.XRELATION_TYPE__SIDE_AARTIFACT_TYPE:
        setSideAArtifactType((XArtifactType)newValue);
        return;
      case OseeDslPackage.XRELATION_TYPE__SIDE_BNAME:
        setSideBName((String)newValue);
        return;
      case OseeDslPackage.XRELATION_TYPE__SIDE_BARTIFACT_TYPE:
        setSideBArtifactType((XArtifactType)newValue);
        return;
      case OseeDslPackage.XRELATION_TYPE__DEFAULT_ORDER_TYPE:
        setDefaultOrderType((String)newValue);
        return;
      case OseeDslPackage.XRELATION_TYPE__MULTIPLICITY:
        setMultiplicity((RelationMultiplicityEnum)newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
      case OseeDslPackage.XRELATION_TYPE__SIDE_ANAME:
        setSideAName(SIDE_ANAME_EDEFAULT);
        return;
      case OseeDslPackage.XRELATION_TYPE__SIDE_AARTIFACT_TYPE:
        setSideAArtifactType((XArtifactType)null);
        return;
      case OseeDslPackage.XRELATION_TYPE__SIDE_BNAME:
        setSideBName(SIDE_BNAME_EDEFAULT);
        return;
      case OseeDslPackage.XRELATION_TYPE__SIDE_BARTIFACT_TYPE:
        setSideBArtifactType((XArtifactType)null);
        return;
      case OseeDslPackage.XRELATION_TYPE__DEFAULT_ORDER_TYPE:
        setDefaultOrderType(DEFAULT_ORDER_TYPE_EDEFAULT);
        return;
      case OseeDslPackage.XRELATION_TYPE__MULTIPLICITY:
        setMultiplicity(MULTIPLICITY_EDEFAULT);
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
      case OseeDslPackage.XRELATION_TYPE__SIDE_ANAME:
        return SIDE_ANAME_EDEFAULT == null ? sideAName != null : !SIDE_ANAME_EDEFAULT.equals(sideAName);
      case OseeDslPackage.XRELATION_TYPE__SIDE_AARTIFACT_TYPE:
        return sideAArtifactType != null;
      case OseeDslPackage.XRELATION_TYPE__SIDE_BNAME:
        return SIDE_BNAME_EDEFAULT == null ? sideBName != null : !SIDE_BNAME_EDEFAULT.equals(sideBName);
      case OseeDslPackage.XRELATION_TYPE__SIDE_BARTIFACT_TYPE:
        return sideBArtifactType != null;
      case OseeDslPackage.XRELATION_TYPE__DEFAULT_ORDER_TYPE:
        return DEFAULT_ORDER_TYPE_EDEFAULT == null ? defaultOrderType != null : !DEFAULT_ORDER_TYPE_EDEFAULT.equals(defaultOrderType);
      case OseeDslPackage.XRELATION_TYPE__MULTIPLICITY:
        return multiplicity != MULTIPLICITY_EDEFAULT;
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy()) return super.toString();

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

} //XRelationTypeImpl
