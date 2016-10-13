/**
 */
package org.eclipse.osee.framework.core.dsl.oseeDsl.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.osee.framework.core.dsl.oseeDsl.AccessContext;
import org.eclipse.osee.framework.core.dsl.oseeDsl.Import;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDslPackage;
import org.eclipse.osee.framework.core.dsl.oseeDsl.Role;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactMatcher;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeArtifactTypeOverride;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumOverride;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Osee Dsl</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslImpl#getImports <em>Imports</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslImpl#getArtifactTypes <em>Artifact Types</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslImpl#getRelationTypes <em>Relation Types</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslImpl#getAttributeTypes <em>Attribute Types</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslImpl#getEnumTypes <em>Enum Types</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslImpl#getEnumOverrides <em>Enum Overrides</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslImpl#getArtifactTypeOverrides <em>Artifact Type Overrides</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslImpl#getArtifactMatchRefs <em>Artifact Match Refs</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslImpl#getAccessDeclarations <em>Access Declarations</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.core.dsl.oseeDsl.impl.OseeDslImpl#getRoleDeclarations <em>Role Declarations</em>}</li>
 * </ul>
 *
 * @generated
 */
public class OseeDslImpl extends MinimalEObjectImpl.Container implements OseeDsl
{
  /**
   * The cached value of the '{@link #getImports() <em>Imports</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getImports()
   * @generated
   * @ordered
   */
  protected EList<Import> imports;

  /**
   * The cached value of the '{@link #getArtifactTypes() <em>Artifact Types</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getArtifactTypes()
   * @generated
   * @ordered
   */
  protected EList<XArtifactType> artifactTypes;

  /**
   * The cached value of the '{@link #getRelationTypes() <em>Relation Types</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRelationTypes()
   * @generated
   * @ordered
   */
  protected EList<XRelationType> relationTypes;

  /**
   * The cached value of the '{@link #getAttributeTypes() <em>Attribute Types</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttributeTypes()
   * @generated
   * @ordered
   */
  protected EList<XAttributeType> attributeTypes;

  /**
   * The cached value of the '{@link #getEnumTypes() <em>Enum Types</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEnumTypes()
   * @generated
   * @ordered
   */
  protected EList<XOseeEnumType> enumTypes;

  /**
   * The cached value of the '{@link #getEnumOverrides() <em>Enum Overrides</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEnumOverrides()
   * @generated
   * @ordered
   */
  protected EList<XOseeEnumOverride> enumOverrides;

  /**
   * The cached value of the '{@link #getArtifactTypeOverrides() <em>Artifact Type Overrides</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getArtifactTypeOverrides()
   * @generated
   * @ordered
   */
  protected EList<XOseeArtifactTypeOverride> artifactTypeOverrides;

  /**
   * The cached value of the '{@link #getArtifactMatchRefs() <em>Artifact Match Refs</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getArtifactMatchRefs()
   * @generated
   * @ordered
   */
  protected EList<XArtifactMatcher> artifactMatchRefs;

  /**
   * The cached value of the '{@link #getAccessDeclarations() <em>Access Declarations</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAccessDeclarations()
   * @generated
   * @ordered
   */
  protected EList<AccessContext> accessDeclarations;

