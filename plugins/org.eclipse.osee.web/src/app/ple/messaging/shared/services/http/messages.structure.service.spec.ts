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
import { apiURL } from 'src/environments/environment';
import { messagesMock } from '../../../message-interface/mocks/ReturnObjects/messages.mock';

import { MessagesStructureService } from './messages.structure.service';

describe('MessagesService', () => {
  let service: MessagesStructureService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports:[HttpClientTestingModule]
    });
    service = TestBed.inject(MessagesStructureService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get a sub message', () => {
    service.getSubMessage('10', '20', '30', '1').subscribe();
    const req = httpTestingController.expectOne(apiURL + "/mim/branch/" + 10 + "/connections/"+1+"/messages/" + 20 + "/submessages/" + 30);
    expect(req.request.method).toEqual('GET');
    req.flush(messagesMock[0].subMessages[0]);
    httpTestingController.verify();
  })
});
