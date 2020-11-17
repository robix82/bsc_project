
DELETE FROM role;
INSERT INTO role(role_id, role) VALUES(1, 'ADMIN');
INSERT INTO role(role_id, role) VALUES(2, 'EXPERIMENTER');
INSERT INTO role(role_id, role) VALUES(3, 'PARTICIPANT');

INSERT INTO user (user_id, user_name, active, password)
SELECT 888888888, 'admin', 0x01, '$2a$10$rC4TQEA33p4ocjkvwnOYS.sL5TH9QYrTMOIx.A7qYFNtvGrEEF8d2' FROM DUAL
WHERE NOT EXISTS (SELECT * FROM administrator);

INSERT IGNORE INTO administrator (user_id) VALUES (888888888);

INSERT IGNORE INTO user_role (user_id, role_id) VALUES(888888888, 1);
INSERT IGNORE INTO user_role (user_id, role_id) VALUES(888888888, 2);

