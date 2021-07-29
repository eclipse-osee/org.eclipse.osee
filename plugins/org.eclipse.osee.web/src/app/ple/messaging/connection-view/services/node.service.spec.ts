import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { apiURL } from 'src/environments/environment';

import { NodeService } from './node.service';

describe('NodeService', () => {
  let service: NodeService;
  let httpTestingController:HttpTestingController

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports:[HttpClientTestingModule]
    });
    service = TestBed.inject(NodeService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('Core Functionality', () => {

    describe('Fetching data', () => {

      it('should get all nodes', () => {
        service.getNodes('10').subscribe();
        const req = httpTestingController.expectOne(apiURL + '/mim/branch/' + 10 + '/nodes/');
        expect(req.request.method).toEqual('GET');
        req.flush([]);
        httpTestingController.verify();
      })

      it('should get a node', () => {
        service.getNode('10', '10').subscribe();
        const req = httpTestingController.expectOne(apiURL + '/mim/branch/' + 10 + '/nodes/' + 10);
        expect(req.request.method).toEqual('GET');
        req.flush({});
        httpTestingController.verify();
      })
    })
    describe('Adding data', () => {
      
      it('should add a node', () => {
        service.createNode('10', {id:'',name:'',description:''}).subscribe();
        const req = httpTestingController.expectOne(apiURL + '/mim/branch/' + 10 + '/nodes/');
        expect(req.request.method).toEqual('POST');
        req.flush({});
        httpTestingController.verify();
      })
    })

    describe('Modifying data', () => {
      
      it('should replace node', () => {
        service.replaceNode('10', {id:'',name:'',description:''}).subscribe();
        const req = httpTestingController.expectOne(apiURL + '/mim/branch/' + 10 + '/nodes/');
        expect(req.request.method).toEqual('PUT');
        req.flush({});
        httpTestingController.verify();
      })

      it('should update node', () => {
        service.patchNode('10', {}).subscribe();
        const req = httpTestingController.expectOne(apiURL + '/mim/branch/' + 10 + '/nodes/');
        expect(req.request.method).toEqual('PATCH');
        req.flush({});
        httpTestingController.verify();
      })
    })

    describe('Removing data', () => {
      it('should delete a node', () => {
        service.deleteNode('10', '10').subscribe();
        const req = httpTestingController.expectOne(apiURL + '/mim/branch/' + 10 + '/nodes/' + 10);
        expect(req.request.method).toEqual('DELETE');
        req.flush({});
        httpTestingController.verify();
      })
    })
  });
});
