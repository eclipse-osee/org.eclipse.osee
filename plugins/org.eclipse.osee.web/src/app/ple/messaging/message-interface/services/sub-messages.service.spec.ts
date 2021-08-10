import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { TestScheduler } from 'rxjs/testing';
import { relation } from 'src/app/transactions/transaction';
import { TransactionBuilderService } from 'src/app/transactions/transaction-builder.service';
import { transactionBuilderMock } from 'src/app/transactions/transaction-builder.service.mock';
import { transactionMock } from 'src/app/transactions/transaction.mock';
import { apiURL } from 'src/environments/environment';

import { SubMessagesService } from './sub-messages.service';

describe('SubMessagesService', () => {
  let service: SubMessagesService;
  let scheduler: TestScheduler;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers:[{provide:TransactionBuilderService,useValue:transactionBuilderMock}],
      imports:[HttpClientTestingModule]
    });
    service = TestBed.inject(SubMessagesService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  beforeEach(() => scheduler = new TestScheduler((actual, expected) => {
    expect(actual).toEqual(expected);
  }));

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should create a message relation', () => {
    scheduler.run(() => {
      let relation: relation = {
        typeName: 'Interface Message SubMessage Content',
        sideA: '10',
        sideB:'10'
      }
      let expectedObservable = { a: relation }
      let expectedMarble = '(a|)';
      scheduler.expectObservable(service.createMessageRelation('10','10')).toBe(expectedMarble, expectedObservable);
    })
  })

  it('should create a submessage creation transaction', () => {
    scheduler.run(() => {
      let expectedObservable = { a: transactionMock };
      let expectedMarble = '(a|)';
      scheduler.expectObservable(service.createSubMessage('10', {}, [])).toBe(expectedMarble, expectedObservable);
    })
  })

  it('should create a submessage change transaction', () => {
    scheduler.run(() => {
      let expectedObservable = { a: transactionMock };
      let expectedMarble = '(a|)';
      scheduler.expectObservable(service.changeSubMessage('10', {})).toBe(expectedMarble, expectedObservable);
    })
  })

  it('should create a relation', () => {
    scheduler.run(() => {
      let expectedObservable = { a: transactionMock };
      let expectedMarble = '(a|)';
      scheduler.expectObservable(service.addRelation('10', { typeName: 'Interface Message SubMessage Content', sideA: '10' })).toBe(expectedMarble, expectedObservable);
    })
  })

  it('should perform a mutation on the sub message endpoint', () => {
    service.performMutation('10', '10', '10', transactionMock).subscribe();
    const req = httpTestingController.expectOne(apiURL+'/orcs/txs');
    expect(req.request.method).toEqual('POST');
    req.flush({});
    httpTestingController.verify();
  })

});
