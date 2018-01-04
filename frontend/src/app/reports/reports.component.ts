import { Component, OnInit } from '@angular/core';
import {DataSource} from '@angular/cdk/collections';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/observable/of';
import {AnalyticsService} from '../analytics.service';
import {AddReportComponent} from '../add-report/add-report.component';
import {MatDialog, MatDialogRef, MatSnackBar, MAT_DIALOG_DATA} from '@angular/material';

@Component({
  selector: 'app-reports',
  templateUrl: './reports.component.html',
  styleUrls: ['./reports.component.css'],
  providers: [AnalyticsService]
})
export class ReportsComponent implements OnInit {
  public datasource: DataSource<Report> = new ReportsDataSource() ;
  public displayedColumns = [ 'name', 'query', 'type', 'createdBy', 'actions'];
  public loading = true;
  constructor(private analyticsService: AnalyticsService, public dialog: MatDialog, public snackBar: MatSnackBar) { }

  ngOnInit() {
    this.analyticsService.getReports().subscribe(data => {
        reports = data;
        this.datasource = new ReportsDataSource();
        this.loading = false;
    });
  }

  delete(reportId: string): void {
      this.loading = true;
      this.analyticsService.deleteReport(+reportId).subscribe(data => {
        reports = data;
        this.datasource = new ReportsDataSource();
        this.loading = false;
        this.snackBar.open('Report Deleted', null,
        {
          duration: 1000
        });
      });
  }
  runReport(reportId: string): void {
    this.analyticsService.runReport(+reportId)
    .subscribe(result => {
      this.snackBar.open('Report triggered', null,
      {
        duration: 1000
      });
    });
  }

  addEditReport(reportId: string): void {
    let dialogRef;
    if ( reportId) {
       dialogRef = this.dialog.open(AddReportComponent, {
        width: '500px', data: { reportId: reportId}
      });
    } else {
       dialogRef = this.dialog.open(AddReportComponent, {
        width: '500px'
      });
    }


    dialogRef.afterClosed().subscribe(refreshValues => {
      console.log('The dialog was closed ' + refreshValues);
      if ( refreshValues) {
        this.loading = true;
        this.analyticsService.getReports().subscribe(data => {
            reports = data;
            this.datasource = new ReportsDataSource();
            this.loading = false;
        });
      }
    });
  }
}



export interface Report {

  reportId: string;
  name: string;
  dbUsername: string;
  dbPassword: string;
  query: string;
  label: string;
  type: string;
  createdBy: string;

}

let reports: Report[] = [];


/**
 * Data source to provide what data should be rendered in the table. The observable provided
 * in connect should emit exactly the data that should be rendered by the table. If the data is
 * altered, the observable should emit that new set of data on the stream. In our case here,
 * we return a stream that contains only one set of data that doesn't change.
 */
export class ReportsDataSource extends DataSource<any> {
  /*constructor(private siteId:string, private adminService:AdminService){
    super();
  }*/

  /** Connect function called by the table to retrieve one stream containing the data to render. */
  connect(): Observable<Report[]> {
    return Observable.of(reports);
  }

  disconnect() {}
}
