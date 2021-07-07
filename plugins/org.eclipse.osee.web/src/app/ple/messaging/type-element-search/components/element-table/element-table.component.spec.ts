import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatTableModule } from '@angular/material/table';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { of } from 'rxjs';
import { OseeStringUtilsDirectivesModule } from 'src/app/osee-utils/osee-string-utils/osee-string-utils-directives/osee-string-utils-directives.module';
import { OseeStringUtilsPipesModule } from 'src/app/osee-utils/osee-string-utils/osee-string-utils-pipes/osee-string-utils-pipes.module';
import { SharedMessagingModule } from '../../../shared/shared-messaging.module';
import { CurrentElementSearchService } from '../../services/current-element-search.service';
import { ElementTableSearchDummy } from '../../testing/MockComponents/ElementTableSearch';
import { elementSearch3 } from '../../testing/MockResponses/ElementSearch';

import { ElementTableComponent } from './element-table.component';

describe('ElementTableComponent', () => {
  let component: ElementTableComponent;
  let fixture: ComponentFixture<ElementTableComponent>;
  let serviceSpy: jasmine.SpyObj<CurrentElementSearchService>;

  beforeEach(async () => {
    serviceSpy = jasmine.createSpyObj('CurrentElementSearchService', {}, {elements:of(elementSearch3)})
    await TestBed.configureTestingModule({
      imports: [MatTableModule,SharedMessagingModule,OseeStringUtilsPipesModule,
        OseeStringUtilsDirectivesModule, NoopAnimationsModule],
      providers:[{provide:CurrentElementSearchService,useValue:serviceSpy}],
      declarations: [ ElementTableComponent, ElementTableSearchDummy ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ElementTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
