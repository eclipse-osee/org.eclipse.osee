import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OseeStringUtilsModule } from './osee-string-utils/osee-string-utils.module';



@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    OseeStringUtilsModule
  ],
  exports:[OseeStringUtilsModule]
})
export class OseeUtilsModule { }
