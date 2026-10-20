
    create table customer (
        created_on datetime(6) not null,
        customer_id bigint not null auto_increment,
        modified_on datetime(6) not null,
        org_id bigint not null,
        customer_email varchar(255) not null,
        customer_name varchar(255) not null,
        gst_no varchar(255) not null,
        mobile_no varchar(255) not null,
        primary key (customer_id)
    ) engine=InnoDB;

    create table customer_address (
        customer_id bigint not null,
        city varchar(255) not null,
        country varchar(255),
        district varchar(255) not null,
        pincode varchar(255) not null,
        state varchar(255),
        street varchar(255) not null
    ) engine=InnoDB;

    create table inventory (
        quantity float(53) not null,
        created_on datetime(6) not null,
        inventory_id bigint not null auto_increment,
        modified_on datetime(6) not null,
        org_id bigint not null,
        product_id bigint not null,
        primary key (inventory_id)
    ) engine=InnoDB;

    create table organization (
        created_on datetime(6) not null,
        modified_on datetime(6) not null,
        org_id bigint not null auto_increment,
        mobile_no varchar(255) not null,
        org_email varchar(255) not null,
        org_name varchar(255) not null,
        org_password varchar(255),
        primary key (org_id)
    ) engine=InnoDB;

    create table organization_address (
        org_id bigint not null,
        city varchar(255) not null,
        country varchar(255),
        district varchar(255) not null,
        pincode varchar(255) not null,
        state varchar(255),
        street varchar(255) not null
    ) engine=InnoDB;

    create table product (
        minimum_stock float(53) not null,
        purchase_price float(53) not null,
        selling_price float(53) not null,
        tax_rate float(53) not null,
        created_on datetime(6) not null,
        modified_on datetime(6) not null,
        org_id bigint not null,
        product_id bigint not null auto_increment,
        product_code varchar(255) not null,
        product_name varchar(255) not null,
        unit enum ('GRAMS','KG','LITER','PCS'),
        primary key (product_id)
    ) engine=InnoDB;

    create table purchase (
        subtotal_amount float(53) not null,
        total_amount float(53) not null,
        total_tax_amount float(53) not null,
        created_on datetime(6) not null,
        modified_on datetime(6) not null,
        org_id bigint not null,
        purchase_id bigint not null auto_increment,
        purchased_date datetime(6) not null,
        supplier_id bigint not null,
        purchase_number varchar(255) not null,
        status enum ('CANCELLED','COMPLETED','DRAFT') not null,
        primary key (purchase_id)
    ) engine=InnoDB;

    create table purchase_items (
        discount_amount float(53) not null,
        discount_rate float(53) not null,
        quantity float(53) not null,
        tax_amount float(53) not null,
        tax_rate float(53) not null,
        total_amount float(53) not null,
        unit_price float(53) not null,
        created_on datetime(6) not null,
        id bigint not null auto_increment,
        modified_on datetime(6) not null,
        product_id bigint not null,
        purchase_id bigint not null,
        primary key (id)
    ) engine=InnoDB;

    create table refresh_token (
        created_at datetime(6) not null,
        expires_at datetime(6) not null,
        id bigint not null auto_increment,
        org_id bigint not null,
        revoked_at datetime(6),
        family_id varchar(36) not null,
        ip_address varchar(64),
        replaced_by_hash varchar(64),
        token_hash varchar(64) not null,
        user_agent varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table sale (
        subtotal_amount float(53) not null,
        total_amount float(53) not null,
        total_tax_amount float(53) not null,
        created_on datetime(6) not null,
        customer_id bigint not null,
        modified_on datetime(6) not null,
        org_id bigint not null,
        purchased_date datetime(6) not null,
        sale_id bigint not null auto_increment,
        sales_number varchar(255) not null,
        status enum ('CANCELLED','COMPLETED','DRAFT') not null,
        primary key (sale_id)
    ) engine=InnoDB;

    create table sale_items (
        discount_amount float(53) not null,
        discount_rate float(53) not null,
        quantity float(53) not null,
        tax_amount float(53) not null,
        tax_rate float(53) not null,
        total_amount float(53) not null,
        unit_price float(53) not null,
        created_on datetime(6) not null,
        id bigint not null auto_increment,
        modified_on datetime(6) not null,
        product_id bigint not null,
        sale_id bigint not null,
        primary key (id)
    ) engine=InnoDB;

    create table supplier (
        created_on datetime(6) not null,
        modified_on datetime(6) not null,
        org_id bigint not null,
        supplier_id bigint not null auto_increment,
        gst_no varchar(255) not null,
        mobile_no varchar(255) not null,
        supplier_email varchar(255) not null,
        supplier_name varchar(255) not null,
        primary key (supplier_id)
    ) engine=InnoDB;

    create table supplier_address (
        supplier_id bigint not null,
        city varchar(255) not null,
        country varchar(255),
        district varchar(255) not null,
        pincode varchar(255) not null,
        state varchar(255),
        street varchar(255) not null
    ) engine=InnoDB;

    alter table customer 
       add constraint uk_customer_org_email unique (org_id, customer_email);

    alter table inventory 
       add constraint uk_inventory_org_product unique (org_id, product_id);

    alter table organization 
       add constraint UKi21y6di2u0g0o52qou7s5q2v9 unique (org_email);

    alter table product 
       add constraint uk_product_org_code unique (org_id, product_code);

    alter table product 
       add constraint uk_product_org_name unique (org_id, product_name);

    alter table refresh_token 
       add constraint UKkdj16cltjxdksuyiosdhliveg unique (token_hash);

    alter table supplier 
       add constraint uk_supplier_org_email unique (org_id, supplier_email);

    alter table customer 
       add constraint FKd57apu9ej518vfmoa5u2weeps 
       foreign key (org_id) 
       references organization (org_id);

    alter table customer_address 
       add constraint FKr9ofa0ydsgbaqmt9leb3v5eii 
       foreign key (customer_id) 
       references customer (customer_id);

    alter table inventory 
       add constraint FKann3fl24wsycyrd78xb1bi0cn 
       foreign key (org_id) 
       references organization (org_id);

    alter table inventory 
       add constraint FKp7gj4l80fx8v0uap3b2crjwp5 
       foreign key (product_id) 
       references product (product_id);

    alter table organization_address 
       add constraint FKrlvbk4ee22dwa7u71mibna3g8 
       foreign key (org_id) 
       references organization (org_id);

    alter table product 
       add constraint FKhqqn382r230tee0tut7m86tna 
       foreign key (org_id) 
       references organization (org_id);

    alter table purchase 
       add constraint FK6sb8uwdyqakaxqotid1lsst6 
       foreign key (org_id) 
       references organization (org_id);

    alter table purchase 
       add constraint FK8omm6fki86s9oqk0o9s6w43h5 
       foreign key (supplier_id) 
       references supplier (supplier_id);

    alter table purchase_items 
       add constraint FKoirqxt3dkkcdb8twb4lnpbcyi 
       foreign key (product_id) 
       references product (product_id);

    alter table purchase_items 
       add constraint FKfbch0rs5h1ih9ng5tn5ydi7vm 
       foreign key (purchase_id) 
       references purchase (purchase_id);

    alter table refresh_token 
       add constraint FKl997kllaqhfd6447s82gjcj1g 
       foreign key (org_id) 
       references organization (org_id);

    alter table sale 
       add constraint FKjw88ojfoqquyd9f1obip1ar0g 
       foreign key (customer_id) 
       references customer (customer_id);

    alter table sale 
       add constraint FKpcc641en74edefbx2xdwidhh1 
       foreign key (org_id) 
       references organization (org_id);

    alter table sale_items 
       add constraint FKtblndatsq8mro19rd0i1nx5qw 
       foreign key (product_id) 
       references product (product_id);

    alter table sale_items 
       add constraint FKjnh6jk7q7v40n33h0wto6m4ua 
       foreign key (sale_id) 
       references sale (sale_id);

    alter table supplier 
       add constraint FKjt85d9nv8u3kstjgte6do04uh 
       foreign key (org_id) 
       references organization (org_id);

    alter table supplier_address 
       add constraint FKr9dtkjlmnsmxxnd1hl8r8lpi6 
       foreign key (supplier_id) 
       references supplier (supplier_id);
