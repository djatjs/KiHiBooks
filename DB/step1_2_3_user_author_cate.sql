
INSERT INTO USER (UR_NUM, UR_EMAIL, UR_PW, UR_NICKNAME, UR_AUTHORITY, UR_GENDER, UR_YEAR) VALUES
(1, 'pub1@example.com', 'pw1', '출판사1', 'PUBLISHER', 'M', '1985'),
(2, 'pub2@example.com', 'pw2', '출판사2', 'PUBLISHER', 'F', '1988'),
(3, 'pub3@example.com', 'pw3', '출판사3', 'PUBLISHER', 'M', '1990'),
(4, 'pub4@example.com', 'pw4', '출판사4', 'PUBLISHER', 'F', '1992'),
(5, 'pub5@example.com', 'pw5', '출판사5', 'PUBLISHER', 'M', '1987'),
(6, 'user1@example.com', '123', 'user1', 'USER', 'F', '1995'),
(7, 'user2@example.com', '123', 'user2', 'USER', 'M', '1998'),
(8, 'user3@example.com', '123', 'user3', 'USER', 'F', '1999'),
(9, 'user4@example.com', '123', 'user4', 'USER', 'M', '2007'),
(10, 'user5@example.com', '123', 'user5', 'USER', 'F', '2010'),
(11, 'user6@example.com', '123', 'user6', 'USER', 'M', '2001');

INSERT INTO PUBLISHER (PU_CODE, PU_NAME) VALUES
('P001', '은하출판사'),
('P002', '달빛출판사'),
('P003', '별무리'),
('P004', '청연서재'),
('P005', '무림문화사');

INSERT INTO PUBLISHER_ID (PI_NUM, PI_AUTHORITY, PI_PU_CODE, PI_UR_NUM) VALUES
(1, 'SUPER', 'P001', 1),
(2, 'SUPER', 'P002', 2),
(3, 'SUPER', 'P003', 3),
(4, 'EDITOR', 'P004', 4),
(5, 'SUPER', 'P005', 5);

-- 2. AUTHOR
DELETE FROM AUTHOR;

INSERT INTO AUTHOR (AU_NUM, AU_NAME, AU_PROFILE) VALUES
(1, '홍길동', 'profile1.png'),
(2, '이서연', 'profile2.png'),
(3, '김영훈', 'profile3.png'),
(4, '박지민', 'profile4.png'),
(5, '최민수', 'profile5.png'),
(6, '윤하늘', 'profile6.png'),
(7, '정유진', 'profile7.png'),
(8, '한지후', 'profile8.png'),
(9, '서지우', 'profile9.png'),
(10, '강도윤', 'profile10.png');

INSERT INTO MAIN_CATE (MC_CODE, MC_NAME) VALUES
('1','로맨스'), ('2', '로판'), ('3', '판타지'), ('4', '무협');

INSERT INTO SUB_CATE VALUES
(11, '현대물', 1),
(12, '역사 현대물', 1),
(21, '동양풍', 2),
(22, '서양풍', 2),
(23, '가상세계', 2),
(31, '정통 판타지', 3),
(32, '퓨전 판타지', 3),
(33, '현대 판타지', 3),
(34, '게임 판타지', 3),
(35, '대체 역사', 3),
(36, '스포츠', 3),
(41, '정통 무협', 4),
(42, '신무협', 4);
