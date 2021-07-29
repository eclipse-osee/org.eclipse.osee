import { TestBed } from '@angular/core/testing';
import {  of, } from 'rxjs';
import { filter } from 'rxjs/operators';
import { TestScheduler } from 'rxjs/testing';
import { response } from '../mocks/Response.mock';
import { connection, transportType } from '../types/connection';
import { node } from '../types/node';
import { ConnectionService } from './connection.service';

import { CurrentGraphService } from './current-graph.service';
import { GraphService } from './graph.service';
import { NodeService } from './node.service';
import { RouteStateService } from './route-state-service.service';

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
      return of([])
    },
    patchNode(branchId: string, body: Partial<node>) {
      return of(response);
    },
    deleteNode(branchId: string, nodeId: string) {
      return of(response);
    },
    createNode(branchId: string, body: node) {
      return of(response)
    }
  }
  let connectionService: Partial<ConnectionService> = {
    updateConnection(branchId: string, body: Partial<connection>) {
      return of(response);
    },
    unrelateConnection(branchId: string, nodeId: string, id: string) {
      return of(response);
    },
    createConnection(branchId: string, nodeId: string, id: string) {
      return of(response);
    },
    relateConnection(branchId: string, nodeId: string, id: string) {
      return of(response);
    }
  }
  let routeState: RouteStateService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: GraphService, useValue: graphService },
        { provide: NodeService, useValue: nodeService },
      {provide:ConnectionService,useValue:connectionService}]
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

  it('should return a response when unrelating connection', () => {
    scheduler.run(() => {
      const expectedfilterValues = { a: response };
      const expectedMarble = '(a|)'
      routeState.branchId='10'
      scheduler.expectObservable(service.unrelateConnection('10','10')).toBe(expectedMarble, expectedfilterValues);
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
      const expectedfilterValues = { a: [response,response,response] };
      const expectedMarble = '(a|)'
      routeState.branchId='10'
      scheduler.expectObservable(service.deleteNodeAndUnrelate('10', [{id:'20',source:'15',target:'10'},{id:'20',source:'10',target:'15'}])).toBe(expectedMarble, expectedfilterValues);
    })
  })

  it('should return a response when creating a connection', () => {
    scheduler.run(() => {
      const expectedfilterValues = { a: response };
      const expectedMarble = '(a|)'
      routeState.branchId='10'
      scheduler.expectObservable(service.createNewConnection({name:'connection',transportType:transportType.Ethernet},'10','15')).toBe(expectedMarble, expectedfilterValues);
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

  it('should fetch empty array of nodes', () => {
    scheduler.run(() => {
      const expectedfilterValues = { a: [] };
      const expectedMarble = 'a'
      routeState.branchId='10'
      scheduler.expectObservable(service.nodeOptions).toBe(expectedMarble, expectedfilterValues);
    })
  })
});
