INSERT INTO members(registered, deleted, nickname, email, profile_url, privacy_policy, follow_strategy)
VALUES (true, false, '김태훈', 'first@test.com', 'https://avatars.githubusercontent.com/u/67636607?s=80&u=66f65ed6f693c3235b3ba0a4a1ed93b7eeae50cb&v=4', 'PUBLIC', 'EAGER'),
       (true, false, '이정우', 'another@test.org', 'https://avatars.githubusercontent.com/u/49686619?v=4', 'PUBLIC', 'EAGER'),
       (true, false, '남궁수', 'suu@test.org', 'https://avatars.githubusercontent.com/u/52947668?v=4', 'PRIVATE', 'LAZY');

INSERT INTO images(url, used)
VALUES ('https://avatars.githubusercontent.com/u/87960006?v=4', true),
       ('https://avatars.githubusercontent.com/u/67636607?s=80&u=66f65ed6f693c3235b3ba0a4a1ed93b7eeae50cb&v=4', true),
       ('https://avatars.githubusercontent.com/u/49686619?v=4', true),
        ('https://avatars.githubusercontent.com/u/52947668?v=4', true);

INSERT INTO machines(name)
VALUES ('덤벨 벤치 프레스'),
       ('체스트 프레스 머신'),
       ('인버티드 로우'),
       ('하이 로우 머신');

INSERT INTO machine_bodyparts(machine_id, body_part)
VALUES (1, 'CHEST'),
       (2, 'CHEST'),
       (3, 'BACK'),
       (4, 'BACK');
