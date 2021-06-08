import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { SharedMessagingModule } from '../../../shared/shared-messaging.module';

import { EditStructureFieldComponent } from './edit-structure-field.component';

describe('EditStructureFieldComponent', () => {
  let component: EditStructureFieldComponent;
  let fixture: ComponentFixture<EditStructureFieldComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports:[HttpClientTestingModule,NoopAnimationsModule,FormsModule,MatFormFieldModule,MatInputModule,MatSelectModule,SharedMessagingModule],
      declarations: [ EditStructureFieldComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EditStructureFieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
