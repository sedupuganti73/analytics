import { Component, OnInit, Inject } from '@angular/core';
import {MatDialog, MatDialogRef, MAT_DIALOG_DATA, MatDialogTitle} from '@angular/material';
import {AnalyticsService} from '../analytics.service';

@Component({
  selector: 'app-add-sfdcdatasource',
  templateUrl: './add-sfdcdatasource.component.html',
  providers: [AnalyticsService],
  styleUrls: ['./add-sfdcdatasource.component.css']
})
export class AddSfdcdatasourceComponent implements OnInit {
  public name: string;
  public description: string;
  public url: string;
  public dsId: number;
  public username: string;
  public password: string;

  public title = 'Add Force Database';
  constructor(public dialogRef: MatDialogRef<AddSfdcdatasourceComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any, private analyticsService: AnalyticsService) { }

  ngOnInit() {
   if ( this.data && this.data.dsId) {
        this.dsId = this.data.dsId;
        this.analyticsService.getForceDatabase(this.data.dsId)
        .subscribe(data => {
               const dataSource = data;
               this.name = dataSource.name;
               this.description = dataSource.description;
               this.url = dataSource.url;
               this.dsId = dataSource.dsId;
               this.username = dataSource.username;
               this.password = dataSource.password;
               this.title = 'Edit Force Database ' + dataSource.name;
        });

      }
  }
      
  onNoClick(): void {
    this.dialogRef.close(false);
  }

  addDB(): void {
      this.analyticsService.
          addForceDatabase(this.name, this.description,
            this.url, this.username, this.password)
          .subscribe(data => {
            this.dialogRef.close(true);
          });

  }

  editDB(): void {
    this.analyticsService.
        updateForceDatabase(this.dsId, this.name, this.description,
          this.url , this.username, this.password)
        .subscribe(data => {
          this.dialogRef.close(true);
        });
  }


}
