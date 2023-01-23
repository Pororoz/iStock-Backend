INSERT INTO role (id, name)
SELECT * FROM (SELECT 1, "admin") AS tmp
WHERE NOT EXISTS(SELECT * FROM role WHERE name="admin");

INSERT INTO role (id, name)
SELECT * FROM (SELECT 2, "user") AS tmp
WHERE NOT EXISTS(SELECT * FROM role WHERE name="user");