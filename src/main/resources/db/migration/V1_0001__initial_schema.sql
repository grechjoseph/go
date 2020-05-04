create schema if not exists go;

create table if not exists go.ip_pool (
    id uuid primary key,
    description text not null,
    lower_bound varchar(25) not null,
    upper_bound varchar(25) not null
);

create table if not exists go.ip_address (
    id uuid primary key,
    ip_pool_id uuid references go.ip_pool(id) not null,
    value varchar(25) not null,
    resource_state varchar(25) not null
);