import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ConvertSubMessageTitlesToStringPipe } from '../../../pipes/convert-sub-message-titles-to-string.pipe';

import { EditSubMessageFieldComponent } from './edit-sub-message-field.component';

describe('EditSubMessageFieldComponent', () => {
  let component: EditSubMessageFieldComponent;
  let fixture: ComponentFixture<EditSubMessageFieldComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports:[FormsModule,MatFormFieldModule,MatInputModule,NoopAnimationsModule,HttpClientTestingModule,],
      declarations: [ EditSubMessageFieldComponent,ConvertSubMessageTitlesToStringPipe ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EditSubMessageFieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
