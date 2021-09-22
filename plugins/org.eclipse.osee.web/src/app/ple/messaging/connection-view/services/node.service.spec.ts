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
import { TransactionBuilderService } from 'src/app/transactions/transaction-builder.service';
import { transactionBuilderMock } from 'src/app/transactions/transaction-builder.service.mock';
import { transactionMock } from 'src/app/transactions/transaction.mock';
import { apiURL } from 'src/environments/environment';

import { NodeService } from './node.service';

describe('NodeService', () => {
  let service: NodeService;
  let scheduler: TestScheduler;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers:[{provide:TransactionBuilderService,useValue:transactionBuilderMock}],
      imports:[HttpClientTestingModule]
    });
    service = TestBed.inject(NodeService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });
  beforeEach(() => scheduler = new TestScheduler((actual, expected) => {
    expect(actual).toEqual(expected);
  }));

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('Core Functionality', () => {

    describe('Fetching data', () => {

      it('should get all nodes', () => {
        service.getNodes('10').subscribe();
        const req = httpTestingController.expectOne(apiURL + '/mim/branch/' + 10 + '/nodes/');
        expect(req.request.method).toEqual('GET');
        req.flush([]);
        httpTestingController.verify();
      })

      it('should get a node', () => {
        service.getNode('10', '10').subscribe();
        const req = httpTestingController.expectOne(apiURL + '/mim/branch/' + 10 + '/nodes/' + 10);
        expect(req.request.method).toEqual('GET');
        req.flush({});
        httpTestingController.verify();
      })
    })
    describe('Adding data', () => {
      
      it('should add a node', () => {
        scheduler.run(() => {
          const expectedfilterValues = { a: transactionMock };
          const expectedMarble = '(a|)'
          scheduler.expectObservable(service.createNode('10', {id:'',name:'',description:''})).toBe(expectedMarble, expectedfilterValues)
        })
      })
    })

    describe('Modifying data', () => {
      
      it('should create a transaction to change a node', () => {
        scheduler.run(() => {
          const expectedfilterValues = { a: transactionMock };
          const expectedMarble = '(a|)'
          scheduler.expectObservable(service.changeNode('10', {id:'',name:'',description:''})).toBe(expectedMarble, expectedfilterValues)
        })
      })

      it('should create a transaction to delete a node', () => {
        scheduler.run(() => {
          const expectedfilterValues = { a: transactionMock };
          const expectedMarble = '(a|)'
          scheduler.expectObservable(service.deleteArtifact('10','15')).toBe(expectedMarble, expectedfilterValues)
        })
      })

      it('should perform a mutation', () => {
        service.performMutation('10', { branch: '10', txComment: '' }).subscribe();
        const req = httpTestingController.expectOne(apiURL+'/orcs/txs');
        expect(req.request.method).toEqual('POST');
        req.flush({});
        httpTestingController.verify();
      })
    })
  });
});
