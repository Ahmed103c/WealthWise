import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StatisitqueCategoryComponent } from './statisitque-category.component';

describe('StatisitqueCategoryComponent', () => {
  let component: StatisitqueCategoryComponent;
  let fixture: ComponentFixture<StatisitqueCategoryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StatisitqueCategoryComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StatisitqueCategoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
