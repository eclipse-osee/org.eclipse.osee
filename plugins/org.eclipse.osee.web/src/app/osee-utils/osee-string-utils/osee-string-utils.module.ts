import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OseeStringUtilsPipesModule } from './osee-string-utils-pipes/osee-string-utils-pipes.module';
import { OseeStringUtilsDirectivesModule } from './osee-string-utils-directives/osee-string-utils-directives.module';



@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    OseeStringUtilsPipesModule,
    OseeStringUtilsDirectivesModule
  ],
  exports: [OseeStringUtilsModule, OseeStringUtilsDirectivesModule]
})
export class OseeStringUtilsModule { }
