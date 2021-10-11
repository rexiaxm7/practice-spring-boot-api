INSERT INTO public."team" (name, input_start_date, alert_start_days,sending_message_url)
VALUES ('takeda', 20, 25,'');
INSERT INTO public."team" (name, input_start_date, alert_start_days,sending_message_url)
VALUES ('nakagawa', 20, 25,'');
INSERT INTO public."user" (name, team_id) VALUES ('takeda', 1);
INSERT INTO public."user" (name, team_id) VALUES ('nakagawa', 2);
INSERT INTO public."report" (user_id, year, month, content) VALUES (1, 2021, 7, 'abcde');
INSERT INTO public."report" (user_id, year, month, content) VALUES (2, 2021, 6, 'abcde');
INSERT INTO public."report" (user_id, year, month, content) VALUES (2, 2021, 7, 'abcde');