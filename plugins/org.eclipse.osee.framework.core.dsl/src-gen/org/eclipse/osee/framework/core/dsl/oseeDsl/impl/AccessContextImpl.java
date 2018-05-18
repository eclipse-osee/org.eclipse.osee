/**
 */
package org.eclipse.osee.framework.core.dsl.oseeDsl.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.osee.framework.core.dsl.oseeDsl.AccessContext;
import org.eclipse.osee.framework.core.dsl.oseeDsl.HierarchyRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ObjectRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Access Context</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.AccessContextImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.AccessContextImpl#getSuperAccessContexts <em>Super Access Contexts</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.AccessContextImpl#getId <em>Id</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.AccessContextImpl#getAccessRules <em>Access Rules</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.AccessContextImpl#getHierarchyRestrictions <em>Hierarchy Restrictions</em>}</li>
 * </ul>
 *
 * @generated
 */
public class AccessContextImpl extends MinimalEObjectImpl.Container implements AccessContext
{
  /**
   * The default value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected static final String NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected String name = NAME_EDEFAULT;

  /**
   * The cached value of the '{@link #getSuperAccessContexts() <em>Super Access Contexts</em>}' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSuperAccessContexts()
   * @generated
   * @ordered
   */
  protected EList<AccessContext> superAccessContexts;

  /**
   * The default value of the '{@link #getId() <em>Id</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getId()
   * @generated
   * @ordered
   */
  protected static final String ID_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getId() <em>Id</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getId()
   * @generated
   * @ordered
   */
  protected String id = ID_EDEFAULT;

  /**
   * The cached value of the '{@link #getAccessRules() <em>Access Rules</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAccessRules()
   * @generated
   * @ordered
   */
  protected EList<ObjectRestriction> accessRules;

  /**
   * The cached value of the '{@link #getHierarchyRestrictions() <em>Hierarchy Restrictions</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getHierarchyRestrictions()
   * @generated
   * @ordered
   */
  protected EList<HierarchyRestriction> hierarchyRestrictions;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected AccessContextImpl()
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
    return OseeDslPackage.Literals.ACCESS_CONTEXT;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getName()
  {
    return name;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setName(String newName)
  {
    String oldName = name;
    name = newName;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, OseeDslPackage.ACCESS_CONTEXT__NAME, oldName, name));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<AccessContext> getSuperAccessContexts()
  {
    if (superAccessContexts == null)
    {
      superAccessContexts = new EObjectResolvingEList<AccessContext>(AccessContext.class, this, OseeDslPackage.ACCESS_CONTEXT__SUPER_ACCESS_CONTEXTS);
    }
    return superAccessContexts;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getId()
  {
    return id;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setId(String newId)
  {
    String oldId = id;
    id = newId;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, OseeDslPackage.ACCESS_CONTEXT__ID, oldId, id));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<ObjectRestriction> getAccessRules()
  {
    if (accessRules == null)
    {
      accessRules = new EObjectContainmentEList<ObjectRestriction>(ObjectRestriction.class, this, OseeDslPackage.ACCESS_CONTEXT__ACCESS_RULES);
    }
    return accessRules;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<HierarchyRestriction> getHierarchyRestrictions()
  {
    if (hierarchyRestrictions == null)
    {
      hierarchyRestrictions = new EObjectContainmentEList<HierarchyRestriction>(HierarchyRestriction.class, this, OseeDslPackage.ACCESS_CONTEXT__HIERARCHY_RESTRICTIONS);
    }
    return hierarchyRestrictions;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
      case OseeDslPackage.ACCESS_CONTEXT__ACCESS_RULES:
        return ((InternalEList<?>)getAccessRules()).basicRemove(otherEnd, msgs);
      case OseeDslPackage.ACCESS_CONTEXT__HIERARCHY_RESTRICTIONS:
        return ((InternalEList<?>)getHierarchyRestrictions()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
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
      case OseeDslPackage.ACCESS_CONTEXT__NAME:
        return getName();
      case OseeDslPackage.ACCESS_CONTEXT__SUPER_ACCESS_CONTEXTS:
        return getSuperAccessContexts();
      case OseeDslPackage.ACCESS_CONTEXT__ID:
        return getId();
      case OseeDslPackage.ACCESS_CONTEXT__ACCESS_RULES:
        return getAccessRules();
      case OseeDslPackage.ACCESS_CONTEXT__HIERARCHY_RESTRICTIONS:
        return getHierarchyRestrictions();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case OseeDslPackage.ACCESS_CONTEXT__NAME:
        setName((String)newValue);
        return;
      case OseeDslPackage.ACCESS_CONTEXT__SUPER_ACCESS_CONTEXTS:
        getSuperAccessContexts().clear();
        getSuperAccessContexts().addAll((Collection<? extends AccessContext>)newValue);
        return;
      case OseeDslPackage.ACCESS_CONTEXT__ID:
        setId((String)newValue);
        return;
      case OseeDslPackage.ACCESS_CONTEXT__ACCESS_RULES:
        getAccessRules().clear();
        getAccessRules().addAll((Collection<? extends ObjectRestriction>)newValue);
        return;
      case OseeDslPackage.ACCESS_CONTEXT__HIERARCHY_RESTRICTIONS:
        getHierarchyRestrictions().clear();
        getHierarchyRestrictions().addAll((Collection<? extends HierarchyRestriction>)newValue);
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
      case OseeDslPackage.ACCESS_CONTEXT__NAME:
        setName(NAME_EDEFAULT);
        return;
      case OseeDslPackage.ACCESS_CONTEXT__SUPER_ACCESS_CONTEXTS:
        getSuperAccessContexts().clear();
        return;
      case OseeDslPackage.ACCESS_CONTEXT__ID:
        setId(ID_EDEFAULT);
        return;
      case OseeDslPackage.ACCESS_CONTEXT__ACCESS_RULES:
        getAccessRules().clear();
        return;
      case OseeDslPackage.ACCESS_CONTEXT__HIERARCHY_RESTRICTIONS:
        getHierarchyRestrictions().clear();
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
      case OseeDslPackage.ACCESS_CONTEXT__NAME:
        return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
      case OseeDslPackage.ACCESS_CONTEXT__SUPER_ACCESS_CONTEXTS:
        return superAccessContexts != null && !superAccessContexts.isEmpty();
      case OseeDslPackage.ACCESS_CONTEXT__ID:
        return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
      case OseeDslPackage.ACCESS_CONTEXT__ACCESS_RULES:
        return accessRules != null && !accessRules.isEmpty();
      case OseeDslPackage.ACCESS_CONTEXT__HIERARCHY_RESTRICTIONS:
        return hierarchyRestrictions != null && !hierarchyRestrictions.isEmpty();
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
    result.append(" (name: ");
    result.append(name);
    result.append(", id: ");
    result.append(id);
    result.append(')');
    return result.toString();
  }

} //AccessContextImpl
