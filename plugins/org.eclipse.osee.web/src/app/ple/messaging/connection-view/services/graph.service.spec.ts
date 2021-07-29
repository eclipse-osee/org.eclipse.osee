import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { apiURL } from 'src/environments/environment';

import { GraphService } from './graph.service';

describe('GraphService', () => {
  let service: GraphService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports:[HttpClientTestingModule]
    });
    service = TestBed.inject(GraphService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should call /mim/branch/10/graph', () => {
    service.getNodes('10').subscribe();
    const req = httpTestingController.expectOne(apiURL + '/mim/branch/10/graph');
    expect(req.request.method).toEqual('GET');
    req.flush({ nodes: [], edges: [] });
    httpTestingController.verify();
  })
});
