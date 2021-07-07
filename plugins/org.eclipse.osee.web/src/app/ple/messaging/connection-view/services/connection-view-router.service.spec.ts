import { Component } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { ConnectionViewRouterService } from './connection-view-router.service';

describe('ConnectionViewRouterService', () => {
  let service: ConnectionViewRouterService;

  @Component({
    selector: 'dummy',
    template: '<div>Dummy</div>'
  })
  class DummyComponent { };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes(
        [
          { path: '', component: DummyComponent },
          { path: ':branchType', component: DummyComponent },
          { path: ':branchType/:branchId', component: DummyComponent }
        ]
      )]
    });
    service = TestBed.inject(ConnectionViewRouterService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
  describe('Core Functionality', () => {
    describe('Branch Type Navigation', () => {
      it('should utilize a split base url to form a url', () => {
        service.branchType = "product line";
        service.branchType = "working";
        expect(service.type.getValue()).toEqual("working");
      });
    })

    describe('Id Navigation', () => {
      it('should utilize a split base url to form a url', () => {
        service.branchType = "product line";
        service.branchId = "0";
        service.branchId = "8";
        expect(service.id.getValue()).toEqual("8");
      });
    
      it('should not utilize a split base url to form a url', () => {
        service.branchId = "0";
        service.branchId = "8";
        expect(service.id.getValue()).toEqual("8");
      });  
    })
  })
});
