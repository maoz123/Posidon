create table if not exists posidon_segment(
	service_name varchar(64) NOT NULL default '',

    max_id int,

    step int,

    description varchar(256),

    update_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    primary key(service_name)
  ) ENGINE = InnoDB
END