/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import { TestBed } from '@angular/core/testing';
import { TestScheduler } from 'rxjs/testing';
import { UiService } from '../../../../../ple-services/ui/ui.service';
import { TransactionBuilderService } from '../../../../../transactions/transaction-builder.service';
import { transactionBuilderMock } from '../../../../../transactions/transaction-builder.service.mock';
import { transactionResultMock } from '../../../../../transactions/transaction.mock';
import { UserDataAccountService } from '../../../../../userdata/services/user-data-account.service';
import { userDataAccountServiceMock } from '../../../../plconfig/testing/mockUserDataAccountService';
import { applicabilityListServiceMock } from '../../mocks/ApplicabilityListService.mock';
import { enumerationSetServiceMock } from '../../mocks/enumeration.set.service.mock';
import { enumsServiceMock } from '../../mocks/EnumsService.mock';
import { MimPreferencesServiceMock } from '../../mocks/MimPreferencesService.mock';
import { typesServiceMock } from '../../mocks/types.service.mock';
import { ApplicabilityListService } from '../http/applicability-list.service';
import { EnumerationSetService } from '../http/enumeration-set.service';
import { EnumsService } from '../http/enums.service';
import { MimPreferencesService } from '../http/mim-preferences.service';
import { TypesService } from '../http/types.service';

import { NewTypeDialogService } from './new-type-dialog.service';

describe('NewTypeDialogService', () => {
  let service: NewTypeDialogService;
  let uiService: UiService;
  let scheduler: TestScheduler;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        { provide: TransactionBuilderService, useValue: transactionBuilderMock },
        { provide: MimPreferencesService, useValue: MimPreferencesServiceMock },
        { provide: UserDataAccountService, useValue: userDataAccountServiceMock },
        { provide: TypesService, useValue: typesServiceMock },
        { provide: EnumsService, useValue: enumsServiceMock },
        { provide: EnumerationSetService, useValue: enumerationSetServiceMock },
        { provide: ApplicabilityListService, useValue: applicabilityListServiceMock}
      ]
    });
    service = TestBed.inject(NewTypeDialogService);
    uiService = TestBed.inject(UiService);
  });
  beforeEach(() => scheduler = new TestScheduler((actual, expected) => {
    expect(actual).toEqual(expected);
  }));
  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should send a post request to create type,enum set, enum', () => {
    scheduler.run(() => {
      const expectedFilterValues = { a: transactionResultMock };
      const expectedMarble = '(a|)';
      uiService.idValue = "10";
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
      const expectedFilterValues = { a: transactionResultMock };
      const expectedMarble = '(a|)';
      uiService.idValue = "10";
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
      const expectedFilterValues = { a: transactionResultMock };
      const expectedMarble = '(a|)';
      uiService.idValue = "10";
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
      const expectedFilterValues = { a: transactionResultMock };
      const expectedMarble = '(a|)';
      uiService.idValue = "10";
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
});
