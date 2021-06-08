import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { SharedMessagingModule } from 'src/app/ple/messaging/shared/shared-messaging.module';

import { EditElementFieldComponent } from './edit-element-field.component';

describe('EditElementFieldComponent', () => {
  let component: EditElementFieldComponent;
  let fixture: ComponentFixture<EditElementFieldComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports:[HttpClientTestingModule,NoopAnimationsModule,FormsModule,MatFormFieldModule,MatInputModule,MatSelectModule,SharedMessagingModule],
      declarations: [ EditElementFieldComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EditElementFieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
