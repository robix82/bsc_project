
DELETE FROM role;
INSERT INTO role(role_id, role) VALUES(1, 'ADMIN');
INSERT INTO role(role_id, role) VALUES(2, 'EXPERIMENTER');
INSERT INTO role(role_id, role) VALUES(3, 'PARTICIPANT');

INSERT IGNORE INTO user (user_id, active, password, user_name)
              VALUES (1, 0x01, '$2a$10$I3s4v7IQX1LIgiuPnjRi6ug/aah0prMFmE2R2m9QHct4.b9OLx5Xq', 'admin');

INSERT IGNORE INTO user_role (user_id, role_id) 
              VALUES (1, 1);

INSERT IGNORE INTO administrator (user_id) VALUES (1);



/*

DELETE FROM user WHERE user_name='admin';

INSERT INTO user (active, password, user_name)
              VALUES (0x01, '$2a$10$I3s4v7IQX1LIgiuPnjRi6ug/aah0prMFmE2R2m9QHct4.b9OLx5Xq', 'admin');

DELETE FROM user_role WHERE user_id=(SELECT user_id FROM user WHERE user_name='admin');

INSERT INTO user_role (user_id, role_id) 
              VALUES (SELECT user_id FROM user WHERE user_name='admin'), 1);

DELETE FROM administrator WHERE user_id=(SELECT user_id FROM user WHERE user_name='admin');
INSERT INTO administrator (user_id) VALUES (SELECT user_id FROM user WHERE user_name='admin');

*/

