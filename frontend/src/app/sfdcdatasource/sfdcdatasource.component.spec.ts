import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SfdcdatasourceComponent } from './sfdcdatasource.component';

describe('SfdcdatasourceComponent', () => {
  let component: SfdcdatasourceComponent;
  let fixture: ComponentFixture<SfdcdatasourceComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SfdcdatasourceComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SfdcdatasourceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
