import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ConvertMessageTableTitlesToStringPipe } from '../../../pipes/convert-message-table-titles-to-string.pipe';

import { EditMessageFieldComponent } from './edit-message-field.component';

describe('EditMessageFieldComponent', () => {
  let component: EditMessageFieldComponent;
  let fixture: ComponentFixture<EditMessageFieldComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports:[HttpClientTestingModule,FormsModule,MatFormFieldModule,MatInputModule,NoopAnimationsModule],
      declarations: [ EditMessageFieldComponent, ConvertMessageTableTitlesToStringPipe ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EditMessageFieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
