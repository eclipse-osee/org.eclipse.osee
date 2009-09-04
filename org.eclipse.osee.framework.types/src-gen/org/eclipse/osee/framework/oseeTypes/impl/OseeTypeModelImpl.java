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

import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.osee.framework.oseeTypes.ArtifactType;
import org.eclipse.osee.framework.oseeTypes.AttributeType;
import org.eclipse.osee.framework.oseeTypes.Import;
import org.eclipse.osee.framework.oseeTypes.OseeEnumOverride;
import org.eclipse.osee.framework.oseeTypes.OseeEnumType;
import org.eclipse.osee.framework.oseeTypes.OseeTypeModel;
import org.eclipse.osee.framework.oseeTypes.OseeTypesPackage;
import org.eclipse.osee.framework.oseeTypes.RelationType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Osee Type Model</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.impl.OseeTypeModelImpl#getImports <em>Imports</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.impl.OseeTypeModelImpl#getArtifactTypes <em>Artifact Types</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.impl.OseeTypeModelImpl#getRelationTypes <em>Relation Types</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.impl.OseeTypeModelImpl#getAttributeTypes <em>Attribute Types</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.impl.OseeTypeModelImpl#getEnumTypes <em>Enum Types</em>}</li>
 *   <li>{@link org.eclipse.osee.framework.oseeTypes.impl.OseeTypeModelImpl#getEnumOverrides <em>Enum Overrides</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class OseeTypeModelImpl extends MinimalEObjectImpl.Container implements OseeTypeModel
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
  protected EList<ArtifactType> artifactTypes;

  /**
   * The cached value of the '{@link #getRelationTypes() <em>Relation Types</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRelationTypes()
   * @generated
   * @ordered
   */
  protected EList<RelationType> relationTypes;

  /**
   * The cached value of the '{@link #getAttributeTypes() <em>Attribute Types</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAttributeTypes()
   * @generated
   * @ordered
   */
  protected EList<AttributeType> attributeTypes;

  /**
   * The cached value of the '{@link #getEnumTypes() <em>Enum Types</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEnumTypes()
   * @generated
   * @ordered
   */
  protected EList<OseeEnumType> enumTypes;

  /**
   * The cached value of the '{@link #getEnumOverrides() <em>Enum Overrides</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEnumOverrides()
   * @generated
   * @ordered
   */
  protected EList<OseeEnumOverride> enumOverrides;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected OseeTypeModelImpl()
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
    return OseeTypesPackage.Literals.OSEE_TYPE_MODEL;
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
      imports = new EObjectContainmentEList<Import>(Import.class, this, OseeTypesPackage.OSEE_TYPE_MODEL__IMPORTS);
    }
    return imports;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<ArtifactType> getArtifactTypes()
  {
    if (artifactTypes == null)
    {
      artifactTypes = new EObjectContainmentEList<ArtifactType>(ArtifactType.class, this, OseeTypesPackage.OSEE_TYPE_MODEL__ARTIFACT_TYPES);
    }
    return artifactTypes;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<RelationType> getRelationTypes()
  {
    if (relationTypes == null)
    {
      relationTypes = new EObjectContainmentEList<RelationType>(RelationType.class, this, OseeTypesPackage.OSEE_TYPE_MODEL__RELATION_TYPES);
    }
    return relationTypes;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<AttributeType> getAttributeTypes()
  {
    if (attributeTypes == null)
    {
      attributeTypes = new EObjectContainmentEList<AttributeType>(AttributeType.class, this, OseeTypesPackage.OSEE_TYPE_MODEL__ATTRIBUTE_TYPES);
    }
    return attributeTypes;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<OseeEnumType> getEnumTypes()
  {
    if (enumTypes == null)
    {
      enumTypes = new EObjectContainmentEList<OseeEnumType>(OseeEnumType.class, this, OseeTypesPackage.OSEE_TYPE_MODEL__ENUM_TYPES);
    }
    return enumTypes;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<OseeEnumOverride> getEnumOverrides()
  {
    if (enumOverrides == null)
    {
      enumOverrides = new EObjectContainmentEList<OseeEnumOverride>(OseeEnumOverride.class, this, OseeTypesPackage.OSEE_TYPE_MODEL__ENUM_OVERRIDES);
    }
    return enumOverrides;
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
      case OseeTypesPackage.OSEE_TYPE_MODEL__IMPORTS:
        return ((InternalEList<?>)getImports()).basicRemove(otherEnd, msgs);
      case OseeTypesPackage.OSEE_TYPE_MODEL__ARTIFACT_TYPES:
        return ((InternalEList<?>)getArtifactTypes()).basicRemove(otherEnd, msgs);
      case OseeTypesPackage.OSEE_TYPE_MODEL__RELATION_TYPES:
        return ((InternalEList<?>)getRelationTypes()).basicRemove(otherEnd, msgs);
      case OseeTypesPackage.OSEE_TYPE_MODEL__ATTRIBUTE_TYPES:
        return ((InternalEList<?>)getAttributeTypes()).basicRemove(otherEnd, msgs);
      case OseeTypesPackage.OSEE_TYPE_MODEL__ENUM_TYPES:
        return ((InternalEList<?>)getEnumTypes()).basicRemove(otherEnd, msgs);
      case OseeTypesPackage.OSEE_TYPE_MODEL__ENUM_OVERRIDES:
        return ((InternalEList<?>)getEnumOverrides()).basicRemove(otherEnd, msgs);
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
      case OseeTypesPackage.OSEE_TYPE_MODEL__IMPORTS:
        return getImports();
      case OseeTypesPackage.OSEE_TYPE_MODEL__ARTIFACT_TYPES:
        return getArtifactTypes();
      case OseeTypesPackage.OSEE_TYPE_MODEL__RELATION_TYPES:
        return getRelationTypes();
      case OseeTypesPackage.OSEE_TYPE_MODEL__ATTRIBUTE_TYPES:
        return getAttributeTypes();
      case OseeTypesPackage.OSEE_TYPE_MODEL__ENUM_TYPES:
        return getEnumTypes();
      case OseeTypesPackage.OSEE_TYPE_MODEL__ENUM_OVERRIDES:
        return getEnumOverrides();
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
      case OseeTypesPackage.OSEE_TYPE_MODEL__IMPORTS:
        getImports().clear();
        getImports().addAll((Collection<? extends Import>)newValue);
        return;
      case OseeTypesPackage.OSEE_TYPE_MODEL__ARTIFACT_TYPES:
        getArtifactTypes().clear();
        getArtifactTypes().addAll((Collection<? extends ArtifactType>)newValue);
        return;
      case OseeTypesPackage.OSEE_TYPE_MODEL__RELATION_TYPES:
        getRelationTypes().clear();
        getRelationTypes().addAll((Collection<? extends RelationType>)newValue);
        return;
      case OseeTypesPackage.OSEE_TYPE_MODEL__ATTRIBUTE_TYPES:
        getAttributeTypes().clear();
        getAttributeTypes().addAll((Collection<? extends AttributeType>)newValue);
        return;
      case OseeTypesPackage.OSEE_TYPE_MODEL__ENUM_TYPES:
        getEnumTypes().clear();
        getEnumTypes().addAll((Collection<? extends OseeEnumType>)newValue);
        return;
      case OseeTypesPackage.OSEE_TYPE_MODEL__ENUM_OVERRIDES:
        getEnumOverrides().clear();
        getEnumOverrides().addAll((Collection<? extends OseeEnumOverride>)newValue);
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
      case OseeTypesPackage.OSEE_TYPE_MODEL__IMPORTS:
        getImports().clear();
        return;
      case OseeTypesPackage.OSEE_TYPE_MODEL__ARTIFACT_TYPES:
        getArtifactTypes().clear();
        return;
      case OseeTypesPackage.OSEE_TYPE_MODEL__RELATION_TYPES:
        getRelationTypes().clear();
        return;
      case OseeTypesPackage.OSEE_TYPE_MODEL__ATTRIBUTE_TYPES:
        getAttributeTypes().clear();
        return;
      case OseeTypesPackage.OSEE_TYPE_MODEL__ENUM_TYPES:
        getEnumTypes().clear();
        return;
      case OseeTypesPackage.OSEE_TYPE_MODEL__ENUM_OVERRIDES:
        getEnumOverrides().clear();
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
      case OseeTypesPackage.OSEE_TYPE_MODEL__IMPORTS:
        return imports != null && !imports.isEmpty();
      case OseeTypesPackage.OSEE_TYPE_MODEL__ARTIFACT_TYPES:
        return artifactTypes != null && !artifactTypes.isEmpty();
      case OseeTypesPackage.OSEE_TYPE_MODEL__RELATION_TYPES:
        return relationTypes != null && !relationTypes.isEmpty();
      case OseeTypesPackage.OSEE_TYPE_MODEL__ATTRIBUTE_TYPES:
        return attributeTypes != null && !attributeTypes.isEmpty();
      case OseeTypesPackage.OSEE_TYPE_MODEL__ENUM_TYPES:
        return enumTypes != null && !enumTypes.isEmpty();
      case OseeTypesPackage.OSEE_TYPE_MODEL__ENUM_OVERRIDES:
        return enumOverrides != null && !enumOverrides.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} //OseeTypeModelImpl
