/**
 */
package org.eclipse.osee.ats.dsl.atsDsl.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EDataTypeEList;

import org.eclipse.osee.ats.dsl.atsDsl.AtsDslPackage;
import org.eclipse.osee.ats.dsl.atsDsl.BooleanDef;
import org.eclipse.osee.ats.dsl.atsDsl.VersionDef;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Version Def</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.VersionDefImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.VersionDefImpl#getUuid <em>Uuid</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.VersionDefImpl#getStaticId <em>Static Id</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.VersionDefImpl#getNext <em>Next</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.VersionDefImpl#getReleased <em>Released</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.VersionDefImpl#getAllowCreateBranch <em>Allow Create Branch</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.VersionDefImpl#getAllowCommitBranch <em>Allow Commit Branch</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.VersionDefImpl#getBaselineBranchUuid <em>Baseline Branch Uuid</em>}</li>
 *   <li>{@link org.eclipse.osee.ats.dsl.atsDsl.impl.VersionDefImpl#getParallelVersion <em>Parallel Version</em>}</li>
 * </ul>
 *
 * @generated
 */
public class VersionDefImpl extends MinimalEObjectImpl.Container implements VersionDef
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
   * The default value of the '{@link #getUuid() <em>Uuid</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getUuid()
   * @generated
   * @ordered
   */
  protected static final int UUID_EDEFAULT = 0;

  /**
   * The cached value of the '{@link #getUuid() <em>Uuid</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getUuid()
   * @generated
   * @ordered
   */
  protected int uuid = UUID_EDEFAULT;

  /**
   * The cached value of the '{@link #getStaticId() <em>Static Id</em>}' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getStaticId()
   * @generated
   * @ordered
   */
  protected EList<String> staticId;

  /**
   * The default value of the '{@link #getNext() <em>Next</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getNext()
   * @generated
   * @ordered
   */
  protected static final BooleanDef NEXT_EDEFAULT = BooleanDef.NONE;

  /**
   * The cached value of the '{@link #getNext() <em>Next</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getNext()
   * @generated
   * @ordered
   */
  protected BooleanDef next = NEXT_EDEFAULT;

  /**
   * The default value of the '{@link #getReleased() <em>Released</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getReleased()
   * @generated
   * @ordered
   */
  protected static final BooleanDef RELEASED_EDEFAULT = BooleanDef.NONE;

  /**
   * The cached value of the '{@link #getReleased() <em>Released</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getReleased()
   * @generated
   * @ordered
   */
  protected BooleanDef released = RELEASED_EDEFAULT;

  /**
   * The default value of the '{@link #getAllowCreateBranch() <em>Allow Create Branch</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAllowCreateBranch()
   * @generated
   * @ordered
   */
  protected static final BooleanDef ALLOW_CREATE_BRANCH_EDEFAULT = BooleanDef.NONE;

  /**
   * The cached value of the '{@link #getAllowCreateBranch() <em>Allow Create Branch</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAllowCreateBranch()
   * @generated
   * @ordered
   */
  protected BooleanDef allowCreateBranch = ALLOW_CREATE_BRANCH_EDEFAULT;

  /**
   * The default value of the '{@link #getAllowCommitBranch() <em>Allow Commit Branch</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAllowCommitBranch()
   * @generated
   * @ordered
   */
  protected static final BooleanDef ALLOW_COMMIT_BRANCH_EDEFAULT = BooleanDef.NONE;

  /**
   * The cached value of the '{@link #getAllowCommitBranch() <em>Allow Commit Branch</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAllowCommitBranch()
   * @generated
   * @ordered
   */
  protected BooleanDef allowCommitBranch = ALLOW_COMMIT_BRANCH_EDEFAULT;

  /**
   * The default value of the '{@link #getBaselineBranchUuid() <em>Baseline Branch Uuid</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getBaselineBranchUuid()
   * @generated
   * @ordered
   */
  protected static final String BASELINE_BRANCH_UUID_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getBaselineBranchUuid() <em>Baseline Branch Uuid</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getBaselineBranchUuid()
   * @generated
   * @ordered
   */
  protected String baselineBranchUuid = BASELINE_BRANCH_UUID_EDEFAULT;

  /**
   * The cached value of the '{@link #getParallelVersion() <em>Parallel Version</em>}' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getParallelVersion()
   * @generated
   * @ordered
   */
  protected EList<String> parallelVersion;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected VersionDefImpl()
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
    return AtsDslPackage.Literals.VERSION_DEF;
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
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.VERSION_DEF__NAME, oldName, name));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public int getUuid()
  {
    return uuid;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setUuid(int newUuid)
  {
    int oldUuid = uuid;
    uuid = newUuid;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.VERSION_DEF__UUID, oldUuid, uuid));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<String> getStaticId()
  {
    if (staticId == null)
    {
      staticId = new EDataTypeEList<String>(String.class, this, AtsDslPackage.VERSION_DEF__STATIC_ID);
    }
    return staticId;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public BooleanDef getNext()
  {
    return next;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setNext(BooleanDef newNext)
  {
    BooleanDef oldNext = next;
    next = newNext == null ? NEXT_EDEFAULT : newNext;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.VERSION_DEF__NEXT, oldNext, next));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public BooleanDef getReleased()
  {
    return released;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setReleased(BooleanDef newReleased)
  {
    BooleanDef oldReleased = released;
    released = newReleased == null ? RELEASED_EDEFAULT : newReleased;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.VERSION_DEF__RELEASED, oldReleased, released));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public BooleanDef getAllowCreateBranch()
  {
    return allowCreateBranch;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setAllowCreateBranch(BooleanDef newAllowCreateBranch)
  {
    BooleanDef oldAllowCreateBranch = allowCreateBranch;
    allowCreateBranch = newAllowCreateBranch == null ? ALLOW_CREATE_BRANCH_EDEFAULT : newAllowCreateBranch;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.VERSION_DEF__ALLOW_CREATE_BRANCH, oldAllowCreateBranch, allowCreateBranch));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public BooleanDef getAllowCommitBranch()
  {
    return allowCommitBranch;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setAllowCommitBranch(BooleanDef newAllowCommitBranch)
  {
    BooleanDef oldAllowCommitBranch = allowCommitBranch;
    allowCommitBranch = newAllowCommitBranch == null ? ALLOW_COMMIT_BRANCH_EDEFAULT : newAllowCommitBranch;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.VERSION_DEF__ALLOW_COMMIT_BRANCH, oldAllowCommitBranch, allowCommitBranch));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getBaselineBranchUuid()
  {
    return baselineBranchUuid;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setBaselineBranchUuid(String newBaselineBranchUuid)
  {
    String oldBaselineBranchUuid = baselineBranchUuid;
    baselineBranchUuid = newBaselineBranchUuid;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, AtsDslPackage.VERSION_DEF__BASELINE_BRANCH_UUID, oldBaselineBranchUuid, baselineBranchUuid));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<String> getParallelVersion()
  {
    if (parallelVersion == null)
    {
      parallelVersion = new EDataTypeEList<String>(String.class, this, AtsDslPackage.VERSION_DEF__PARALLEL_VERSION);
    }
    return parallelVersion;
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
      case AtsDslPackage.VERSION_DEF__NAME:
        return getName();
      case AtsDslPackage.VERSION_DEF__UUID:
        return getUuid();
      case AtsDslPackage.VERSION_DEF__STATIC_ID:
        return getStaticId();
      case AtsDslPackage.VERSION_DEF__NEXT:
        return getNext();
      case AtsDslPackage.VERSION_DEF__RELEASED:
        return getReleased();
      case AtsDslPackage.VERSION_DEF__ALLOW_CREATE_BRANCH:
        return getAllowCreateBranch();
      case AtsDslPackage.VERSION_DEF__ALLOW_COMMIT_BRANCH:
        return getAllowCommitBranch();
      case AtsDslPackage.VERSION_DEF__BASELINE_BRANCH_UUID:
        return getBaselineBranchUuid();
      case AtsDslPackage.VERSION_DEF__PARALLEL_VERSION:
        return getParallelVersion();
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
      case AtsDslPackage.VERSION_DEF__NAME:
        setName((String)newValue);
        return;
      case AtsDslPackage.VERSION_DEF__UUID:
        setUuid((Integer)newValue);
        return;
      case AtsDslPackage.VERSION_DEF__STATIC_ID:
        getStaticId().clear();
        getStaticId().addAll((Collection<? extends String>)newValue);
        return;
      case AtsDslPackage.VERSION_DEF__NEXT:
        setNext((BooleanDef)newValue);
        return;
      case AtsDslPackage.VERSION_DEF__RELEASED:
        setReleased((BooleanDef)newValue);
        return;
      case AtsDslPackage.VERSION_DEF__ALLOW_CREATE_BRANCH:
        setAllowCreateBranch((BooleanDef)newValue);
        return;
      case AtsDslPackage.VERSION_DEF__ALLOW_COMMIT_BRANCH:
        setAllowCommitBranch((BooleanDef)newValue);
        return;
      case AtsDslPackage.VERSION_DEF__BASELINE_BRANCH_UUID:
        setBaselineBranchUuid((String)newValue);
        return;
      case AtsDslPackage.VERSION_DEF__PARALLEL_VERSION:
        getParallelVersion().clear();
        getParallelVersion().addAll((Collection<? extends String>)newValue);
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
      case AtsDslPackage.VERSION_DEF__NAME:
        setName(NAME_EDEFAULT);
        return;
      case AtsDslPackage.VERSION_DEF__UUID:
        setUuid(UUID_EDEFAULT);
        return;
      case AtsDslPackage.VERSION_DEF__STATIC_ID:
        getStaticId().clear();
        return;
      case AtsDslPackage.VERSION_DEF__NEXT:
        setNext(NEXT_EDEFAULT);
        return;
      case AtsDslPackage.VERSION_DEF__RELEASED:
        setReleased(RELEASED_EDEFAULT);
        return;
      case AtsDslPackage.VERSION_DEF__ALLOW_CREATE_BRANCH:
        setAllowCreateBranch(ALLOW_CREATE_BRANCH_EDEFAULT);
        return;
      case AtsDslPackage.VERSION_DEF__ALLOW_COMMIT_BRANCH:
        setAllowCommitBranch(ALLOW_COMMIT_BRANCH_EDEFAULT);
        return;
      case AtsDslPackage.VERSION_DEF__BASELINE_BRANCH_UUID:
        setBaselineBranchUuid(BASELINE_BRANCH_UUID_EDEFAULT);
        return;
      case AtsDslPackage.VERSION_DEF__PARALLEL_VERSION:
        getParallelVersion().clear();
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
      case AtsDslPackage.VERSION_DEF__NAME:
        return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
      case AtsDslPackage.VERSION_DEF__UUID:
        return uuid != UUID_EDEFAULT;
      case AtsDslPackage.VERSION_DEF__STATIC_ID:
        return staticId != null && !staticId.isEmpty();
      case AtsDslPackage.VERSION_DEF__NEXT:
        return next != NEXT_EDEFAULT;
      case AtsDslPackage.VERSION_DEF__RELEASED:
        return released != RELEASED_EDEFAULT;
      case AtsDslPackage.VERSION_DEF__ALLOW_CREATE_BRANCH:
        return allowCreateBranch != ALLOW_CREATE_BRANCH_EDEFAULT;
      case AtsDslPackage.VERSION_DEF__ALLOW_COMMIT_BRANCH:
        return allowCommitBranch != ALLOW_COMMIT_BRANCH_EDEFAULT;
      case AtsDslPackage.VERSION_DEF__BASELINE_BRANCH_UUID:
        return BASELINE_BRANCH_UUID_EDEFAULT == null ? baselineBranchUuid != null : !BASELINE_BRANCH_UUID_EDEFAULT.equals(baselineBranchUuid);
      case AtsDslPackage.VERSION_DEF__PARALLEL_VERSION:
        return parallelVersion != null && !parallelVersion.isEmpty();
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
    result.append(", uuid: ");
    result.append(uuid);
    result.append(", staticId: ");
    result.append(staticId);
    result.append(", next: ");
    result.append(next);
    result.append(", released: ");
    result.append(released);
    result.append(", allowCreateBranch: ");
    result.append(allowCreateBranch);
    result.append(", allowCommitBranch: ");
    result.append(allowCommitBranch);
    result.append(", baselineBranchUuid: ");
    result.append(baselineBranchUuid);
    result.append(", parallelVersion: ");
    result.append(parallelVersion);
    result.append(')');
    return result.toString();
  }

} //VersionDefImpl
