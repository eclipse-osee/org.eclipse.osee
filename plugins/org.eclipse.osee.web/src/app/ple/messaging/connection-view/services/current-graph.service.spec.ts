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
import { TestBed } from '@angular/core/testing';
import { iif, of } from 'rxjs';
import { TestScheduler } from 'rxjs/testing';
import { userDataAccountServiceMock } from 'src/app/ple/plconfig/testing/mockUserDataAccountService';
import { UserDataAccountService } from 'src/app/userdata/services/user-data-account.service';
import { applicabilityListServiceMock } from '../../shared/mocks/ApplicabilityListService.mock';
import { MimPreferencesMock } from '../../shared/mocks/MimPreferences.mock';
import { MimPreferencesServiceMock } from '../../shared/mocks/MimPreferencesService.mock';
import { ApplicabilityListService } from '../../shared/services/http/applicability-list.service';
import { MimPreferencesService } from '../../shared/services/http/mim-preferences.service';
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
      providers: [
        { provide: GraphService, useValue: graphService },
        { provide: NodeService, useValue: nodeService },
        { provide: ConnectionService, useValue: connectionService },
        { provide: ApplicabilityListService, useValue: applicabilityListServiceMock },
        { provide: MimPreferencesService, useValue: MimPreferencesServiceMock },
        { provide: UserDataAccountService, useValue: userDataAccountServiceMock }
      ]
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

  it('should fetch preferences', () => {
    scheduler.run(() => {
      const expectedFilterValues = { a: MimPreferencesMock };
      const expectedMarble = 'a';
      routeState.branchId = '10'
      scheduler.expectObservable(service.preferences).toBe(expectedMarble, expectedFilterValues);
    })
  })

  it('should fetch applicabilities', () => {
    scheduler.run(() => {
      const expectedFilterValues = { a: [{id:'1',name:'Base'},{id:'2',name:'Second'}] };
      const expectedMarble = 'a';
      routeState.branchId = '10'
      scheduler.expectObservable(service.applic).toBe(expectedMarble, expectedFilterValues);
    })
  })

  it('should update user preferences', () => {
    scheduler.run(() => {
      routeState.branchId='10'
      let expectedObservable = { a: response };
      let expectedMarble = '(a|)';
      scheduler.expectObservable(service.updatePreferences({branchId:'10',allowedHeaders1:['hello','hello3'],allowedHeaders2:['hello2','hello3'],allHeaders1:['hello'],allHeaders2:['hello2'],editable:true,headers1Label:'',headers2Label:'',headersTableActive:false})).toBe(expectedMarble, expectedObservable);
    })
  })
});
