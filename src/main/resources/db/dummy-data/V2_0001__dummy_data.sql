{
		"id": 1,
		"description": "Static - IP Block 1",
		"usedCapacity": 0,
		"lowerBound": "10.70.26.1",
		"upperBound": "10.70.26.100"
	},
	{
		"id": 2,
		"description": "Static - IP Block 2",
		"usedCapacity": 0,
		"lowerBound": "10.70.25.0",
		"upperBound": "10.70.25.255"
	},
	{
		"id": 3,
		"description": "Dynamic - IP Block 3",
		"usedCapacity": 0,
		"lowerBound": "10.50.0.0",
		"upperBound": "10.50.255.255"
	}

create table if not exists go.ip_pool (
    id uuid primary key,
    description text not null,
    lower_bound varchar(25) not null,
    upper_bound varchar(25) not null,
    support_dynamic boolean not null
);

insert into go.ip_pool values ('94bb2d8d-14a7-4d7b-9e89-fe2e448fc9b4', 'Static - IP Block 1', '10.70.26.1', '10.70.26.100', false);
insert into go.ip_pool values ('d9a0d03c-91cb-41fd-b64e-7c2b4dd6cd21', 'Static - IP Block 2', '10.70.26.1', '10.70.26.100', false);
insert into go.ip_pool values ('f75985fb-ba1b-4f05-8689-1d80606f702e', 'Dynamic - IP Block 3', '10.50.0.0', '10.70.26.100', true);