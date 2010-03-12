/**
 * <copyright>
 * </copyright>
 *

 */
package org.eclipse.osee.framework.oseeTypes.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.osee.framework.oseeTypes.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.osee.framework.oseeTypes.OseeTypesPackage
 * @generated
 */
public class OseeTypesAdapterFactory extends AdapterFactoryImpl
{
  /**
   * The cached model package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected static OseeTypesPackage modelPackage;

  /**
   * Creates an instance of the adapter factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public OseeTypesAdapterFactory()
  {
    if (modelPackage == null)
    {
      modelPackage = OseeTypesPackage.eINSTANCE;
    }
  }

  /**
   * Returns whether this factory is applicable for the type of the object.
   * <!-- begin-user-doc -->
   * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
   * <!-- end-user-doc -->
   * @return whether this factory is applicable for the type of the object.
   * @generated
   */
  @Override
  public boolean isFactoryForType(Object object)
  {
    if (object == modelPackage)
    {
      return true;
    }
    if (object instanceof EObject)
    {
      return ((EObject)object).eClass().getEPackage() == modelPackage;
    }
    return false;
  }

  /**
   * The switch that delegates to the <code>createXXX</code> methods.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected OseeTypesSwitch<Adapter> modelSwitch =
    new OseeTypesSwitch<Adapter>()
    {
      @Override
      public Adapter caseOseeTypeModel(OseeTypeModel object)
      {
        return createOseeTypeModelAdapter();
      }
      @Override
      public Adapter caseImport(Import object)
      {
        return createImportAdapter();
      }
      @Override
      public Adapter caseOseeElement(OseeElement object)
      {
        return createOseeElementAdapter();
      }
      @Override
      public Adapter caseOseeType(OseeType object)
      {
        return createOseeTypeAdapter();
      }
      @Override
      public Adapter caseXArtifactType(XArtifactType object)
      {
        return createXArtifactTypeAdapter();
      }
      @Override
      public Adapter caseXAttributeTypeRef(XAttributeTypeRef object)
      {
        return createXAttributeTypeRefAdapter();
      }
      @Override
      public Adapter caseXAttributeType(XAttributeType object)
      {
        return createXAttributeTypeAdapter();
      }
      @Override
      public Adapter caseXOseeEnumType(XOseeEnumType object)
      {
        return createXOseeEnumTypeAdapter();
      }
      @Override
      public Adapter caseXOseeEnumEntry(XOseeEnumEntry object)
      {
        return createXOseeEnumEntryAdapter();
      }
      @Override
      public Adapter caseXOseeEnumOverride(XOseeEnumOverride object)
      {
        return createXOseeEnumOverrideAdapter();
      }
      @Override
      public Adapter caseOverrideOption(OverrideOption object)
      {
        return createOverrideOptionAdapter();
      }
      @Override
      public Adapter caseAddEnum(AddEnum object)
      {
        return createAddEnumAdapter();
      }
      @Override
      public Adapter caseRemoveEnum(RemoveEnum object)
      {
        return createRemoveEnumAdapter();
      }
      @Override
      public Adapter caseXRelationType(XRelationType object)
      {
        return createXRelationTypeAdapter();
      }
      @Override
      public Adapter defaultCase(EObject object)
      {
        return createEObjectAdapter();
      }
    };

  /**
   * Creates an adapter for the <code>target</code>.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param target the object to adapt.
   * @return the adapter for the <code>target</code>.
   * @generated
   */
  @Override
  public Adapter createAdapter(Notifier target)
  {
    return modelSwitch.doSwitch((EObject)target);
  }


  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.oseeTypes.OseeTypeModel <em>Osee Type Model</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.framework.oseeTypes.OseeTypeModel
   * @generated
   */
  public Adapter createOseeTypeModelAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.oseeTypes.Import <em>Import</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.framework.oseeTypes.Import
   * @generated
   */
  public Adapter createImportAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.oseeTypes.OseeElement <em>Osee Element</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.framework.oseeTypes.OseeElement
   * @generated
   */
  public Adapter createOseeElementAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.oseeTypes.OseeType <em>Osee Type</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.framework.oseeTypes.OseeType
   * @generated
   */
  public Adapter createOseeTypeAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.oseeTypes.XArtifactType <em>XArtifact Type</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.framework.oseeTypes.XArtifactType
   * @generated
   */
  public Adapter createXArtifactTypeAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.oseeTypes.XAttributeTypeRef <em>XAttribute Type Ref</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.framework.oseeTypes.XAttributeTypeRef
   * @generated
   */
  public Adapter createXAttributeTypeRefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.oseeTypes.XAttributeType <em>XAttribute Type</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.framework.oseeTypes.XAttributeType
   * @generated
   */
  public Adapter createXAttributeTypeAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.oseeTypes.XOseeEnumType <em>XOsee Enum Type</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.framework.oseeTypes.XOseeEnumType
   * @generated
   */
  public Adapter createXOseeEnumTypeAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.oseeTypes.XOseeEnumEntry <em>XOsee Enum Entry</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.framework.oseeTypes.XOseeEnumEntry
   * @generated
   */
  public Adapter createXOseeEnumEntryAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.oseeTypes.XOseeEnumOverride <em>XOsee Enum Override</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.framework.oseeTypes.XOseeEnumOverride
   * @generated
   */
  public Adapter createXOseeEnumOverrideAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.oseeTypes.OverrideOption <em>Override Option</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.framework.oseeTypes.OverrideOption
   * @generated
   */
  public Adapter createOverrideOptionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.oseeTypes.AddEnum <em>Add Enum</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.framework.oseeTypes.AddEnum
   * @generated
   */
  public Adapter createAddEnumAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.oseeTypes.RemoveEnum <em>Remove Enum</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.framework.oseeTypes.RemoveEnum
   * @generated
   */
  public Adapter createRemoveEnumAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.osee.framework.oseeTypes.XRelationType <em>XRelation Type</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.osee.framework.oseeTypes.XRelationType
   * @generated
   */
  public Adapter createXRelationTypeAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for the default case.
   * <!-- begin-user-doc -->
   * This default implementation returns null.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @generated
   */
  public Adapter createEObjectAdapter()
  {
    return null;
  }

} //OseeTypesAdapterFactory
