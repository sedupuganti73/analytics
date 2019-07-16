import { Component, OnInit } from '@angular/core';
import {DataSource} from '@angular/cdk/collections';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/observable/of';
import {AnalyticsService} from '../analytics.service';
import {AddSfdcdatasourceComponent} from '../add-sfdcdatasource/add-sfdcdatasource.component';
import {MatDialog, MatDialogRef, MatSnackBar, MAT_DIALOG_DATA} from '@angular/material';

@Component({
  selector: 'app-sfdcdatasource',
  templateUrl: './sfdcdatasource.component.html',
  styleUrls: ['./sfdcdatasource.component.css'],
   providers: [AnalyticsService]
})
export class SfdcdatasourceComponent implements OnInit {

  public datasource: DataSource<SFDCDataSource> = new DsDataSource() ;
  public displayedColumns = [ 'name', 'description', 'url', 'username', 'password', 'actions'];
  public loading = true;
  constructor(private analyticsService: AnalyticsService, public dialog: MatDialog, public snackBar: MatSnackBar) { }

  ngOnInit() {
    this.analyticsService.getForceDatabases().subscribe(data => {
        dataSources = data;
        this.datasource = new DsDataSource();
        this.loading = false;
    });
  }

  delete(dsId: string): void {
      this.loading = true;
      this.analyticsService.deleteForceDatabase(+dsId).subscribe(data => {
        dataSources = data;
        this.datasource = new DsDataSource();
        this.loading = false;
        this.snackBar.open('Data Source Deleted', null,
        {
          duration: 1000
        });
      });
  }

  addDatabase(dsId: number): void {

    let dialogRef;
    if (dsId) {
       dialogRef = this.dialog.open(AddSfdcdatasourceComponent, {
        width: '500px', data: {dsId: dsId}
      });
    } else {
       dialogRef = this.dialog.open(AddSfdcdatasourceComponent, {
        width: '500px'
      });
    }



    dialogRef.afterClosed().subscribe(refreshValues => {
      console.log('The dialog was closed ' + refreshValues);
      if (refreshValues) {
        this.loading = true;
        this.analyticsService.getForceDatabases().subscribe(data => {
            dataSources = data;
            this.datasource = new DsDataSource();
            this.loading = false;
        });
      }
    });
  }
}



export interface SFDCDataSource {
  dsId: string;
  name: string;
  description: string;
  url: string;
  username: string;
  password: string;
}

let dataSources: SFDCDataSource[] = [];


/**
 * Data source to provide what data should be rendered in the table. The observable provided
 * in connect should emit exactly the data that should be rendered by the table. If the data is
 * altered, the observable should emit that new set of data on the stream. In our case here,
 * we return a stream that contains only one set of data that doesn't change.
 */
export class DsDataSource extends DataSource<any> {
  /*constructor(private siteId:string, private adminService:AdminService){
    super();
  }*/

  /** Connect function called by the table to retrieve one stream containing the data to render. */
  connect(): Observable<SFDCDataSource[]> {
    return Observable.of(dataSources);
  }

  disconnect() {}

}

