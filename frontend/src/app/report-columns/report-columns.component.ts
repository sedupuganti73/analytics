import { Component, OnInit } from '@angular/core';
import {DataSource} from '@angular/cdk/collections';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/observable/of';
import {AnalyticsService} from '../analytics.service';
import {AddColumnComponent} from '../add-column/add-column.component';
import {MatDialog, MatDialogRef, MAT_DIALOG_DATA} from '@angular/material';
import { ActivatedRoute, ParamMap } from '@angular/router';

@Component({
  selector: 'app-report-columns',
  templateUrl: './report-columns.component.html',
  styleUrls: ['./report-columns.component.css'],
  providers: [AnalyticsService]
})
export class ReportColumnsComponent implements OnInit {
  public datasource: DataSource<Column> = new ColumnsDataSource() ;
  public displayedColumns = [ 'name', 'label',  'type', 'format', 'actions'];
  public loading = true;
  public reportId: number;
  constructor(private route: ActivatedRoute, private analyticsService: AnalyticsService, public dialog: MatDialog) { }

  ngOnInit() {
    this.reportId = +this.route.snapshot.paramMap.get('report-id');
    this.analyticsService.getReportColumns(this.reportId).subscribe(data => {
        columns = data.json();
        this.datasource = new ColumnsDataSource();
        this.loading = false;
    });
  }

  delete(columnId: string): void {
      this.loading = true;
      this.analyticsService.deleteReportColumn(+columnId, this.reportId).subscribe(data=>{
        columns = data.json();
        this.datasource = new ColumnsDataSource();
        this.loading = false;
      });
  }

  addColumn(): void {
    const dialogRef = this.dialog.open(AddColumnComponent, {
      width: '500px', data: { reportId: this.reportId}
    });

    dialogRef.afterClosed().subscribe(refreshValues => {
      console.log('The dialog was closed ' + refreshValues);
      if ( refreshValues) {
        this.loading = true;
        this.analyticsService.getReportColumns(this.reportId).subscribe(data => {
            columns = data.json();
            this.datasource = new ColumnsDataSource();
            this.loading = false;
        });
      }
    });
  }
}



export interface Column {

  columnId: string,
  reportId: string,
  name: string,
  label: string,
  type: string,
  format: string

}

let columns: Column[] = [];


/**
 * Data source to provide what data should be rendered in the table. The observable provided
 * in connect should emit exactly the data that should be rendered by the table. If the data is
 * altered, the observable should emit that new set of data on the stream. In our case here,
 * we return a stream that contains only one set of data that doesn't change.
 */
export class ColumnsDataSource extends DataSource<any> {
  /*constructor(private siteId:string, private adminService:AdminService){
    super();
  }*/

  /** Connect function called by the table to retrieve one stream containing the data to render. */
  connect(): Observable<Column[]> {
    return Observable.of(columns);
  }

  disconnect() {}
}
