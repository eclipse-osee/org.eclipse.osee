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
import { structureServiceMock } from '../../../message-element-interface/mocks/services/structure.service.mock';
import { structuresMock } from '../../mocks/structure.mock';
import { StructuresService } from '../http/structures.service';
import { MimRouteService } from './mim-route.service';

import { SharedStructureUIService } from './shared-structure-ui.service';

describe('SharedStructureUIService', () => {
  let service: SharedStructureUIService;
  let uiService: MimRouteService;
  let scheduler: TestScheduler;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers:[{provide:StructuresService,useValue:structureServiceMock}]
    });
    service = TestBed.inject(SharedStructureUIService);
    uiService = TestBed.inject(MimRouteService);
    uiService.idValue = '10';
    uiService.connectionIdString = '20';
  });
  beforeEach(() => scheduler = new TestScheduler((actual, expected) => {
    expect(actual).toEqual(expected);
  }));
  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get the structure', () => {
    scheduler.run(({ expectObservable }) => {
      expectObservable(service.structure).toBe('a',{a:structuresMock[0]})
    })
  })
});
