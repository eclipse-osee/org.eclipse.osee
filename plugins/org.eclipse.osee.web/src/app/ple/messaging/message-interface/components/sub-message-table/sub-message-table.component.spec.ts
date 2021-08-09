import { HttpClientTestingModule } from '@angular/common/http/testing';
import { SimpleChange } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule } from '@angular/material/dialog';
import { MatMenuModule } from '@angular/material/menu';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { Router, ActivatedRoute, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { OseeStringUtilsDirectivesModule } from 'src/app/osee-utils/osee-string-utils/osee-string-utils-directives/osee-string-utils-directives.module';
import { OseeStringUtilsPipesModule } from 'src/app/osee-utils/osee-string-utils/osee-string-utils-pipes/osee-string-utils-pipes.module';
import { ConvertMessageTableTitlesToStringPipe } from '../../pipes/convert-message-table-titles-to-string.pipe';
import { ConvertSubMessageTitlesToStringPipe } from '../../pipes/convert-sub-message-titles-to-string.pipe';
import { AddSubMessageDialogComponent } from './add-sub-message-dialog/add-sub-message-dialog.component';
import { EditSubMessageFieldComponent } from './edit-sub-message-field/edit-sub-message-field.component';

import { SubMessageTableComponent } from './sub-message-table.component';

describe('SubMessageTableComponent', () => {
  let component: SubMessageTableComponent;
  let fixture: ComponentFixture<SubMessageTableComponent>;
  let router: any;
  let expectedData = [
    {
        name: "Name",
        description: "description adslkfj;asjfadkljf;lajdfla;jsdfdlkasjf;lkajslfjad;ljfkladjsf;",
        interfaceSubMessageNumber: "0",
        interfaceMessageRate:"1Hz"
    },
    {
        name: "Name2",
        description: "description2",
        interfaceSubMessageNumber: "1",
        interfaceMessageRate:"1Hz"
    }
]

  beforeEach(async () => {
    router = jasmine.createSpyObj('Router', ['navigate', 'createUrlTree', 'serializeUrl']);
    await TestBed.configureTestingModule({
      imports:[MatTableModule, MatButtonModule,OseeStringUtilsDirectivesModule,OseeStringUtilsPipesModule, RouterTestingModule, MatMenuModule, MatDialogModule, HttpClientTestingModule],
      declarations: [SubMessageTableComponent, ConvertMessageTableTitlesToStringPipe, ConvertSubMessageTitlesToStringPipe, EditSubMessageFieldComponent, AddSubMessageDialogComponent],
      providers: [{ provide: Router, useValue: router },
        {
          provide: ActivatedRoute, useValue: {
        //     paramMap: of(
        //       convertToParamMap(
        //         {
        //           name:"Name > Name"
        //         }
        //       )
        //     )
            // 
          }  
        },
    ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SubMessageTableComponent);
    component = fixture.componentInstance;
    component.dataSource = new MatTableDataSource();
    component.data = expectedData;
    fixture.detectChanges();
  });

  beforeEach(function () {
    let window1 = spyOn(window, 'open').and.callFake((url,target,replace) => {
      return null;
    })
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should navigate to Hello/10/10/elements', () => {
    component.navigateToElementsTable("10","10",'Hello');
    expect(router.navigate).toHaveBeenCalledWith(['10','10','Hello','elements'],{relativeTo: Object({ }), queryParamsHandling:'merge'});
  });

  it('should update the datasource filter', () => {
    component.filter = "sub message: Name2";
    component.ngOnChanges({
      data: new SimpleChange(component.data, component.data, false),
      filter: new SimpleChange('', component.filter, false)
    })
    expect(component.dataSource.filter === component.filter.replace('sub message: ', ''));
  });

  it('should open the menu', () => {
    
  })
});
