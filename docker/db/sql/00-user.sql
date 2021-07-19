CREATE TABLE public.user
(
    id serial NOT NULL,
    name character varying(20) COLLATE pg_catalog."default" NOT NULL,
    age integer NOT NULL
)