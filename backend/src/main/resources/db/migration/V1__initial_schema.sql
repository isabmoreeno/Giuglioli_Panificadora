create table products (
  id bigserial primary key, name varchar(120) not null, category varchar(80) not null,
  unit varchar(20) not null default 'un', price numeric(12,2) not null default 0,
  cost numeric(12,2) not null default 0, active boolean not null default true,
  created_at timestamptz not null default now()
);
create table customers (
  id bigserial primary key, name varchar(160) not null, city varchar(100) not null,
  active boolean not null default true, created_at timestamptz not null default now()
);
create table schedules (
  id bigserial primary key, delivery_date date not null, period varchar(40) not null,
  status varchar(20) not null default 'OPEN', updated_at timestamptz not null default now(),
  unique(delivery_date, period)
);
create table schedule_lines (
  id bigserial primary key, schedule_id bigint not null references schedules(id) on delete cascade,
  customer_id bigint not null references customers(id), product_id bigint not null references products(id),
  quantity integer not null default 0 check(quantity >= 0), unique(schedule_id, customer_id, product_id)
);
create table schedule_audit (
  id bigserial primary key, schedule_id bigint not null references schedules(id) on delete cascade,
  employee_label varchar(120) not null default 'Funcionário', changed_at timestamptz not null default now(),
  summary text not null
);
create table ingredients (
  id bigserial primary key, name varchar(120) not null, unit varchar(20) not null,
  quantity numeric(12,3) not null default 0 check(quantity >= 0), minimum_quantity numeric(12,3) not null default 0,
  active boolean not null default true
);
create table stock_movements (
  id bigserial primary key, ingredient_id bigint not null references ingredients(id),
  movement_type varchar(20) not null check(movement_type in ('IN','OUT','ADJUST')),
  quantity numeric(12,3) not null check(quantity > 0), reason varchar(240) not null,
  created_at timestamptz not null default now()
);
create index idx_schedule_date on schedules(delivery_date);
create index idx_schedule_lines_schedule on schedule_lines(schedule_id);
create index idx_stock_movements_ingredient on stock_movements(ingredient_id, created_at desc);
