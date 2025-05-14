
    create table artist (
        artist_id int identity not null,
        date_of_birth date not null,
        name varchar(100) not null,
        stage_name varchar(100),
        instagram_profile varchar(255),
        website varchar(255),
        primary key (artist_id)
    );

    create table artist_genre (
        artist_id int not null,
        subgenre varchar(50),
        genre varchar(255) not null,
        primary key (artist_id, genre)
    );

    create table band (
        band_id int identity not null,
        formation_date date,
        band_name varchar(100) not null,
        website varchar(255),
        primary key (band_id)
    );

    create table band_member (
        artist_id int not null,
        band_id int not null,
        join_date date,
        role varchar(50),
        primary key (artist_id, band_id)
    );

    create table event (
        event_date date not null,
        event_id int identity not null,
        festival_id int not null,
        scene_id int not null,
        primary key (event_id)
    );

    create table event_staff (
        event_id int not null,
        scene_id int not null,
        staff_id int not null,
        staff_category varchar(255) not null,
        primary key (event_id, scene_id, staff_id, staff_category)
    );

    create table festival (
        end_date date not null,
        festival_id int identity not null,
        location_id int not null,
        start_date date not null,
        [year] int not null,
        primary key (festival_id)
    );

    create table location (
        latitude numeric(9,6) not null,
        location_id int identity not null,
        longitude numeric(9,6) not null,
        continent varchar(50) not null,
        city varchar(100) not null,
        country varchar(100) not null,
        address varchar(255) not null,
        location_name varchar(255),
        primary key (location_id)
    );

    create table performance (
        artist_id int,
        band_id int,
        break_duration time,
        duration time not null,
        event_id int not null,
        performance_id int identity not null,
        start_time time not null,
        performance_type varchar(50),
        primary key (performance_id)
    );

    create table rating (
        interpretation_score int,
        organization_score int,
        overall_score int,
        performance_id int not null,
        rating_id int identity not null,
        sound_lighting_score int,
        stage_presence_score int,
        ticket_id int not null,
        visitor_id int not null,
        rating_date datetime2(6),
        primary key (rating_id)
    );

    create table resale_queue (
        buyer_id int not null,
        fifo_order int,
        listing_date date not null,
        queue_position int,
        resale_id int identity not null,
        seller_id int not null,
        ticket_id int not null,
        visitor_id int,
        entry_time datetime2(6),
        resale_status varchar(20),
        primary key (resale_id)
    );

    create table scene (
        max_capacity int not null,
        scene_id int identity not null,
        name varchar(100) not null,
        description varchar(max),
        equipment_info varchar(max),
        primary key (scene_id)
    );

    create table staff (
        age int,
        staff_id int identity not null,
        experience_level varchar(20),
        role varchar(50) not null,
        name varchar(100) not null,
        primary key (staff_id)
    );

    create table ticket (
        cost numeric(10,2) not null,
        event_id int not null,
        purchase_date date not null,
        ticket_id int identity not null,
        used bit,
        visitor_id int not null,
        ean bigint,
        payment_method varchar(50),
        ticket_category varchar(50),
        primary key (ticket_id)
    );

    create table visitor (
        age int,
        visitor_id int identity not null,
        first_name varchar(100) not null,
        last_name varchar(100) not null,
        contact varchar(255),
        primary key (visitor_id)
    );

    create table website (
        festival_id int,
        website_id int identity not null,
        image_url varchar(255),
        url varchar(255) not null,
        description varchar(max),
        primary key (website_id)
    );

    alter table ticket 
       add constraint unique_ticket_per_visitor_event unique (event_id, visitor_id);

    alter table artist_genre 
       add constraint FKpwxr33mdh8tx5akxsdsyc5ed7 
       foreign key (artist_id) 
       references artist;

    alter table band_member 
       add constraint FKi1o5s1ec9jvew97sbim9n1mjk 
       foreign key (artist_id) 
       references artist;

    alter table band_member 
       add constraint FK260yqm4dj85eh8d8xsy0pteas 
       foreign key (band_id) 
       references band;

    alter table event 
       add constraint FKn8gfsf6oe8w0j1yfpfj31m3ci 
       foreign key (festival_id) 
       references festival;

    alter table event 
       add constraint FKgi2n460clngyn2q3mxdvenr6m 
       foreign key (scene_id) 
       references scene;

    alter table event_staff 
       add constraint FKckpcnvkvelgbqx4xqk006gxdu 
       foreign key (event_id) 
       references event;

    alter table event_staff 
       add constraint FKgbqkruh6kg54gflx3lguscn0n 
       foreign key (scene_id) 
       references scene;

    alter table event_staff 
       add constraint FKe5fho7rbwi6oqapjc002bwi7r 
       foreign key (staff_id) 
       references staff;

    alter table festival 
       add constraint FKm4d6ffjuteqrbc3vxh06gfsl1 
       foreign key (location_id) 
       references location;

    alter table performance 
       add constraint FKuatgv2wsnmbmb4gesus15x59 
       foreign key (artist_id) 
       references artist;

    alter table performance 
       add constraint FKhknnm5rxqo4e75ydrbfusamv4 
       foreign key (band_id) 
       references band;

    alter table performance 
       add constraint FKgus6nu3sl41ckxchxpr49yxyv 
       foreign key (event_id) 
       references event;

    alter table rating 
       add constraint FKgx891616xsrrjwhet1ywdc1fo 
       foreign key (performance_id) 
       references performance;

    alter table rating 
       add constraint FK5cvopymdsfldp4sqdhf17auc3 
       foreign key (ticket_id) 
       references ticket;

    alter table rating 
       add constraint FKep5ketupqkv10lh5ns9gqecrs 
       foreign key (visitor_id) 
       references visitor;

    alter table resale_queue 
       add constraint FKabwwr79a6c0fsugf7vhjmckgo 
       foreign key (buyer_id) 
       references visitor;

    alter table resale_queue 
       add constraint FKe7do27q8iogqsnfjlpdy246cm 
       foreign key (seller_id) 
       references visitor;

    alter table resale_queue 
       add constraint FKh3a30mk27r9itsov86w1k3gww 
       foreign key (ticket_id) 
       references ticket;

    alter table resale_queue 
       add constraint FKfho8et6so6slrg4736vmibm77 
       foreign key (visitor_id) 
       references visitor;

    alter table ticket 
       add constraint FKfytuhjopeamxbt1cpudy92x5n 
       foreign key (event_id) 
       references event;

    alter table ticket 
       add constraint FK37ftit4hrpnv9hb1mvl6pir6l 
       foreign key (visitor_id) 
       references visitor;

    alter table website 
       add constraint FK1p3fo5ns0p8ntpnnrba576r8e 
       foreign key (festival_id) 
       references festival;
