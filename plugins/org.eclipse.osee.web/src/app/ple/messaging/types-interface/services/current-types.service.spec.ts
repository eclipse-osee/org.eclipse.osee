/*********************************************************************
 * Copyright (c) 2021 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { fakeAsync, TestBed, tick } from '@angular/core/testing';
import { TestScheduler } from 'rxjs/testing';
import { userDataAccountServiceMock } from 'src/app/ple/plconfig/testing/mockUserDataAccountService';
import { TransactionBuilderService } from 'src/app/transactions/transaction-builder.service';
import { transactionBuilderMock } from 'src/app/transactions/transaction-builder.service.mock';
import { UserDataAccountService } from 'src/app/userdata/services/user-data-account.service';
import { apiURL } from 'src/environments/environment';
import { response } from '../../connection-view/mocks/Response.mock';
import { applicabilityListServiceMock } from '../../shared/mocks/ApplicabilityListService.mock';
import { MimPreferencesMock } from '../../shared/mocks/MimPreferences.mock';
import { MimPreferencesServiceMock } from '../../shared/mocks/MimPreferencesService.mock';
import { ApplicabilityListService } from '../../shared/services/http/applicability-list.service';
import { MimPreferencesService } from '../../shared/services/http/mim-preferences.service';
import { platformTypes1 } from '../../type-element-search/testing/MockResponses/PlatformType';
import { enumerationSetMock } from '../mocks/returnObjects/enumerationset.mock';
import { logicalTypeMock } from '../mocks/returnObjects/logicalType.mock';
import { logicalTypeFormDetailMock } from '../mocks/returnObjects/logicalTypeFormDetail.mock';
import { enumerationSetServiceMock } from '../../shared/mocks/enumeration.set.service.mock';
import { typesServiceMock } from '../mocks/services/types.service.mock';
import { TypesApiResponse } from '../types/ApiResponse';
import { logicalType, logicalTypeFormDetail } from '../types/logicaltype';
import { PlatformType } from '../types/platformType.d';

import { CurrentTypesService } from './current-types.service';
import { EnumerationSetService } from '../../shared/services/http/enumeration-set.service';
import { PlMessagingTypesUIService } from './pl-messaging-types-ui.service';
import { TypesService } from './types.service';

class PlatformTypeInstance implements PlatformType{
  id?: string | undefined ='';
  interfaceLogicalType: string='';
  interfacePlatform2sComplement: boolean=false;
  interfacePlatformTypeAnalogAccuracy: string | null='';
  interfacePlatformTypeBitsResolution: string | null='';
  interfacePlatformTypeBitSize: string | null='';
  interfacePlatformTypeCompRate: string | null='';
  interfacePlatformTypeDefaultValue: string | null='';
  interfacePlatformTypeEnumLiteral: string | null='';
  interfacePlatformTypeMaxval: string | null='';
  interfacePlatformTypeMinval: string | null='';
  interfacePlatformTypeMsbValue: string | null='';
  interfacePlatformTypeUnits: string | null='';
  interfacePlatformTypeValidRangeDescription: string | null='';
  name: string='';
  constructor () {}
  
}

describe('CurrentTypesServiceService', () => {
  let service: CurrentTypesService;
  let uiService: PlMessagingTypesUIService;
  let scheduler: TestScheduler;
  let httpTestingController:HttpTestingController

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers:
        [
          { provide: TransactionBuilderService, useValue: transactionBuilderMock },
          { provide: MimPreferencesService, useValue: MimPreferencesServiceMock },
          { provide: UserDataAccountService, useValue: userDataAccountServiceMock },
          { provide: TypesService, useValue: typesServiceMock },
          { provide: EnumerationSetService, useValue: enumerationSetServiceMock },
          { provide: ApplicabilityListService, useValue: applicabilityListServiceMock}
        ],
      imports:[HttpClientTestingModule]
    });
    service = TestBed.inject(CurrentTypesService);
    uiService = TestBed.inject(PlMessagingTypesUIService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  beforeEach(() => scheduler = new TestScheduler((actual, expected) => {
    expect(actual).toEqual(expected);
  }));

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch data from backend', () => {
    scheduler.run(() => {
      const expectedFilterValues = { a: platformTypes1 };
      const expectedMarble = '500ms a';
      uiService.BranchIdString = "10";
      scheduler.expectObservable(service.typeData).toBe(expectedMarble, expectedFilterValues);
      uiService.filterString = "A filter";
    })
  });

  it('should set singleLineAdjustment to 0', () => {
    scheduler.run(() => {
      const expectedFilterValues = { a: 30,b:0 };
      const expectedMarble = 'b';
      uiService.columnCountNumber = 2;
      uiService.BranchIdString = "10";
      uiService.filterString = "A filter";
      scheduler.expectObservable(uiService.singleLineAdjustment).toBe(expectedMarble, expectedFilterValues);
    })
  });
  it('should set singleLineAdjustment to 30', () => {
    scheduler.run(() => {
      const expectedFilterValues = { a: 30,b:0 };
      const expectedMarble = 'b 499ms a';
      uiService.columnCountNumber = 9;
      uiService.BranchIdString = "10";
      uiService.filterString = "A filter";
      scheduler.expectObservable(uiService.singleLineAdjustment).toBe(expectedMarble, expectedFilterValues);
      const expectedFilterValues2 = { a: platformTypes1 };
      const expectedMarble2 = '500ms a';
      scheduler.expectObservable(service.typeData).toBe(expectedMarble2, expectedFilterValues2);
    })
  });

  it('should send a modification request', () => {
    scheduler.run(() => {
      const expectedFilterValues = { a: response };
      const expectedMarble = '(a|)';
      uiService.BranchIdString = "10";
      scheduler.expectObservable(service.partialUpdate({})).toBe(expectedMarble, expectedFilterValues);
    })

  });

  it('should send a post request to copy type', () => {
    scheduler.run(() => {
      const expectedFilterValues = { a: response };
      const expectedMarble = '(a|)';
      uiService.BranchIdString = "10";
      scheduler.expectObservable(service.copyType({})).toBe(expectedMarble, expectedFilterValues);
    })
  });

  it('should send a post request to create type,enum set, enum', () => {
    scheduler.run(() => {
      const expectedFilterValues = { a: response };
      const expectedMarble = '(a|)';
      uiService.BranchIdString = "10";
      scheduler.expectObservable(service.createType({}, true, {
        enumSetId: '1', enumSetName: 'hello', enumSetApplicability: { id: '1', name: 'Base' }, enumSetDescription: 'description', enums:[
          {
            name: 'Hello',
            ordinal: 0,
            applicability:{id:'1',name:"base"}
          }
        ]})).toBe(expectedMarble, expectedFilterValues);
    })
  });

  it('should send a post request to create type', () => {
    scheduler.run(() => {
      const expectedFilterValues = { a: response };
      const expectedMarble = '(a|)';
      uiService.BranchIdString = "10";
      scheduler.expectObservable(service.createType({}, false, {
        enumSetId: '1', enumSetName: 'hello', enumSetApplicability: { id: '1', name: 'Base' }, enumSetDescription: 'description', enums:[
          {
            name: 'Hello',
            ordinal: 0,
            applicability:{id:'1',name:"base"}
          }
        ]})).toBe(expectedMarble, expectedFilterValues);
    })
  });

  it('should send a post request to create type with new enum set', () => {
    scheduler.run(() => {
      const expectedFilterValues = { a: response };
      const expectedMarble = '(a|)';
      uiService.BranchIdString = "10";
      scheduler.expectObservable(service.createType({interfaceLogicalType:'enumeration'}, true, {
        enumSetId: '1', enumSetName: 'hello', enumSetApplicability: { id: '1', name: 'Base' }, enumSetDescription: 'description', enums:[
          {
            name: 'Hello',
            ordinal: 0,
            applicability:{id:'1',name:"base"}
          }
        ]})).toBe(expectedMarble, expectedFilterValues);
    })
  });

  it('should send a post request to create type with existing enum set', () => {
    scheduler.run(() => {
      const expectedFilterValues = { a: response };
      const expectedMarble = '(a|)';
      uiService.BranchIdString = "10";
      scheduler.expectObservable(service.createType({interfaceLogicalType:'enumeration'}, false, {
        enumSetId: '1', enumSetName: 'hello', enumSetApplicability: { id: '1', name: 'Base' }, enumSetDescription: 'description', enums:[
          {
            name: 'Hello',
            ordinal: 0,
            applicability:{id:'1',name:"base"}
          }
        ]})).toBe(expectedMarble, expectedFilterValues);
    })
  });

  it('should fetch logical types', () => {
    scheduler.run(() => {
      const expectedFilterValues = { a: logicalTypeMock };
      const expectedMarble = '(a|)';
      uiService.BranchIdString = "10";
      scheduler.expectObservable(service.logicalTypes).toBe(expectedMarble, expectedFilterValues);
    })
  });

  it('should fetch a specific logical type', () => {
    scheduler.run(() => {
      const expectedFilterValues = { a: logicalTypeFormDetailMock };
      const expectedMarble = '(a|)';
      uiService.BranchIdString = "10";
      scheduler.expectObservable(service.getLogicalTypeFormDetail("1")).toBe(expectedMarble, expectedFilterValues);
    })
  });
  it('should fetch preferences', () => {
    scheduler.run(() => {
      const expectedFilterValues = { a: MimPreferencesMock };
      const expectedMarble = 'a';
      uiService.BranchIdString = "10";
      scheduler.expectObservable(service.preferences).toBe(expectedMarble, expectedFilterValues);
    })
  });
  it('should fetch branch prefs', () => {
    scheduler.run(() => {
      const expectedFilterValues = { a: ['8:true'] };
      const expectedMarble = 'a';
      uiService.BranchIdString = "10";
      scheduler.expectObservable(service.BranchPrefs).toBe(expectedMarble, expectedFilterValues);
    })
  })

  it('should fetch edit mode', () => {
    scheduler.run(() => {
      const expectedFilterValues = { a: MimPreferencesMock.inEditMode };
      const expectedMarble = 'a';
      uiService.BranchIdString = "10";
      scheduler.expectObservable(service.inEditMode).toBe(expectedMarble, expectedFilterValues);
    })
  });

  it('should update user preferences', () => {
    scheduler.run(() => {
      const expectedObservable = { a: response };
      const expectedMarble = '(a|)';
      uiService.BranchIdString = "10";
      scheduler.expectObservable(service.updatePreferences({branchId:'10',allowedHeaders1:['name','description'],allowedHeaders2:['name','description'],allHeaders1:['name'],allHeaders2:['name'],editable:true,headers1Label:'',headers2Label:'',headersTableActive:false})).toBe(expectedMarble, expectedObservable);
    })
  })

  it('should get enum sets', () => {
    scheduler.run(() => {
      const expectedFilterValues = { a: enumerationSetMock };
      const expectedMarble = 'a';
      uiService.BranchIdString = "10";
      scheduler.expectObservable(service.enumSets).toBe(expectedMarble, expectedFilterValues);
    })
  });

  it('should get applicabilities', () => {
    scheduler.run(() => {
      let expectedObservable = { a: [{id:'1',name:'Base'},{id:'2',name:'Second'}] };
      let expectedMarble = 'a';
      scheduler.expectObservable(service.applic).toBe(expectedMarble, expectedObservable);
    })
  })

  it('should get a specific enum set', () => {
    scheduler.run(() => {
      const expectedFilterValues = { a: enumerationSetMock[0] };
      const expectedMarble = '(a|)';
      uiService.BranchIdString = "10";
      scheduler.expectObservable(service.getEnumSet('0')).toBe(expectedMarble, expectedFilterValues);
    })
  })

  it('should change an enum set', () => {
    scheduler.run(() => {
      const expectedFilterValues = { a: response };
      const expectedMarble = '(a|)';
      uiService.BranchIdString = '10';
      scheduler.expectObservable(service.changeEnumSet({ name: '', applicability: { id: '1', name: 'Base' }, description: '' })).toBe(expectedMarble, expectedFilterValues);
    })
  })
});
