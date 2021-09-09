import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HarnessLoader } from '@angular/cdk/testing';
import { MatCardHarness } from '@angular/material/card/testing';
import { MatButtonHarness } from '@angular/material/button/testing';
import {TestbedHarnessEnvironment} from '@angular/cdk/testing/testbed';
import { MatDialogModule } from '@angular/material/dialog';

import { PlatformTypeCardComponent } from './platform-type-card.component';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { FormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { EditTypeDialogComponent } from '../edit-type-dialog/edit-type-dialog.component';
import { editPlatformTypeDialogDataMode } from '../../types/EditPlatformTypeDialogDataMode.enum';
import { CurrentTypesService } from '../../services/current-types.service';
import { of } from 'rxjs';

let loader: HarnessLoader;

describe('PlatformTypeCardComponent', () => {
  let component: PlatformTypeCardComponent;
  let fixture: ComponentFixture<PlatformTypeCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports:[MatCardModule, MatDialogModule,MatButtonModule, MatFormFieldModule,MatSlideToggleModule,FormsModule,MatInputModule, NoopAnimationsModule],
      declarations: [PlatformTypeCardComponent, EditTypeDialogComponent],
      providers: [{
        provide: CurrentTypesService, useValue: {
          partialUpdate: {
            empty: false,
            errorCount: 0,
            errors: false,
            failed: false,
            ids: ["1"],
            infoCount: false,
            numErrors: 0,
            numErrorsViaSearch: 0,
            numWarnings: 0,
            numWarningsViaSearch: 0,
            results: [],
            success: true,
            tables: [],
            title: "Patching 1",
            txId: "2",
            warningCount:0
          },
          inEditMode:of(true)
      }}
        // {
        //   provide: MatDialog, useValue: {
        //     open() {
        //     },
        //     afterClosed(): Observable<object> {
        //       return of({
        //         data: {
        //           mode: Math.random()<0.5? 'copy':'edit' 
        //         }
        //       })
        //     }
        //   }
        // },
      ],
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PlatformTypeCardComponent);
    loader = TestbedHarnessEnvironment.loader(fixture);
    component = fixture.componentInstance;
    const expectedData={
      id: "0",
      name: "Random Boolean",
      interfaceLogicalType: "Boolean",
      interfacePlatformTypeMinval: "0",
      interfacePlatformTypeMaxval: "1",
      interfacePlatformTypeBitSize: "8",
      interfacePlatformTypeDefaultValue: "0",
      interfacePlatformTypeMsbValue: "0",
      interfacePlatformTypeBitsResolution: "0",
      interfacePlatformTypeCompRate: "0",
      interfacePlatformTypeAnalogAccuracy: "0",
      interfacePlatform2sComplement: false,
      interfacePlatformTypeEnumLiteral: "A string",
      interfacePlatformTypeUnits: "N/A",
      interfacePlatformTypeValidRangeDescription:"N/A"
    }
    component.typeData = expectedData;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should create a header of class mat-card-header-text with text of Random Boolean',async () => {
    fixture.detectChanges();
    const card = await loader.getHarness(MatCardHarness);
    expect(await card.getTitleText()).toEqual("Random Boolean");
  });
  it('should create a subtitle with text of Boolean',async () => {
    fixture.detectChanges();
    const card = await loader.getHarness(MatCardHarness);
    expect(await card.getSubtitleText()).toEqual("Boolean");
  });

  it('should contain text that has minimum value, maximum value, byte size, default value, msb value, resolution, comp rate, analog accuracy, edit and Create New Type From Base',async () => {
    fixture.detectChanges();
    const card = await loader.getHarness(MatCardHarness);
    expect(await card.getText()).toEqual("Random Boolean  Boolean  Minimum Value: 0  Maximum Value: 1  Bit Size: 8  Default Value: 0  MSB Value: 0  Resolution: 0  Comp Rate: 0  Analog Accuracy: 0  Edit  Create New Type From Base");
  });

  it('should call openDialog()', async () => {
    const openDialog = spyOn(component, 'openDialog').and.callThrough();
    const buttons = await (await loader.getHarness(MatCardHarness)).getAllHarnesses(MatButtonHarness);
    buttons.forEach(async (b) => {
      await b.click();
      let text: editPlatformTypeDialogDataMode;
      if (await b.getText() === 'Create New Type From Base') {
        text = editPlatformTypeDialogDataMode.copy;
      } else {
        text = editPlatformTypeDialogDataMode.edit;
      }
      await expect(openDialog).toHaveBeenCalledWith(text);
    });
    expect(component).toBeTruthy();
  })
  //Don't know how to do this test yet
  // it('should open a dialog subscription',async () => {
  //   fixture.detectChanges();
  //   component.openDialog('edit');
  //   component.dialog.
  // });

});
