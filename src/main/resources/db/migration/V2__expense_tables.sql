	create table expense (
        expense_id bigint not null auto_increment,
        created_on datetime(6) not null,
        description varchar(255),
        expense_category tinyint not null check ((expense_category between 0 and 6)),
        expense_date datetime(6),
        modified_on datetime(6) not null,
        payment_status tinyint not null check ((payment_status between 0 and 2)),
        total_amount float(53) not null,
        org_id bigint not null,
        primary key (expense_id)
    ) engine=InnoDB;

    alter table expense 
       add constraint FKq4cboo3f49vq0mffwa9xfb3hh 
       foreign key (org_id) 
       references organization (org_id);
