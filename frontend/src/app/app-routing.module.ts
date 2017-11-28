import { NgModule }             from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
 
import {AppComponent} from './app.component' 
import {ReportsComponent} from './reports/reports.component';
import {ReportColumnsComponent} from './report-columns/report-columns.component';
import {HistoryComponent} from './history/history.component';

const routes: Routes = [
  { path: '', redirectTo: '/reports', pathMatch: 'full' },
  { path: 'reports', component: ReportsComponent },
  { path: 'history/:report-id', component: HistoryComponent},
  { path: 'columns/:report-id', component: ReportColumnsComponent}
];
 
@NgModule({
  imports: [ 
    RouterModule.forRoot(
      routes,
      //{ enableTracing: true } 
    )
  ],
  exports: [ RouterModule ]
})
export class AppRoutingModule {}
