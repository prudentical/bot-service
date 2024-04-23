CREATE TABLE public.bots (
    id bigserial PRIMARY KEY,
    account_id bigint NOT NULL,
    exchange_id bigint NOT NULL,
    title character varying(255) NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    updated_at timestamp(6) without time zone NULL,
    capital numeric(38, 2) NOT NULL,
    stop_loss numeric(38, 2) NULL,
    take_profit numeric(38, 2) NULL,
    type character varying(50) NOT NULL
);

CREATE TABLE public.spot_grid_bots (
    id bigint PRIMARY KEY,
    ceiling numeric(38, 2) NOT NULL,
    floor numeric(38, 2) NOT NULL,
    grids integer NOT NULL,
    pair_id bigint NOT NULL,
   CONSTRAINT fk_spot_grid_bot_parent_id
      FOREIGN KEY(id) 
	  REFERENCES bots(id)
);