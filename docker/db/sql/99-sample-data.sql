INSERT INTO public."team" (name, input_start_date, alert_start_days) VALUES ('takeda', 20, 25);
INSERT INTO public."team" (name, input_start_date, alert_start_days) VALUES ('nakagawa', 20, 25);
INSERT INTO public."user" (name, team_id) VALUES ('takeda', 1);
INSERT INTO public."user" (name, team_id) VALUES ('nakagawa', 2);
INSERT INTO public."report" (user_id, month, content) VALUES (1, 7,'abcde');
INSERT INTO public."report" (user_id, month, content) VALUES (2, 6,'abcde');
INSERT INTO public."report" (user_id, month, content) VALUES (2, 7,'abcde');