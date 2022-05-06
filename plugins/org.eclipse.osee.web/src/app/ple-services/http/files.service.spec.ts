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
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { TestScheduler } from 'rxjs/testing';
import { HttpMethods } from 'src/app/types/http-methods';
import { apiURL } from 'src/environments/environment';

import { FilesService } from './files.service';

describe('FilesService', () => {
  let service: FilesService;
  let scheduler: TestScheduler;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports:[HttpClientTestingModule]
    });
    service = TestBed.inject(FilesService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  beforeEach(() => scheduler = new TestScheduler((actual, expected) => {
    expect(actual).toEqual(expected);
  }));

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get a file as a blob', () => {
    const file = new Blob();
    service.getFileAsBlob(HttpMethods.GET, 'url', '{}').subscribe();
    const req = httpTestingController.expectOne(apiURL+'url');
    expect(req.request.method).toEqual('GET');
    req.flush(file);
    httpTestingController.verify();
  });

});
