import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AddSfdcdatasourceComponent } from './add-sfdcdatasource.component';

describe('AddSfdcdatasourceComponent', () => {
  let component: AddSfdcdatasourceComponent;
  let fixture: ComponentFixture<AddSfdcdatasourceComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AddSfdcdatasourceComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddSfdcdatasourceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
