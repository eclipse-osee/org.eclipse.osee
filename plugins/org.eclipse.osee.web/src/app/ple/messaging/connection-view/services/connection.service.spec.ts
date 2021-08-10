import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { TestScheduler } from 'rxjs/testing';
import { transaction } from 'src/app/transactions/transaction';
import { transactionMock } from 'src/app/transactions/transaction.mock';
import { apiURL } from 'src/environments/environment';
import { transportType } from '../../shared/types/connection';

import { ConnectionService } from './connection.service';

describe('ConnectionService', () => {
  let service: ConnectionService;
  let scheduler: TestScheduler;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports:[HttpClientTestingModule]
    });
    service = TestBed.inject(ConnectionService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
  beforeEach(() => scheduler = new TestScheduler((actual, expected) => {
    expect(actual).toEqual(expected);
  }));

  describe('Core Functionality', () => {

    describe('Modifying data', () => {
      
      describe('should create a valid node relation', () => {
        it('should create a secondary node relation', () => {
          scheduler.run(() => {
            let relation = {
              typeName: 'Interface Connection Secondary Node',
              sideB: '10',
              sideA:undefined
            }
            const expectedObservable = { a: relation }
            const expectedMarble = '(a|)'
            scheduler.expectObservable(service.createNodeRelation('10', true)).toBe(expectedMarble, expectedObservable);
          })
        })

        it('should create a primary node relation', () => {
          scheduler.run(() => {
            let relation = {
              typeName: 'Interface Connection Primary Node',
              sideB: '10',
              sideA:undefined
            }
            const expectedObservable = { a: relation }
            const expectedMarble = '(a|)'
            scheduler.expectObservable(service.createNodeRelation('10', false)).toBe(expectedMarble, expectedObservable);
          })
        })
      })

      it('should create a valid connection', () => {
        scheduler.run(() => {
          let extransaction: transaction = {
            branch: '10',
            txComment: "Create Connection and Relate to Node(s): " +"Hello"+ " , "+"Hello",
            createArtifacts: [{
              typeId: '126164394421696910',
              name: 'connection',
              applicabilityId: undefined,
              attributes: [{typeName:"Interface Transport Type",value:transportType.Ethernet}],
              relations:[{typeName:'blah',sideB:'Hello'},{typeName:'blah',sideB:'Hello'}]
            }]
          }
          const expectedfilterValues = { a: extransaction };
          const expectedMarble = '(a|)'
          scheduler.expectObservable(service.createConnection('10',{name:'connection',transportType:transportType.Ethernet},[{typeName:'blah',sideB:'Hello'},{typeName:'blah',sideB:'Hello'}])).toBe(expectedMarble, expectedfilterValues)
        })
      })

      it('should create a valid connection change', () => {
        scheduler.run(() => {
          let extransaction: transaction = {
            branch: '10',
            txComment: "Change connection attributes",
            modifyArtifacts: [{
              id: '1',
              applicabilityId:undefined,
              setAttributes:[{typeName:'Interface Transport Type',value:transportType.Ethernet}]
            }]
          }
          const expectedfilterValues = { a: extransaction };
          const expectedMarble = '(a|)'
          scheduler.expectObservable(service.changeConnection('10',{id:'1',name:'connection',transportType:transportType.Ethernet})).toBe(expectedMarble, expectedfilterValues)
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

    it('should create a delete relation transaction', () => {
      scheduler.run(() => {
        let relation = {
          typeName: 'Interface Connection Secondary Node',
          sideB: '10',
          sideA:undefined
        }
        let transaction = transactionMock;
        transaction.txComment = "Relating Element";
        transaction.deleteRelations = [{ typeName: 'Interface Connection Secondary Node', typeId: undefined, aArtId: undefined, bArtId: '10', rationale: undefined }];
        const expectedObservable = { a: transaction }
        const expectedMarble = '(a|)'
        scheduler.expectObservable(service.deleteRelation('10',relation)).toBe(expectedMarble, expectedObservable);
      })
    })
  })
});
