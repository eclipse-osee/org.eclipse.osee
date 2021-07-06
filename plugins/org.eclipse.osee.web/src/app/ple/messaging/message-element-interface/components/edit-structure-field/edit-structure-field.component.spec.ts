import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { apiURL } from 'src/environments/environment';
import { SharedMessagingModule } from '../../../shared/shared-messaging.module';
import { UiService } from '../../services/ui.service';

import { EditStructureFieldComponent } from './edit-structure-field.component';

describe('EditStructureFieldComponent', () => {
  let component: EditStructureFieldComponent;
  let fixture: ComponentFixture<EditStructureFieldComponent>;
  let httpTestingController: HttpTestingController;
  let uiService:UiService

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports:[HttpClientTestingModule,NoopAnimationsModule,FormsModule,MatFormFieldModule,MatInputModule,MatSelectModule,SharedMessagingModule],
      declarations: [ EditStructureFieldComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    httpTestingController = TestBed.inject(HttpTestingController);
    uiService = TestBed.inject(UiService);
    fixture = TestBed.createComponent(EditStructureFieldComponent);
    component = fixture.componentInstance;
    component.header='description'
    component.value='v1'
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should update value', fakeAsync(() => {
    uiService.BranchIdString = '8';
    uiService.messageIdString = '10';
    uiService.subMessageIdString = '20';
    component.updateStructure('description', 'v2');
    tick(500);
    const req = httpTestingController.expectOne(apiURL + "/mim/branch/" + "8" + "/messages/" + '10' + "/submessages/"+ '20'+"/structures");
    expect(req.request.method).toEqual('PATCH');
  }));
});
