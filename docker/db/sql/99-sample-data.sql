INSERT INTO public."team" (name, input_start_date, alert_start_days, sending_message_url)
VALUES ('takeda', 20, 25, '');
INSERT INTO public."team" (name, input_start_date, alert_start_days, sending_message_url)
VALUES ('nakagawa', 20, 25, '');
INSERT INTO public."user" (name, team_id, email, password,admin)
VALUES ('takeda', 1, 'test3@crossware.co.jp', '{bcrypt}$2a$10$/dFnw0SujLm0yH8.YhmWBOdIv0RXNd70xX3xW9XcRGLgYeCog0Tfa', true);
INSERT INTO public."user" (name, team_id ,email,password,admin)
VALUES ('nakagawa', 2,  'test2@crossware.co.jp', 'test2' , false);
INSERT INTO public."report" (user_id, year, month, content)
VALUES (1, 2021, 7, 'abcde');
INSERT INTO public."report" (user_id, year, month, content)
VALUES (2, 2021, 6, 'abcde');
INSERT INTO public."report" (user_id, year, month, content)
VALUES (2, 2021, 7, 'abcde');