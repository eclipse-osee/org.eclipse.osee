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

import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ReferencedContext;
import org.eclipse.osee.framework.core.dsl.oseeDsl.Role;
import org.eclipse.osee.framework.core.dsl.oseeDsl.UsersAndGroups;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Role</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.RoleImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.RoleImpl#getSuperRoles <em>Super Roles</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.RoleImpl#getUsersAndGroups <em>Users And Groups</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.RoleImpl#getReferencedContexts <em>Referenced Contexts</em>}</li>
 * </ul>
 *
 * @generated
 */
public class RoleImpl extends MinimalEObjectImpl.Container implements Role
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
   * The cached value of the '{@link #getSuperRoles() <em>Super Roles</em>}' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSuperRoles()
   * @generated
   * @ordered
   */
  protected EList<Role> superRoles;

  /**
   * The cached value of the '{@link #getUsersAndGroups() <em>Users And Groups</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getUsersAndGroups()
   * @generated
   * @ordered
   */
  protected EList<UsersAndGroups> usersAndGroups;

  /**
   * The cached value of the '{@link #getReferencedContexts() <em>Referenced Contexts</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getReferencedContexts()
   * @generated
   * @ordered
   */
  protected EList<ReferencedContext> referencedContexts;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected RoleImpl()
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
    return OseeDslPackage.Literals.ROLE;
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
      eNotify(new ENotificationImpl(this, Notification.SET, OseeDslPackage.ROLE__NAME, oldName, name));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<Role> getSuperRoles()
  {
    if (superRoles == null)
    {
      superRoles = new EObjectResolvingEList<Role>(Role.class, this, OseeDslPackage.ROLE__SUPER_ROLES);
    }
    return superRoles;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<UsersAndGroups> getUsersAndGroups()
  {
    if (usersAndGroups == null)
    {
      usersAndGroups = new EObjectContainmentEList<UsersAndGroups>(UsersAndGroups.class, this, OseeDslPackage.ROLE__USERS_AND_GROUPS);
    }
    return usersAndGroups;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<ReferencedContext> getReferencedContexts()
  {
    if (referencedContexts == null)
    {
      referencedContexts = new EObjectContainmentEList<ReferencedContext>(ReferencedContext.class, this, OseeDslPackage.ROLE__REFERENCED_CONTEXTS);
    }
    return referencedContexts;
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
      case OseeDslPackage.ROLE__USERS_AND_GROUPS:
        return ((InternalEList<?>)getUsersAndGroups()).basicRemove(otherEnd, msgs);
      case OseeDslPackage.ROLE__REFERENCED_CONTEXTS:
        return ((InternalEList<?>)getReferencedContexts()).basicRemove(otherEnd, msgs);
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
      case OseeDslPackage.ROLE__NAME:
        return getName();
      case OseeDslPackage.ROLE__SUPER_ROLES:
        return getSuperRoles();
      case OseeDslPackage.ROLE__USERS_AND_GROUPS:
        return getUsersAndGroups();
      case OseeDslPackage.ROLE__REFERENCED_CONTEXTS:
        return getReferencedContexts();
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
      case OseeDslPackage.ROLE__NAME:
        setName((String)newValue);
        return;
      case OseeDslPackage.ROLE__SUPER_ROLES:
        getSuperRoles().clear();
        getSuperRoles().addAll((Collection<? extends Role>)newValue);
        return;
      case OseeDslPackage.ROLE__USERS_AND_GROUPS:
        getUsersAndGroups().clear();
        getUsersAndGroups().addAll((Collection<? extends UsersAndGroups>)newValue);
        return;
      case OseeDslPackage.ROLE__REFERENCED_CONTEXTS:
        getReferencedContexts().clear();
        getReferencedContexts().addAll((Collection<? extends ReferencedContext>)newValue);
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
      case OseeDslPackage.ROLE__NAME:
        setName(NAME_EDEFAULT);
        return;
      case OseeDslPackage.ROLE__SUPER_ROLES:
        getSuperRoles().clear();
        return;
      case OseeDslPackage.ROLE__USERS_AND_GROUPS:
        getUsersAndGroups().clear();
        return;
      case OseeDslPackage.ROLE__REFERENCED_CONTEXTS:
        getReferencedContexts().clear();
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
      case OseeDslPackage.ROLE__NAME:
        return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
      case OseeDslPackage.ROLE__SUPER_ROLES:
        return superRoles != null && !superRoles.isEmpty();
      case OseeDslPackage.ROLE__USERS_AND_GROUPS:
        return usersAndGroups != null && !usersAndGroups.isEmpty();
      case OseeDslPackage.ROLE__REFERENCED_CONTEXTS:
        return referencedContexts != null && !referencedContexts.isEmpty();
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
    result.append(')');
    return result.toString();
  }

} //RoleImpl
