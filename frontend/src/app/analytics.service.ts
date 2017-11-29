import { Injectable } from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {Response, Headers, Http, ResponseContentType} from "@angular/http";

@Injectable()
export class AnalyticsService {
  prefix ='/api';  
  constructor(private http:Http) { }
  getReports():Observable<Response> {
    return this.http.get(this.prefix+'/reports/');
  }
  addReport( name:string, dbUsername:string, dbPassword:string, query:string, type:string, createdBy:string ):Observable<Response> {
    return this.http.post(this.prefix+'/reports/create',{name:name, dbUsername:dbUsername, dbPassword:dbPassword, queryString: query , label:'', type: type, createdBy:createdBy});
  }

  deleteReport ( reportId: number) {
    return this.http.delete(this.prefix+'/reports/delete/'+reportId);
  }

  getReportColumns(reportId:number):Observable<Response> {
    return this.http.get(this.prefix+'/columns/'+reportId);
  }
  getHistory(reportId:string):Observable<Response> {
    return this.http.get(this.prefix+'/history/'+reportId);
  }

  addReportColumn( name:string, reportId:number, label:string, type:string, format:string ):Observable<Response> {
    return this.http.post(this.prefix+'/columns/create',{name:name, reportId:reportId, label:label, type:type, format: format});
  }

  deleteReportColumn ( columnId: number, reportId: number) {
    return this.http.delete(this.prefix+'/columns/delete/'+columnId+'/'+reportId);
  }

  getDatabases():Observable<Response> {
    return this.http.get(this.prefix+'/ds/');
  }
  addDatabase( name:string, description:string, url:string ):Observable<Response> {
    return this.http.post(this.prefix+'/ds/create',{name:name, description:description, url:url});
  }

  deleteDatabase ( dsId: number) {
    return this.http.delete(this.prefix+'/ds/delete/'+dsId);
  }


}
