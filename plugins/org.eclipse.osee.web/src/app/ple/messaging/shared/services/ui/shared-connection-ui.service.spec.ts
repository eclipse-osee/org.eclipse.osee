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
import { connectionMock } from '../../mocks/connection.mock';
import { sharedConnectionServiceMock } from '../../mocks/SharedConnection.service.mock';
import { SharedConnectionService } from '../http/shared-connection.service';
import { MimRouteService } from './mim-route.service';

import { SharedConnectionUIService } from './shared-connection-ui.service';

describe('SharedConnectionUIService', () => {
  let service: SharedConnectionUIService;
  let uiService: MimRouteService;
  let scheduler: TestScheduler;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers:[{provide:SharedConnectionService,useValue:sharedConnectionServiceMock}]
    });
    service = TestBed.inject(SharedConnectionUIService);
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
  it('should get the connection', () => {
    scheduler.run(({ expectObservable }) => {
      expectObservable(service.connection).toBe('a',{a:connectionMock})
    })
  })
});
