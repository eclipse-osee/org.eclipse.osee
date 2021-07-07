import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { ConnectionViewRouterService } from '../../../services/connection-view-router.service';

import { GraphComponent } from './graph.component';

describe('GraphComponent', () => {
  let component: GraphComponent;
  let fixture: ComponentFixture<GraphComponent>;
  let router: any;
  let routerService: ConnectionViewRouterService;

  beforeEach(async () => {
    router = jasmine.createSpyObj('Router', ['navigate', 'createUrlTree', 'serializeUrl'],{'url':new String()});
    await TestBed.configureTestingModule({
      providers:[{ provide: Router, useValue: router }],
      imports:[RouterTestingModule],
      declarations: [ GraphComponent ]
    })
      .compileComponents();
    routerService=TestBed.inject(ConnectionViewRouterService)
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GraphComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('Core Functionality', () => {
    
    describe('View Functionality', () => {

      it('should navigate to messages page', () => {
        routerService.branchType = 'product line';
        routerService.branchId = '8';
        component.navigateToMessages("hello");
        expect(router.navigate).toHaveBeenCalledWith(['', 'product line', '8', 'hello', 'messages'])
      });
      
    })
    
  })
});
