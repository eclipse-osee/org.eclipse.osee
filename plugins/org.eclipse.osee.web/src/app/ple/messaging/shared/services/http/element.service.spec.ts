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
import { elementsMock } from '../../mocks/element.mock';

import { ElementService } from './element.service';

describe('ElementService', () => {
  let service: ElementService;
  let httpTestingController: HttpTestingController;
  let scheduler: TestScheduler;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers:[{provide:TransactionBuilderService,useValue:transactionBuilderMock}],
      imports:[HttpClientTestingModule]
    });
    service = TestBed.inject(ElementService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  beforeEach(() => scheduler = new TestScheduler((actual, expected) => {
    expect(actual).toEqual(expected);
  }));

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return a create transaction for an element w/ end index removed', () => {
    scheduler.run(() => {
      let expectedObservable = { a: transactionMock };
      let expectedMarble = '(a|)';
      scheduler.expectObservable(service.createElement({interfaceElementIndexEnd:0,interfaceElementIndexStart:0},'10', [])).toBe(expectedMarble, expectedObservable);
    })
  })

  it('should return a create transaction for an element w/ start index removed', () => {
    scheduler.run(() => {
      let expectedObservable = { a: transactionMock };
      let expectedMarble = '(a|)';
      scheduler.expectObservable(service.createElement({interfaceElementIndexStart:0},'10', [])).toBe(expectedMarble, expectedObservable);
    })
  })

  it('should return a create transaction for an element w/ no deletions', () => {
    scheduler.run(() => {
      let expectedObservable = { a: transactionMock };
      let expectedMarble = '(a|)';
      scheduler.expectObservable(service.createElement({},'10', [])).toBe(expectedMarble, expectedObservable);
    })
  })
  it('should create a transaction for deleting an element', () => {
    scheduler.run(({ expectObservable }) => {
      let expectedObservable = { a: transactionMock };
      let expectedMarble = '(a|)';
      expectObservable(service.deleteElement('10', '20')).toBe(expectedMarble, expectedObservable);
    })
  })

  it('should create a transaction for an element modification', () => {
    scheduler.run(() => {
      let expectedObservable = { a: transactionMock };
      let expectedMarble = '(a|)';
      scheduler.expectObservable(service.changeElement({},'10')).toBe(expectedMarble, expectedObservable);
    })
  })

  it('should perform a mutation on the element endpoint', () => {
    service.performMutation(transactionMock).subscribe();
    const req = httpTestingController.expectOne(apiURL + "/orcs/txs");
    expect(req.request.method).toEqual('POST');
    req.flush({});
    httpTestingController.verify();
  })

  it('should fetch an element', () => {
    service.getElement('10', '30', '40', '50', '60', '20').subscribe();
    const req = httpTestingController.expectOne(apiURL + "/mim/branch/" + 10 + "/connections/"+20+"/messages/" + 30 + "/submessages/"+ 40+"/structures/"+50+"/elements/"+60);
    expect(req.request.method).toEqual('GET');
    req.flush(elementsMock[0]);
    httpTestingController.verify();
  })

  it('should create a relation to a structure', () => {
    let relation: relation = {
      typeName: "Interface Structure Content",
      sideA: '10',
      sideB:undefined,
      afterArtifact:'end'
    }
    scheduler.run(() => {
      let expectedObservable = { a: relation };
      let expectedMarble = '(a|)';
      scheduler.expectObservable(service.createStructureRelation('10')).toBe(expectedMarble, expectedObservable);
    })
  })

  it('should create a relation to a platform type', () => {
    let relation: relation = {
      typeName: "Interface Element Platform Type",
      sideB: '10',
      sideA:undefined
    }
    scheduler.run(() => {
      let expectedObservable = { a: relation };
      let expectedMarble = '(a|)';
      scheduler.expectObservable(service.createPlatformTypeRelation('10')).toBe(expectedMarble, expectedObservable);
    })
  })

  it('should create an addRelation transaction', () => {
    let relation: relation = {
      typeName: "Interface Element Platform Type",
      sideB: '10',
      sideA:undefined,
      afterArtifact:'end'
    }
    scheduler.run(() => {
      let expectedObservable = { a: transactionMock };
      let expectedMarble = '(a|)';
      scheduler.expectObservable(service.addRelation('10',relation)).toBe(expectedMarble, expectedObservable);
    })
  })

  it('should create a deleteRelation transaction', () => {
    let relation: relation = {
      typeName: "Interface Element Platform Type",
      sideB: '10',
      sideA:undefined,
      afterArtifact:undefined
    }
    scheduler.run(() => {
      let expectedObservable = { a: transactionMock };
      let expectedMarble = '(a|)';
      scheduler.expectObservable(service.deleteRelation('10',relation)).toBe(expectedMarble, expectedObservable);
    })
  })
});
