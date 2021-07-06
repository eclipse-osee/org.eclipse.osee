import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { apiURL } from 'src/environments/environment';
import { ConvertMessageTableTitlesToStringPipe } from '../../../pipes/convert-message-table-titles-to-string.pipe';
import { UiService } from '../../../services/ui.service';

import { EditMessageFieldComponent } from './edit-message-field.component';

describe('EditMessageFieldComponent', () => {
  let component: EditMessageFieldComponent;
  let fixture: ComponentFixture<EditMessageFieldComponent>;
  let httpTestingController: HttpTestingController;
  let uiService: UiService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports:[HttpClientTestingModule,FormsModule,MatFormFieldModule,MatInputModule,NoopAnimationsModule],
      declarations: [ EditMessageFieldComponent, ConvertMessageTableTitlesToStringPipe ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    httpTestingController = TestBed.inject(HttpTestingController);
    uiService = TestBed.inject(UiService);
    fixture = TestBed.createComponent(EditMessageFieldComponent);
    component = fixture.componentInstance;
    component.header='description'
    component.value='v1'
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should update value', fakeAsync(() => {
    uiService.BranchIdString='8'
    component.updateMessage('description', 'v2');
    tick(500);
    const req = httpTestingController.expectOne(apiURL + "/mim/branch/" + '8' + "/messages");
    expect(req.request.method).toEqual('PATCH');
  }));
});
