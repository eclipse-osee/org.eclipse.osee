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
import { TestBed } from '@angular/core/testing';
import { TestScheduler } from 'rxjs/testing';
import { relation } from 'src/app/transactions/transaction';
import { TransactionBuilderService } from 'src/app/transactions/transaction-builder.service';
import { transactionBuilderMock } from 'src/app/transactions/transaction-builder.service.mock';
import { transactionMock } from 'src/app/transactions/transaction.mock';
import { apiURL } from 'src/environments/environment';

import { EnumerationSetService } from './enumeration-set.service';

describe('EnumerationSetServiceService', () => {
  let service: EnumerationSetService;
  let scheduler: TestScheduler;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers:[{provide:TransactionBuilderService,useValue:transactionBuilderMock}],
      imports:[HttpClientTestingModule]
    });
    service = TestBed.inject(EnumerationSetService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });
  beforeEach(() => scheduler = new TestScheduler((actual, expected) => {
    expect(actual).toEqual(expected);
  }));

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should create an enum set', () => {
    scheduler.run(() => {
      const expectedFilterValues = { a:transactionMock};
      const expectedMarble = '(a|)';
      scheduler.expectObservable(service.createEnumSet('10', {}, [])).toBe(expectedMarble, expectedFilterValues);
    })
  })

  it('should create an enum', () => {
    scheduler.run(() => {
      const expectedFilterValues = { a:transactionMock};
      const expectedMarble = '(a|)';
      scheduler.expectObservable(service.createEnum('10', {}, [])).toBe(expectedMarble, expectedFilterValues);
    })
  })

  it('should change an enum set', () => {
    scheduler.run(() => {
      const expectedFilterValues = { a:transactionMock};
      const expectedMarble = '(a|)';
      scheduler.expectObservable(service.changeEnumSet('10', {})).toBe(expectedMarble, expectedFilterValues);
    })
  })

  it('should create EnumSet To PlatformType Relation', () => {
    scheduler.run(() => {
      let relation: relation = {
        typeName: "Interface Platform Type Enumeration Set",
        sideA:'10'
      }
      const expectedFilterValues = { a:relation};
      const expectedMarble = '(a|)';
      scheduler.expectObservable(service.createEnumSetToPlatformTypeRelation('10')).toBe(expectedMarble, expectedFilterValues);
    })
  })

  it('should create PlatformType To EnumSet Relation', () => {
    scheduler.run(() => {
      let relation: relation = {
        typeName: "Interface Platform Type Enumeration Set",
        sideB:'10'
      }
      const expectedFilterValues = { a:relation};
      const expectedMarble = '(a|)';
      scheduler.expectObservable(service.createPlatformTypeToEnumSetRelation('10')).toBe(expectedMarble, expectedFilterValues);
    })
  })

  it('should create Enum To EnumSet Relation', () => {
    scheduler.run(() => {
      let relation: relation = {
        typeName: "Interface Enumeration Definition",
        sideA:'10'
      }
      const expectedFilterValues = { a:relation};
      const expectedMarble = '(a|)';
      scheduler.expectObservable(service.createEnumToEnumSetRelation('10')).toBe(expectedMarble, expectedFilterValues);
    })
  })

  it('should fetch an array of enumsets', () => {
    service.getEnumSets('10').subscribe();
    const req = httpTestingController.expectOne(apiURL+"/mim/branch/"+10+"/enumerations/");
    expect(req.request.method).toEqual('GET');
    req.flush({});
    httpTestingController.verify();
  })

  it('should get a single enum set', () => {
    service.getEnumSet('10', '20').subscribe();
    const req = httpTestingController.expectOne(apiURL + "/mim/branch/" + 10 + "/types/" + 20 + "/enumeration");
    expect(req.request.method).toEqual('GET');
    req.flush({});
    httpTestingController.verify();
  })

  it('should perform a mutation', () => {
    service.performMutation({ branch: '10', txComment: '' }).subscribe();
    const req = httpTestingController.expectOne(apiURL+'/orcs/txs');
    expect(req.request.method).toEqual('POST');
    req.flush({});
    httpTestingController.verify();
  })
});
