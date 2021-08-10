import { TestBed } from '@angular/core/testing';
import { iif, of } from 'rxjs';
import { TestScheduler } from 'rxjs/testing';
import { applicabilityListServiceMock } from '../../shared/mocks/ApplicabilityListService.mock';
import { ApplicabilityListService } from '../../shared/services/http/applicability-list.service';
import { response } from '../mocks/Response.mock';
import { connection, transportType } from '../../shared/types/connection';
import { node } from '../../shared/types/node';
import { ConnectionService } from './connection.service';

import { CurrentGraphService } from './current-graph.service';
import { GraphService } from './graph.service';
import { NodeService } from './node.service';
import { RouteStateService } from './route-state-service.service';
import { transactionMock } from 'src/app/transactions/transaction.mock';
import { relation, transaction } from 'src/app/transactions/transaction';

describe('CurrentGraphService', () => {
  let service: CurrentGraphService;
  let scheduler: TestScheduler;
  let graphService: Partial<GraphService> = {
    getNodes(id:string) {
      return of({nodes:[{id:'1',name:'1'},{id:'2',name:'2'}],edges:[{id:'1234',source:'1',target:'2'}]})
    },
  }
  let nodeService: Partial<NodeService> = {
    getNodes(branchId: string) {
      return of([{id:'1',name:'1'},{id:'2',name:'2'}])
    },
    getNode(branchId: string, nodeId: string) {
      return of({id:'1',name:'1'})
    },
    createNode(branchId: string, body: Partial<node>) {
      return of(transactionMock)
    },
    changeNode(branchId: string, node: Partial<node>) {
      return of(transactionMock)
    },
    performMutation(branchId: string, body: transaction) {
      return of(response);
    },
    deleteArtifact(branchId: string, artId: string) {
      return of(transactionMock);
    }
  }
  let connectionService: Partial<ConnectionService> = {
    createConnection(branchId: string, connection:connection, relations:relation[]) {
      return of(transactionMock);
    },
    createNodeRelation(nodeId: string, type: boolean) {
      return iif(() => type, of({
        typeName: 'Interface Connection Secondary Node',
        sideB: nodeId
      }), of({
        typeName: 'Interface Connection Primary Node',
        sideB: nodeId
      }));
    },
    changeConnection(branchId: string, connection: Partial<connection>) {
      return of(transactionMock);
    },
    performMutation(branchId: string, body: transaction) {
      return of(response);
    },
    deleteRelation(branchId: string, relation: relation, transaction?: transaction) {
      return of(transactionMock);
    }
  }
  let routeState: RouteStateService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: GraphService, useValue: graphService },
        { provide: NodeService, useValue: nodeService },
        { provide: ConnectionService, useValue: connectionService },
      {provide:ApplicabilityListService,useValue:applicabilityListServiceMock}]
    });
    service = TestBed.inject(CurrentGraphService);
    routeState = TestBed.inject(RouteStateService)
  });

  beforeEach(() => scheduler = new TestScheduler((actual, expected) => {
    expect(actual).toEqual(expected);
  }));

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return a response when updating connection', () => {
    scheduler.run(() => {
      const expectedfilterValues = { a: response };
      const expectedMarble = '(a|)'
      routeState.branchId='10'
      scheduler.expectObservable(service.updateConnection({})).toBe(expectedMarble, expectedfilterValues);
    })
  })

  it('should return a response when unrelating connection(source)', () => {
    scheduler.run(() => {
      const expectedfilterValues = { a: response };
      const expectedMarble = '(a|)'
      routeState.branchId='10'
      scheduler.expectObservable(service.unrelateConnection('1','1')).toBe(expectedMarble, expectedfilterValues);
    })
  })

  it('should return a response when unrelating connection(target)', () => {
    scheduler.run(() => {
      const expectedfilterValues = { a: response };
      const expectedMarble = '(a|)'
      routeState.branchId='10'
      scheduler.expectObservable(service.unrelateConnection('2','2')).toBe(expectedMarble, expectedfilterValues);
    })
  })

  it('should return a response when updating a node', () => {
    scheduler.run(() => {
      const expectedfilterValues = { a: response };
      const expectedMarble = '(a|)'
      routeState.branchId='10'
      scheduler.expectObservable(service.updateNode({})).toBe(expectedMarble, expectedfilterValues);
    })
  })

  it('should return a response when deleting a node and unrelating', () => {
    scheduler.run(() => {
      const expectedfilterValues = { a: response };
      const expectedMarble = '(a|)'
      routeState.branchId='10'
      scheduler.expectObservable(service.deleteNodeAndUnrelate('10', [{id:'20',source:'15',target:'10'},{id:'20',source:'10',target:'15'}])).toBe(expectedMarble, expectedfilterValues);
    })
  })

  it('should return a response when creating a connection', () => {
    routeState.branchId = '10';
    scheduler.run(() => {
      const expectedfilterValues = { a: response };
      const expectedMarble = '(a|)'
      routeState.branchId='10'
      scheduler.expectObservable(service.createNewConnection({name:'connection',transportType:transportType.Ethernet},'1','2')).toBe(expectedMarble, expectedfilterValues);
    })
  })

  it('should return a response when creating a connection(and flip the target and source)', () => {
    routeState.branchId = '10';
    scheduler.run(() => {
      const expectedfilterValues = { a: response };
      const expectedMarble = '(a|)'
      routeState.branchId='10'
      scheduler.expectObservable(service.createNewConnection({name:'connection',transportType:transportType.Ethernet},'2','1')).toBe(expectedMarble, expectedfilterValues);
    })
  })

  it('should return a response when creating a node', () => {
    scheduler.run(() => {
      const expectedfilterValues = { a: response };
      const expectedMarble = '(a|)'
      routeState.branchId='10'
      scheduler.expectObservable(service.createNewNode({name:'node'})).toBe(expectedMarble, expectedfilterValues);
    })
  })

  it('should fetch empty array of nodes and edges', () => {
    scheduler.run(() => {
      const expectedfilterValues = { a: {nodes:[{id:'1',name:'1'},{id:'2',name:'2'}],edges:[{id:'a1234',source:'1',target:'2'}]} };
      const expectedMarble = 'a'
      routeState.branchId='10'
      scheduler.expectObservable(service.nodes).toBe(expectedMarble, expectedfilterValues);
    })
  })

  it('should fetch array of nodes', () => {
    scheduler.run(() => {
      const expectedfilterValues = { a: [{id:'1',name:'1'},{id:'2',name:'2'}] };
      const expectedMarble = 'a'
      routeState.branchId='10'
      scheduler.expectObservable(service.nodeOptions).toBe(expectedMarble, expectedfilterValues);
    })
  })
});
