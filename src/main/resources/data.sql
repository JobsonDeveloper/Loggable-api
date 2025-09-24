-- MySQL

--INSERT IGNORE INTO tb_roles (role_id, name) VALUES (1, 'admin');
--INSERT IGNORE INTO tb_roles (role_id, name) VALUES (2, 'basic');

-- PostgreSQL

--INSERT INTO tb_roles (role_id, name) VALUES (1, 'admin')
--ON CONFLICT (role_id) DO NOTHING;

INSERT INTO tb_roles (name) VALUES ('BASIC')
ON CONFLICT (name) DO NOTHING;