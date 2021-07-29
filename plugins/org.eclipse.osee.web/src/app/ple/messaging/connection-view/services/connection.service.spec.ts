import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { apiURL } from 'src/environments/environment';
import { transportType } from '../types/connection';

import { ConnectionService } from './connection.service';

describe('ConnectionService', () => {
  let service: ConnectionService;
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

  describe('Core Functionality', () => {

    describe('Fetching data', () => {

      it('should get all connections', () => {
        service.getConnections('10').subscribe();
        const req = httpTestingController.expectOne(apiURL+'/mim/branch/'+10+'/connections/');
        expect(req.request.method).toEqual('GET');
        req.flush([]);
        httpTestingController.verify();
      })

      it('should get a connection', () => {
        service.getConnection('10','10').subscribe();
        const req = httpTestingController.expectOne(apiURL+'/mim/branch/'+10+'/connections/'+10);
        expect(req.request.method).toEqual('GET');
        req.flush({});
        httpTestingController.verify();
      })
    })
    describe('Adding data', () => {
      
      it('should add a connection', () => {
        service.addConnection('10', {}).subscribe();
        const req = httpTestingController.expectOne(apiURL+'/mim/branch/'+10+'/connections/');
        expect(req.request.method).toEqual('POST');
        req.flush({});
        httpTestingController.verify();
      })

      it('should create a valid connection', () => {
        service.createConnection('10','10','primary', {id:'',name:'',description:'',transportType:transportType.Ethernet}).subscribe();
        const req = httpTestingController.expectOne(apiURL+'/mim/branch/'+10+'/nodes/'+10+'/connections/'+'primary');
        expect(req.request.method).toEqual('POST');
        req.flush({});
        httpTestingController.verify();
      })
    })

    describe('Modifying data', () => {
      
      it('should replace connection', () => {
        service.replaceConnection('10', {}).subscribe();
        const req = httpTestingController.expectOne(apiURL+'/mim/branch/'+10+'/connections/');
        expect(req.request.method).toEqual('PUT');
        req.flush({});
        httpTestingController.verify();
      })

      it('should update connection', () => {
        service.updateConnection('10', {id:'',name:'',description:'',transportType:transportType.Ethernet}).subscribe();
        const req = httpTestingController.expectOne(apiURL+'/mim/branch/'+10+'/connections/');
        expect(req.request.method).toEqual('PATCH');
        req.flush({});
        httpTestingController.verify();
      })
    })

    describe('Removing data', () => {
      it('should delete a connection', () => {
        service.deleteConnection('10','10').subscribe();
        const req = httpTestingController.expectOne(apiURL+'/mim/branch/'+10+'/connections/'+10);
        expect(req.request.method).toEqual('DELETE');
        req.flush({});
        httpTestingController.verify();
      })
    })

    describe('Relationship modification', () => {
      
      it('should relate to a new node', () => {
        service.relateConnection('10','10','primary','10', {id:'',name:'',description:'',transportType:transportType.Ethernet}).subscribe();
        const req = httpTestingController.expectOne(apiURL+'/mim/branch/'+10+'/nodes/'+10+'/connections/'+10+'/primary');
        expect(req.request.method).toEqual('PATCH');
        req.flush({});
        httpTestingController.verify();  
      })

      it('should un-relate from node', () => {
        service.unrelateConnection('10', '10', '10').subscribe();
        const req = httpTestingController.expectOne(apiURL+'/mim/branch/'+10+'/nodes/'+10+'/connections/'+10);
        expect(req.request.method).toEqual('DELETE');
        req.flush({});
        httpTestingController.verify();  
      })
    })
  })
});
