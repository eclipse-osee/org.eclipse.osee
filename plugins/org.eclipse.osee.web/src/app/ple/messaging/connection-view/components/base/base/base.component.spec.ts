import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouteStateService } from '../../../services/route-state-service.service';
import { BranchDummySelector} from '../../../testing/MockComponents/BranchSelector.mock'
import { BranchTypeDummySelector } from '../../../testing/MockComponents/BranchTypeSelector.mock';
import { GraphDummy } from '../../../testing/MockComponents/Graph.mock';
import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';


import { BaseComponent } from './base.component';
import { MatButtonHarness } from '@angular/material/button/testing';
import { EditAuthService } from 'src/app/ple/messaging/shared/services/edit-auth-service.service';
import { editAuthServiceMock } from '../../../mocks/EditAuthService.mock';
import { CurrentGraphService } from '../../../services/current-graph.service';
import { graphServiceMock } from '../../../mocks/CurrentGraphService.mock';
import { of } from 'rxjs';

describe('BaseComponent', () => {
  let component: BaseComponent;
  let routeState: RouteStateService;
  let loader: HarnessLoader;
  let fixture: ComponentFixture<BaseComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MatDialogModule, MatButtonModule, NoopAnimationsModule],
      providers: [
        { provide: EditAuthService, useValue: editAuthServiceMock },
        { provide: CurrentGraphService, useValue: graphServiceMock }
      ],
      declarations: [ BaseComponent, BranchDummySelector, BranchTypeDummySelector, GraphDummy ]
    })
      .compileComponents();
    routeState = TestBed.inject(RouteStateService);
  });

  beforeEach(() => {
    routeState.branchId = '10';
    fixture = TestBed.createComponent(BaseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    loader = TestbedHarnessEnvironment.loader(fixture);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should open settings dialog', async () => {
    let dialogRefSpy = jasmine.createSpyObj({ afterClosed: of({branchId:'10',allowedHeaders1:[],allowedHeaders2:[],allHeaders1:[],allHeaders2:[],editable:true,headers1Label:'',headers2Label:'',headersTableActive:false}), close: null });
    let dialogSpy = spyOn(TestBed.inject(MatDialog), 'open').and.returnValue(dialogRefSpy);
    let spy = spyOn(component, 'openSettingsDialog').and.callThrough();
    (await (await loader.getHarness(MatButtonHarness)).click());
    expect(spy).toHaveBeenCalled();
  })
});