  /**
   * The cached value of the '{@link #getRoleDeclarations() <em>Role Declarations</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRoleDeclarations()
   * @generated
   * @ordered
   */
  protected EList<Role> roleDeclarations;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected OseeDslImpl()
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
    return OseeDslPackage.Literals.OSEE_DSL;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<Import> getImports()
  {
    if (imports == null)
    {
      imports = new EObjectContainmentEList<Import>(Import.class, this, OseeDslPackage.OSEE_DSL__IMPORTS);
    }
    return imports;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<XArtifactType> getArtifactTypes()
  {
    if (artifactTypes == null)
    {
      artifactTypes = new EObjectContainmentEList<XArtifactType>(XArtifactType.class, this, OseeDslPackage.OSEE_DSL__ARTIFACT_TYPES);
    }
    return artifactTypes;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<XRelationType> getRelationTypes()
  {
    if (relationTypes == null)
    {
      relationTypes = new EObjectContainmentEList<XRelationType>(XRelationType.class, this, OseeDslPackage.OSEE_DSL__RELATION_TYPES);
    }
    return relationTypes;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<XAttributeType> getAttributeTypes()
  {
    if (attributeTypes == null)
    {
      attributeTypes = new EObjectContainmentEList<XAttributeType>(XAttributeType.class, this, OseeDslPackage.OSEE_DSL__ATTRIBUTE_TYPES);
    }
    return attributeTypes;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<XOseeEnumType> getEnumTypes()
  {
    if (enumTypes == null)
    {
      enumTypes = new EObjectContainmentEList<XOseeEnumType>(XOseeEnumType.class, this, OseeDslPackage.OSEE_DSL__ENUM_TYPES);
    }
    return enumTypes;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<XOseeEnumOverride> getEnumOverrides()
  {
    if (enumOverrides == null)
    {
      enumOverrides = new EObjectContainmentEList<XOseeEnumOverride>(XOseeEnumOverride.class, this, OseeDslPackage.OSEE_DSL__ENUM_OVERRIDES);
    }
    return enumOverrides;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<XOseeArtifactTypeOverride> getArtifactTypeOverrides()
  {
    if (artifactTypeOverrides == null)
    {
      artifactTypeOverrides = new EObjectContainmentEList<XOseeArtifactTypeOverride>(XOseeArtifactTypeOverride.class, this, OseeDslPackage.OSEE_DSL__ARTIFACT_TYPE_OVERRIDES);
    }
    return artifactTypeOverrides;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<XArtifactMatcher> getArtifactMatchRefs()
  {
    if (artifactMatchRefs == null)
    {
      artifactMatchRefs = new EObjectContainmentEList<XArtifactMatcher>(XArtifactMatcher.class, this, OseeDslPackage.OSEE_DSL__ARTIFACT_MATCH_REFS);
    }
    return artifactMatchRefs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<AccessContext> getAccessDeclarations()
  {
    if (accessDeclarations == null)
    {
      accessDeclarations = new EObjectContainmentEList<AccessContext>(AccessContext.class, this, OseeDslPackage.OSEE_DSL__ACCESS_DECLARATIONS);
    }
    return accessDeclarations;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<Role> getRoleDeclarations()
  {
    if (roleDeclarations == null)
    {
      roleDeclarations = new EObjectContainmentEList<Role>(Role.class, this, OseeDslPackage.OSEE_DSL__ROLE_DECLARATIONS);
    }
    return roleDeclarations;
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
      case OseeDslPackage.OSEE_DSL__IMPORTS:
        return ((InternalEList<?>)getImports()).basicRemove(otherEnd, msgs);
      case OseeDslPackage.OSEE_DSL__ARTIFACT_TYPES:
        return ((InternalEList<?>)getArtifactTypes()).basicRemove(otherEnd, msgs);
      case OseeDslPackage.OSEE_DSL__RELATION_TYPES:
        return ((InternalEList<?>)getRelationTypes()).basicRemove(otherEnd, msgs);
      case OseeDslPackage.OSEE_DSL__ATTRIBUTE_TYPES:
        return ((InternalEList<?>)getAttributeTypes()).basicRemove(otherEnd, msgs);
      case OseeDslPackage.OSEE_DSL__ENUM_TYPES:
        return ((InternalEList<?>)getEnumTypes()).basicRemove(otherEnd, msgs);
      case OseeDslPackage.OSEE_DSL__ENUM_OVERRIDES:
        return ((InternalEList<?>)getEnumOverrides()).basicRemove(otherEnd, msgs);
      case OseeDslPackage.OSEE_DSL__ARTIFACT_TYPE_OVERRIDES:
        return ((InternalEList<?>)getArtifactTypeOverrides()).basicRemove(otherEnd, msgs);
      case OseeDslPackage.OSEE_DSL__ARTIFACT_MATCH_REFS:
        return ((InternalEList<?>)getArtifactMatchRefs()).basicRemove(otherEnd, msgs);
      case OseeDslPackage.OSEE_DSL__ACCESS_DECLARATIONS:
        return ((InternalEList<?>)getAccessDeclarations()).basicRemove(otherEnd, msgs);
      case OseeDslPackage.OSEE_DSL__ROLE_DECLARATIONS:
        return ((InternalEList<?>)getRoleDeclarations()).basicRemove(otherEnd, msgs);
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
      case OseeDslPackage.OSEE_DSL__IMPORTS:
        return getImports();
      case OseeDslPackage.OSEE_DSL__ARTIFACT_TYPES:
        return getArtifactTypes();
      case OseeDslPackage.OSEE_DSL__RELATION_TYPES:
        return getRelationTypes();
      case OseeDslPackage.OSEE_DSL__ATTRIBUTE_TYPES:
        return getAttributeTypes();
      case OseeDslPackage.OSEE_DSL__ENUM_TYPES:
        return getEnumTypes();
      case OseeDslPackage.OSEE_DSL__ENUM_OVERRIDES:
        return getEnumOverrides();
      case OseeDslPackage.OSEE_DSL__ARTIFACT_TYPE_OVERRIDES:
        return getArtifactTypeOverrides();
      case OseeDslPackage.OSEE_DSL__ARTIFACT_MATCH_REFS:
        return getArtifactMatchRefs();
      case OseeDslPackage.OSEE_DSL__ACCESS_DECLARATIONS:
        return getAccessDeclarations();
      case OseeDslPackage.OSEE_DSL__ROLE_DECLARATIONS:
        return getRoleDeclarations();
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
      case OseeDslPackage.OSEE_DSL__IMPORTS:
        getImports().clear();
        getImports().addAll((Collection<? extends Import>)newValue);
        return;
      case OseeDslPackage.OSEE_DSL__ARTIFACT_TYPES:
        getArtifactTypes().clear();
        getArtifactTypes().addAll((Collection<? extends XArtifactType>)newValue);
        return;
      case OseeDslPackage.OSEE_DSL__RELATION_TYPES:
        getRelationTypes().clear();
        getRelationTypes().addAll((Collection<? extends XRelationType>)newValue);
        return;
      case OseeDslPackage.OSEE_DSL__ATTRIBUTE_TYPES:
        getAttributeTypes().clear();
        getAttributeTypes().addAll((Collection<? extends XAttributeType>)newValue);
        return;
      case OseeDslPackage.OSEE_DSL__ENUM_TYPES:
        getEnumTypes().clear();
        getEnumTypes().addAll((Collection<? extends XOseeEnumType>)newValue);
        return;
      case OseeDslPackage.OSEE_DSL__ENUM_OVERRIDES:
        getEnumOverrides().clear();
        getEnumOverrides().addAll((Collection<? extends XOseeEnumOverride>)newValue);
        return;
      case OseeDslPackage.OSEE_DSL__ARTIFACT_TYPE_OVERRIDES:
        getArtifactTypeOverrides().clear();
        getArtifactTypeOverrides().addAll((Collection<? extends XOseeArtifactTypeOverride>)newValue);
        return;
      case OseeDslPackage.OSEE_DSL__ARTIFACT_MATCH_REFS:
        getArtifactMatchRefs().clear();
        getArtifactMatchRefs().addAll((Collection<? extends XArtifactMatcher>)newValue);
        return;
      case OseeDslPackage.OSEE_DSL__ACCESS_DECLARATIONS:
        getAccessDeclarations().clear();
        getAccessDeclarations().addAll((Collection<? extends AccessContext>)newValue);
        return;
      case OseeDslPackage.OSEE_DSL__ROLE_DECLARATIONS:
        getRoleDeclarations().clear();
        getRoleDeclarations().addAll((Collection<? extends Role>)newValue);
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
      case OseeDslPackage.OSEE_DSL__IMPORTS:
        getImports().clear();
        return;
      case OseeDslPackage.OSEE_DSL__ARTIFACT_TYPES:
        getArtifactTypes().clear();
        return;
      case OseeDslPackage.OSEE_DSL__RELATION_TYPES:
        getRelationTypes().clear();
        return;
      case OseeDslPackage.OSEE_DSL__ATTRIBUTE_TYPES:
        getAttributeTypes().clear();
        return;
      case OseeDslPackage.OSEE_DSL__ENUM_TYPES:
        getEnumTypes().clear();
        return;
      case OseeDslPackage.OSEE_DSL__ENUM_OVERRIDES:
        getEnumOverrides().clear();
        return;
      case OseeDslPackage.OSEE_DSL__ARTIFACT_TYPE_OVERRIDES:
        getArtifactTypeOverrides().clear();
        return;
      case OseeDslPackage.OSEE_DSL__ARTIFACT_MATCH_REFS:
        getArtifactMatchRefs().clear();
        return;
      case OseeDslPackage.OSEE_DSL__ACCESS_DECLARATIONS:
        getAccessDeclarations().clear();
        return;
      case OseeDslPackage.OSEE_DSL__ROLE_DECLARATIONS:
        getRoleDeclarations().clear();
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
      case OseeDslPackage.OSEE_DSL__IMPORTS:
        return imports != null && !imports.isEmpty();
      case OseeDslPackage.OSEE_DSL__ARTIFACT_TYPES:
        return artifactTypes != null && !artifactTypes.isEmpty();
      case OseeDslPackage.OSEE_DSL__RELATION_TYPES:
        return relationTypes != null && !relationTypes.isEmpty();
      case OseeDslPackage.OSEE_DSL__ATTRIBUTE_TYPES:
        return attributeTypes != null && !attributeTypes.isEmpty();
      case OseeDslPackage.OSEE_DSL__ENUM_TYPES:
        return enumTypes != null && !enumTypes.isEmpty();
      case OseeDslPackage.OSEE_DSL__ENUM_OVERRIDES:
        return enumOverrides != null && !enumOverrides.isEmpty();
      case OseeDslPackage.OSEE_DSL__ARTIFACT_TYPE_OVERRIDES:
        return artifactTypeOverrides != null && !artifactTypeOverrides.isEmpty();
      case OseeDslPackage.OSEE_DSL__ARTIFACT_MATCH_REFS:
        return artifactMatchRefs != null && !artifactMatchRefs.isEmpty();
      case OseeDslPackage.OSEE_DSL__ACCESS_DECLARATIONS:
        return accessDeclarations != null && !accessDeclarations.isEmpty();
      case OseeDslPackage.OSEE_DSL__ROLE_DECLARATIONS:
        return roleDeclarations != null && !roleDeclarations.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} //OseeDslImpl
