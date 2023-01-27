INSERT INTO role (id, name)
SELECT * FROM (SELECT 1, "ROLE_ADMIN") AS tmp
WHERE NOT EXISTS(SELECT * FROM role WHERE name="ROLE_ADMIN");

INSERT INTO role (id, name)
SELECT * FROM (SELECT 2, "ROLE_USER") AS tmp
WHERE NOT EXISTS(SELECT * FROM role WHERE name="ROLE_USER");