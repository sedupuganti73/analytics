import { Component, OnInit, Inject } from '@angular/core';
import {MatDialog, MatDialogRef, MAT_DIALOG_DATA, MatDialogTitle} from '@angular/material';
import {AnalyticsService} from '../analytics.service';

@Component({
  selector: 'app-add-database',
  templateUrl: './add-database.component.html',
  providers: [AnalyticsService],
  styleUrls: ['./add-database.component.css']
})
export class AddDatabaseComponent   implements OnInit {
  public name:string;
  public description:string;
  public url:string;
  public dsId:number;
  public title = "Add Database";
  constructor(public dialogRef: MatDialogRef<AddDatabaseComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any, private analyticsService:AnalyticsService) { }

  
    ngOnInit(){
      if(this.data && this.data.dsId) {
        this.dsId = this.data.dsId;
        this.analyticsService.getDatabase(this.data.dsId)
        .subscribe(data=>{
               var dataSource = data.json();
               this.name = dataSource.name;
               this.description = dataSource.description;
               this.url = dataSource.url;
               this.dsId = dataSource.dsId;
               this.title = "Edit Database "+dataSource.name;
        });  
  
      }
    }     

  onNoClick(): void {
    this.dialogRef.close(false);
  }

  addDB():void{
      this.analyticsService.
          addDatabase(this.name, this.description, 
            this.url)
          .subscribe(data=>{
            this.dialogRef.close(true);
          });  

  }

  editDB():void{
    this.analyticsService.
        updateDatabase(this.dsId, this.name, this.description, 
          this.url)
        .subscribe(data=>{
          this.dialogRef.close(true);
        });  

  }
}
