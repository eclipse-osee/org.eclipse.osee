import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatStepperModule } from '@angular/material/stepper';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { of } from 'rxjs';
import { CurrentTypesService } from '../../services/current-types.service';

import { NewTypeDialogComponent } from './new-type-dialog.component';

describe('NewTypeDialogComponent', () => {
  let component: NewTypeDialogComponent;
  let fixture: ComponentFixture<NewTypeDialogComponent>;
  let typesService: Partial<CurrentTypesService> = {
    logicalTypes:of([{id: '0',
      name: 'name',
      idString: '0',
      idIntValue: 0
    }])
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports:[MatDialogModule, MatStepperModule,NoopAnimationsModule,MatSelectModule,FormsModule,MatFormFieldModule,MatInputModule,MatButtonModule],
      declarations: [NewTypeDialogComponent],
      providers: [{ provide: MatDialogRef, useValue: {} },
      {provide:CurrentTypesService, useValue:typesService}]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NewTypeDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
