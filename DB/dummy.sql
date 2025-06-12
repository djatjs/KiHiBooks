-- DELETE EXISTING DATA
-- DELETE FROM REVIEW;
-- DELETE FROM book_keyword;
-- DELETE FROM BOOK;
-- DELETE FROM AUTHOR;
-- DELETE FROM PUBLISHER_ID;
-- DELETE FROM PUBLISHER;
-- DELETE FROM user;
SET SQL_SAFE_UPDATES = 0;
-- INSERT USER
INSERT INTO `USER` (
  UR_EMAIL, UR_PW, UR_NICKNAME, UR_AUTHORITY,
  UR_GENDER, UR_YEAR
) VALUES -- 비밀번호 '1111'
('pub1@example.com', '$2a$10$wlSsruxN/AGAH9cy1WXBk.6Lf30SjfWL7NISbMujL.H9W7DnNUgHK', '출판사1', 'PUBLISHER', 'M', '1985'), -- 1
('pub2@example.com', '$2a$10$wlSsruxN/AGAH9cy1WXBk.6Lf30SjfWL7NISbMujL.H9W7DnNUgHK', '출판사2', 'PUBLISHER', 'F', '1988'), -- 2
('pub3@example.com', '$2a$10$wlSsruxN/AGAH9cy1WXBk.6Lf30SjfWL7NISbMujL.H9W7DnNUgHK', '출판사3', 'PUBLISHER', 'M', '1990'), -- 3
('pub4@example.com', '$2a$10$wlSsruxN/AGAH9cy1WXBk.6Lf30SjfWL7NISbMujL.H9W7DnNUgHK', '출판사4', 'PUBLISHER', 'F', '1992'), -- 4
('pub5@example.com', '$2a$10$wlSsruxN/AGAH9cy1WXBk.6Lf30SjfWL7NISbMujL.H9W7DnNUgHK', '출판사5', 'PUBLISHER', 'M', '1987'), -- 5
('user1@example.com', '$2a$10$wlSsruxN/AGAH9cy1WXBk.6Lf30SjfWL7NISbMujL.H9W7DnNUgHK', 'user1', 'USER', 'F', '1995'), -- 6
('user2@example.com', '$2a$10$wlSsruxN/AGAH9cy1WXBk.6Lf30SjfWL7NISbMujL.H9W7DnNUgHK', 'user2', 'USER', 'M', '1998'), -- 7
('user3@example.com', '$2a$10$wlSsruxN/AGAH9cy1WXBk.6Lf30SjfWL7NISbMujL.H9W7DnNUgHK', 'user3', 'USER', 'F', '1999'), -- 8
('user4@example.com', '$2a$10$wlSsruxN/AGAH9cy1WXBk.6Lf30SjfWL7NISbMujL.H9W7DnNUgHK', 'user4', 'USER', 'M', '2007'), -- 9
('user5@example.com', '$2a$10$wlSsruxN/AGAH9cy1WXBk.6Lf30SjfWL7NISbMujL.H9W7DnNUgHK', 'user5', 'USER', 'F', '2010'), -- 10
('user6@example.com', '$2a$10$wlSsruxN/AGAH9cy1WXBk.6Lf30SjfWL7NISbMujL.H9W7DnNUgHK', 'user6', 'USER', 'M', '2001'), -- 11
('admin@admin.com', '$2a$10$wlSsruxN/AGAH9cy1WXBk.6Lf30SjfWL7NISbMujL.H9W7DnNUgHK', '관리자', 'PUBLISHER', 'M', '1985'); -- 12 (관리자)


-- INSERT PUBLISHERS
INSERT INTO PUBLISHER (PU_CODE, PU_NAME) VALUES ('P001', '은하출판사');
INSERT INTO PUBLISHER (PU_CODE, PU_NAME) VALUES ('P002', '달빛출판사');
INSERT INTO PUBLISHER (PU_CODE, PU_NAME) VALUES ('P003', '별무리');
INSERT INTO PUBLISHER (PU_CODE, PU_NAME) VALUES ('P004', '청연서재');
INSERT INTO PUBLISHER (PU_CODE, PU_NAME) VALUES ('P005', '무림문화사');

INSERT INTO PUBLISHER_ID (PI_NUM, PI_AUTHORITY, PI_PU_CODE, PI_UR_NUM) VALUES (1, 'SUPER', 'P001', 1);
INSERT INTO PUBLISHER_ID (PI_NUM, PI_AUTHORITY, PI_PU_CODE, PI_UR_NUM) VALUES (2, 'SUPER', 'P002', 2);
INSERT INTO PUBLISHER_ID (PI_NUM, PI_AUTHORITY, PI_PU_CODE, PI_UR_NUM) VALUES (3, 'SUPER', 'P003', 3);
INSERT INTO PUBLISHER_ID (PI_NUM, PI_AUTHORITY, PI_PU_CODE, PI_UR_NUM) VALUES (4, 'EDITOR', 'P004', 4);
INSERT INTO PUBLISHER_ID (PI_NUM, PI_AUTHORITY, PI_PU_CODE, PI_UR_NUM) VALUES (5, 'SUPER', 'P005', 5);

-- INSERT AUTHORS
INSERT INTO AUTHOR (AU_NUM, AU_NAME, AU_PROFILE) VALUES 
(1, '작가1', 'profile1.png'),
(2, '작가2', 'profile2.png'),
(3, '작가3', 'profile3.png'),
(4, '작가4', 'profile4.png'),
(5, '작가5', 'profile5.png'),
(6, '작가6', 'profile6.png'),
(7, '작가7', 'profile7.png'),
(8, '작가8', 'profile8.png'),
(9, '작가9', 'profile9.png'),
(10, '작가10', 'profile10.png');


-- INSERT CATE
INSERT INTO MAIN_CATE(MC_CODE, MC_NAME)
VALUES('1','로맨스'), (2, '로판'), (3, '판타지'), (4, '무협');

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

-- INSERT BOOKS
INSERT INTO BOOK (BO_CODE, BO_TITLE, BO_DESCRIPTION, BO_ADULT, BO_FIN, BO_SC_CODE, BO_PI_NUM, BO_AU_NUM) 
VALUES 
('B0001', '도시의 연인', '현대물 장르의 도시의 연인는 흥미진진한 스토리를 담고 있습니다.', 'Y', 'N', '11', (SELECT PI_NUM FROM PUBLISHER_ID WHERE PI_PU_CODE = 'P001' LIMIT 1), 10),
('B0002', '사무실 로맨스', '현대물 장르의 사무실 로맨스는 흥미진진한 스토리를 담고 있습니다.', 'Y', 'Y', '11', (SELECT PI_NUM FROM PUBLISHER_ID WHERE PI_PU_CODE = 'P004' LIMIT 1), 9),
('B0003', '왕의 연인', '역사/시대물 장르의 왕의 연인는 흥미진진한 스토리를 담고 있습니다.', 'Y', 'N', '12', (SELECT PI_NUM FROM PUBLISHER_ID WHERE PI_PU_CODE = 'P004' LIMIT 1), 10),
('B0004', '왕의 연인', '역사/시대물 장르의 왕의 연인는 흥미진진한 스토리를 담고 있습니다.', 'Y', 'Y', '12', (SELECT PI_NUM FROM PUBLISHER_ID WHERE PI_PU_CODE = 'P004' LIMIT 1), 6),
('B0005', '화산의 검', '동양풍 장르의 화산의 검는 흥미진진한 스토리를 담고 있습니다.', 'N', 'N', '21', (SELECT PI_NUM FROM PUBLISHER_ID WHERE PI_PU_CODE = 'P002' LIMIT 1), 3),
('B0006', '드래곤 마스터', '서양풍 장르의 드래곤 마스터는 흥미진진한 스토리를 담고 있습니다.', 'N', 'N', '22', (SELECT PI_NUM FROM PUBLISHER_ID WHERE PI_PU_CODE = 'P004' LIMIT 1), 7),
('B0007', '이세계 셰프', '가상 세계 장르의 이세계 셰프는 흥미진진한 스토리를 담고 있습니다.', 'Y', 'N', '23', (SELECT PI_NUM FROM PUBLISHER_ID WHERE PI_PU_CODE = 'P001' LIMIT 1), 6),
('B0008', '가상도시', '가상 세계 장르의 가상도시는 흥미진진한 스토리를 담고 있습니다.', 'Y', 'Y', '23', (SELECT PI_NUM FROM PUBLISHER_ID WHERE PI_PU_CODE = 'P002' LIMIT 1), 6),
('B0009', '왕국의 후계자', '정통 장르의 왕국의 후계자는 흥미진진한 스토리를 담고 있습니다.', 'N', 'Y', '31', (SELECT PI_NUM FROM PUBLISHER_ID WHERE PI_PU_CODE = 'P001' LIMIT 1), 1),
('B0010', '마법전쟁 연대기', '정통 장르의 마법전쟁 연대기는 흥미진진한 스토리를 담고 있습니다.', 'N', 'N', '31', (SELECT PI_NUM FROM PUBLISHER_ID WHERE PI_PU_CODE = 'P002' LIMIT 1), 6),
('B0011', '헌터의 제국', '퓨전 장르의 헌터의 제국는 흥미진진한 스토리를 담고 있습니다.', 'N', 'Y', '32', (SELECT PI_NUM FROM PUBLISHER_ID WHERE PI_PU_CODE = 'P002' LIMIT 1), 4),
('B0012', '강남마법사', '퓨전 장르의 강남마법사는 흥미진진한 스토리를 담고 있습니다.', 'Y', 'Y', '32', (SELECT PI_NUM FROM PUBLISHER_ID WHERE PI_PU_CODE = 'P001' LIMIT 1), 6),
('B0013', '리셋 라이프', '현대 장르의 리셋 라이프는 흥미진진한 스토리를 담고 있습니다.', 'Y', 'Y', '33', (SELECT PI_NUM FROM PUBLISHER_ID WHERE PI_PU_CODE = 'P001' LIMIT 1), 1),
('B0014', '공략불가 NPC', '게임 장르의 공략불가 NPC는 흥미진진한 스토리를 담고 있습니다.', 'N', 'Y', '34', (SELECT PI_NUM FROM PUBLISHER_ID WHERE PI_PU_CODE = 'P005' LIMIT 1), 5),
('B0015', '변경된 연대기', '대체 역사 장르의 변경된 연대기는 흥미진진한 스토리를 담고 있습니다.', 'Y', 'N', '35', (SELECT PI_NUM FROM PUBLISHER_ID WHERE PI_PU_CODE = 'P004' LIMIT 1), 2),
('B0016', '변경된 연대기', '대체 역사 장르의 변경된 연대기는 흥미진진한 스토리를 담고 있습니다.', 'Y', 'N', '35', (SELECT PI_NUM FROM PUBLISHER_ID WHERE PI_PU_CODE = 'P001' LIMIT 1), 9),
('B0017', '리바운드', '스포츠 장르의 리바운드는 흥미진진한 스토리를 담고 있습니다.', 'Y', 'Y', '36', (SELECT PI_NUM FROM PUBLISHER_ID WHERE PI_PU_CODE = 'P003' LIMIT 1), 9),
('B0018', '혈검강호', '정통무협 장르의 혈검강호는 흥미진진한 스토리를 담고 있습니다.', 'Y', 'Y', '41', (SELECT PI_NUM FROM PUBLISHER_ID WHERE PI_PU_CODE = 'P005' LIMIT 1), 8),
('B0019', '무림 리스타트', '신무협 장르의 무림 리스타트는 흥미진진한 스토리를 담고 있습니다.', 'Y', 'Y', '42', (SELECT PI_NUM FROM PUBLISHER_ID WHERE PI_PU_CODE = 'P001' LIMIT 1), 2);


-- INSERT EPISODE
INSERT INTO `EPISODE` (EP_CODE, EP_TITLE, EP_FILE_NAME, EP_COVER_IMG, EP_TOTAL_PAGE, EP_PRICE, EP_BO_CODE) VALUES
('B0001001', '도시의 연인 1화', 'FILENAME1', 'FILEIMG1', '100', 0, 'B0001'),
('B0001002', '도시의 연인 2화', 'FILENAME1', 'FILEIMG2', '100', 0, 'B0001'),
('B0001003', '도시의 연인 3화', 'FILENAME1', 'FILEIMG3', '100', 0, 'B0001'),
('B0001004', '도시의 연인 4화', 'FILENAME1', 'FILEIMG4', '100', 0, 'B0001'),
('B0001005', '도시의 연인 5화', 'FILENAME1', 'FILEIMG5', '100', 0, 'B0001'),
('B0001006', '도시의 연인 6화', 'FILENAME1', 'FILEIMG6', '100', 0, 'B0001'),
('B0001007', '도시의 연인 7화', 'FILENAME1', 'FILEIMG7', '100', 0, 'B0001'),
('B0001008', '도시의 연인 8화', 'FILENAME1', 'FILEIMG8', '100', 0, 'B0001'),
('B0001009', '도시의 연인 9화', 'FILENAME1', 'FILEIMG9', '100', 0, 'B0001'),
('B0001010', '도시의 연인 10화', 'FILENAME1', 'FILEIMG10', '100', 0, 'B0001'),
('B0001011', '도시의 연인 11화', 'FILENAME1', 'FILEIMG11', '100', 100, 'B0001'),
('B0001012', '도시의 연인 12화', 'FILENAME1', 'FILEIMG12', '100', 100, 'B0001'),
('B0001013', '도시의 연인 13화', 'FILENAME1', 'FILEIMG13', '100', 100, 'B0001'),
('B0001014', '도시의 연인 14화', 'FILENAME1', 'FILEIMG14', '100', 100, 'B0001'),
('B0001015', '도시의 연인 15화', 'FILENAME1', 'FILEIMG15', '100', 100, 'B0001'),
('B0001016', '도시의 연인 16화', 'FILENAME1', 'FILEIMG16', '100', 100, 'B0001'),
('B0001017', '도시의 연인 17화', 'FILENAME1', 'FILEIMG17', '100', 100, 'B0001'),
('B0001018', '도시의 연인 18화', 'FILENAME1', 'FILEIMG18', '100', 100, 'B0001'),
('B0001019', '도시의 연인 19화', 'FILENAME1', 'FILEIMG19', '100', 100, 'B0001'),
('B0001020', '도시의 연인 20화', 'FILENAME1', 'FILEIMG20', '100', 100, 'B0001'),
('B0001021', '도시의 연인 21화', 'FILENAME1', 'FILEIMG21', '100', 100, 'B0001'),
('B0001022', '도시의 연인 22화', 'FILENAME1', 'FILEIMG22', '100', 100, 'B0001'),
('B0001023', '도시의 연인 23화', 'FILENAME1', 'FILEIMG23', '100', 100, 'B0001'),
('B0001024', '도시의 연인 24화', 'FILENAME1', 'FILEIMG24', '100', 100, 'B0001'),
('B0001025', '도시의 연인 25화', 'FILENAME1', 'FILEIMG25', '100', 100, 'B0001');

-- INSERT REVIEWS
INSERT INTO `REVIEW` (RV_RATING, RV_SPOILER, RV_CONTENT, RV_DATE, RV_BO_CODE, RV_UR_NUM) VALUES
(2, 'N', '정말 재미있게 봤어요!', '2025-05-20 14:23:45', 'B0003', 3),
(4, 'Y', '결말이 충격적이네요.', '2025-05-19 10:15:32', 'B0007', 7),
(6, 'N', '보통이었어요.', '2025-05-18 08:45:10', 'B0012', 5),
(8, 'Y', '스포일러가 있지만 추천합니다.', '2025-05-17 19:30:00', 'B0001', 1),
(10, 'N', '최고예요! 꼭 보세요!', '2025-05-16 22:00:05', 'B0015', 11),

(2, 'Y', '중간에 반전이 있네요.', '2025-05-15 13:12:01', 'B0009', 4),
(4, 'N', '기대만큼은 아니었어요.', '2025-05-14 16:40:55', 'B0005', 6),
(6, 'Y', '내용이 좀 어렵네요.', '2025-05-13 11:20:20', 'B0002', 2),
(8, 'N', '좋은 작품입니다.', '2025-05-12 09:05:30', 'B0010', 8),
(10, 'Y', '마지막 장면이 인상적이에요.', '2025-05-11 21:45:10', 'B0004', 9),

(2, 'N', '그저 그랬어요.', '2025-05-10 07:30:15', 'B0008', 10),
(4, 'Y', '내용이 복잡하지만 재미있어요.', '2025-05-09 15:50:25', 'B0013', 11),
(6, 'N', '중간 부분이 지루했어요.', '2025-05-08 14:00:00', 'B0006', 1),
(8, 'Y', '다시 보고 싶네요.', '2025-05-07 20:10:40', 'B0014', 3),
(10, 'N', '완벽한 스토리라인!', '2025-05-06 18:25:55', 'B0003', 7),

(2, 'Y', '스포일러 주의하세요.', '2025-05-05 12:05:00', 'B0007', 2),
(4, 'N', '특별한 점은 없었어요.', '2025-05-04 17:45:30', 'B0011', 5),
(6, 'Y', '생각보다 재미있었어요.', '2025-05-03 10:10:10', 'B0001', 4),
(8, 'N', '배우들의 연기가 좋네요.', '2025-05-02 19:00:00', 'B0012', 6),
(10, 'Y', '감동적인 이야기였어요.', '2025-05-01 22:15:45', 'B0015', 9),

(4, 'N', '스토리가 조금 산만했어요.', '2025-05-21 11:10:00', 'B0004', 2),
(10, 'Y', '감동이 밀려왔어요.', '2025-05-21 13:45:30', 'B0010', 5),
(6, 'N', '특별한 건 없지만 볼만했어요.', '2025-05-21 15:20:15', 'B0008', 9),
(8, 'Y', '중반부가 재미있었어요.', '2025-05-21 18:05:55', 'B0006', 1),
(2, 'N', '기대 이하였어요.', '2025-05-22 09:30:40', 'B0011', 3),

(10, 'N', '최고의 작품입니다!', '2025-05-22 11:25:20', 'B0015', 11),
(4, 'Y', '복선이 많아서 흥미로웠어요.', '2025-05-22 14:00:00', 'B0002', 4),
(8, 'N', '배우 연기가 훌륭했어요.', '2025-05-22 17:40:10', 'B0005', 7),
(6, 'Y', '스토리가 조금 아쉬웠어요.', '2025-05-22 20:15:50', 'B0009', 8),
(2, 'N', '다음 편이 기대됩니다.', '2025-05-23 08:00:00', 'B0013', 6),

(8, 'Y', '긴장감이 넘쳤어요.', '2025-05-23 10:30:30', 'B0014', 10),
(4, 'N', '그냥 그랬어요.', '2025-05-23 12:55:45', 'B0001', 1),
(6, 'Y', '재미있었지만 중간에 좀 지루했어요.', '2025-05-23 15:10:10', 'B0003', 5),
(10, 'N', '완성도가 높네요.', '2025-05-23 18:25:35', 'B0007', 9),
(2, 'Y', '스포일러 포함, 반전이 놀라웠어요.', '2025-05-23 20:40:00', 'B0012', 2),

(8, 'N', '음악이 인상적이었어요.', '2025-05-24 09:15:25', 'B0006', 7),
(6, 'Y', '조금 복잡한 내용이에요.', '2025-05-24 11:50:40', 'B0008', 3),
(4, 'N', '기대만큼은 아니었어요.', '2025-05-24 14:05:55', 'B0011', 11),
(10, 'Y', '정말 감명 깊었어요.', '2025-05-24 17:20:20', 'B0004', 6),
(2, 'N', '조금 아쉬운 점이 있었어요.', '2025-05-24 19:35:45', 'B0002', 4),

(6, 'N', '전반적으로 무난했어요.', '2025-05-25 10:05:10', 'B0005', 8),
(8, 'Y', '결말이 의외였어요.', '2025-05-25 12:40:30', 'B0010', 3),
(4, 'N', '좀 더 흥미로웠으면 좋겠어요.', '2025-05-25 15:15:50', 'B0013', 7),
(10, 'Y', '대단한 연출이었어요.', '2025-05-25 18:50:05', 'B0001', 1),
(2, 'N', '기대 이하입니다.', '2025-05-25 20:25:20', 'B0009', 9),

(8, 'Y', '중반이 특히 좋았어요.', '2025-05-26 09:30:45', 'B0007', 2),
(6, 'N', '스토리가 탄탄했어요.', '2025-05-26 11:55:15', 'B0014', 4),
(4, 'Y', '아쉬움이 남네요.', '2025-05-26 14:20:40', 'B0003', 6),
(10, 'N', '모든 면에서 만족합니다.', '2025-05-26 17:45:55', 'B0015', 11),
(2, 'Y', '다소 지루했어요.', '2025-05-26 19:10:10', 'B0006', 5),

(8, 'N', '배경음악이 좋았어요.', '2025-05-27 08:15:25', 'B0002', 7),
(6, 'Y', '복잡하지만 재밌었어요.', '2025-05-27 10:40:40', 'B0008', 1),
(4, 'N', '기대 이하였어요.', '2025-05-27 13:05:55', 'B0011', 3),
(10, 'Y', '강력 추천합니다!', '2025-05-27 16:30:10', 'B0004', 9),
(2, 'N', '스포일러 없이 봤어요.', '2025-05-27 18:55:25', 'B0001', 2),

(6, 'Y', '긴장감 넘치는 전개.', '2025-05-28 09:20:40', 'B0005', 6),
(8, 'N', '배우들의 연기가 최고였어요.', '2025-05-28 11:45:55', 'B0012', 10),
(4, 'Y', '내용이 복잡했어요.', '2025-05-28 14:10:10', 'B0007', 4),
(10, 'N', '마음에 쏙 들었어요.', '2025-05-28 16:35:25', 'B0010', 8),
(2, 'Y', '조금 아쉬운 점이 있었네요.', '2025-05-28 19:00:40', 'B0003', 11),

(4, 'N', '생각보다 괜찮았어요.', '2025-05-29 09:15:10', 'B0011', 1),
(8, 'Y', '스토리가 흥미진진했어요.', '2025-05-29 11:40:25', 'B0006', 5),
(6, 'N', '무난하게 봤어요.', '2025-05-29 14:05:30', 'B0009', 3),
(10, 'Y', '감동적인 결말이었어요.', '2025-05-29 16:30:45', 'B0002', 9),
(2, 'N', '좀 별로였어요.', '2025-05-29 18:55:50', 'B0015', 7),

(8, 'N', '배우 연기가 훌륭했어요.', '2025-05-30 08:10:05', 'B0001', 2),
(6, 'Y', '다시 보고 싶어요.', '2025-05-30 10:35:20', 'B0013', 4),
(4, 'N', '특별한 점은 없었어요.', '2025-05-30 13:00:35', 'B0007', 6),
(10, 'Y', '최고의 작품입니다.', '2025-05-30 15:25:40', 'B0010', 8),
(2, 'N', '기대 이하였어요.', '2025-05-30 17:50:55', 'B0003', 11),

(6, 'Y', '중반부가 재미있었어요.', '2025-05-31 09:05:10', 'B0014', 1),
(8, 'N', '스토리가 탄탄했어요.', '2025-05-31 11:30:25', 'B0004', 3),
(4, 'Y', '조금 지루했어요.', '2025-05-31 13:55:30', 'B0008', 5),
(10, 'N', '강력 추천합니다.', '2025-05-31 16:20:45', 'B0005', 7),
(2, 'Y', '스포일러가 있지만 재미있어요.', '2025-05-31 18:45:50', 'B0012', 9),

(8, 'N', '배경음악이 좋았어요.', '2025-06-01 08:10:05', 'B0006', 2),
(6, 'Y', '내용이 조금 복잡했어요.', '2025-06-01 10:35:20', 'B0009', 4),
(4, 'N', '무난했어요.', '2025-06-01 13:00:35', 'B0011', 6),
(10, 'Y', '마음에 쏙 들었어요.', '2025-06-01 15:25:40', 'B0002', 8),
(2, 'N', '다소 아쉬웠어요.', '2025-06-01 17:50:55', 'B0015', 10),

(10, 'N', '완전 만족합니다!', '2025-06-02 09:15:00', 'B0001', 1),
(8, 'Y', '반전이 대단했어요.', '2025-06-02 11:40:10', 'B0003', 2),
(6, 'N', '무난한 작품이에요.', '2025-06-02 14:05:20', 'B0005', 3),
(4, 'Y', '좀 지루한 부분이 있었어요.', '2025-06-02 16:30:30', 'B0007', 4),
(2, 'N', '기대에 못 미쳤어요.', '2025-06-02 18:55:40', 'B0009', 5),

(10, 'Y', '강력 추천합니다!', '2025-06-03 08:10:50', 'B0011', 6),
(8, 'N', '배우 연기가 뛰어났어요.', '2025-06-03 10:35:00', 'B0013', 7),
(6, 'Y', '스토리가 조금 복잡했어요.', '2025-06-03 13:00:10', 'B0015', 8),
(4, 'N', '평범한 작품이에요.', '2025-06-03 15:25:20', 'B0002', 9),
(2, 'Y', '아쉬움이 남네요.', '2025-06-03 17:50:30', 'B0004', 10),

(10, 'N', '정말 감동적이었어요.', '2025-06-04 09:15:40', 'B0006', 11),
(8, 'Y', '긴장감 넘치는 전개.', '2025-06-04 11:40:50', 'B0008', 1),
(6, 'N', '보통이었어요.', '2025-06-04 14:06:00', 'B0010', 2),
(4, 'Y', '조금 지루했어요.', '2025-06-04 16:31:10', 'B0012', 3),
(2, 'N', '별로였어요.', '2025-06-04 18:56:20', 'B0014', 4),

(10, 'Y', '최고의 작품이에요!', '2025-06-05 08:11:30', 'B0001', 5),
(8, 'N', '스토리가 흥미진진했어요.', '2025-06-05 10:36:40', 'B0003', 6),
(6, 'Y', '중간 부분이 재미있었어요.', '2025-06-05 13:01:50', 'B0005', 7),
(4, 'N', '기대보다는 아쉬웠어요.', '2025-06-05 15:27:00', 'B0007', 8),
(2, 'Y', '스포일러 주의가 필요해요.', '2025-06-05 17:52:10', 'B0009', 9),

(2, 'N', '그냥 무난했어요.', '2025-06-06 09:00:00', 'B0001', 1),
(4, 'Y', '중간에 반전이 있었네요.', '2025-06-06 09:15:00', 'B0002', 2),
(6, 'N', '생각보다 괜찮았어요.', '2025-06-06 09:30:00', 'B0003', 3),
(8, 'Y', '긴장감이 최고였어요.', '2025-06-06 09:45:00', 'B0004', 4),
(10, 'N', '감동적인 이야기였습니다.', '2025-06-06 10:00:00', 'B0005', 5),

(2, 'Y', '스포일러가 있으니 조심하세요.', '2025-06-06 10:15:00', 'B0006', 6),
(4, 'N', '그저 그랬어요.', '2025-06-06 10:30:00', 'B0007', 7),
(6, 'Y', '복잡하지만 흥미로웠어요.', '2025-06-06 10:45:00', 'B0008', 8),
(8, 'N', '연기가 훌륭했습니다.', '2025-06-06 11:00:00', 'B0009', 9),
(10, 'Y', '다시 보고 싶어요.', '2025-06-06 11:15:00', 'B0010', 10),

(2, 'N', '기대 이하였어요.', '2025-06-06 11:30:00', 'B0011', 11),
(4, 'Y', '흥미로운 플롯이었어요.', '2025-06-06 11:45:00', 'B0012', 1),
(6, 'N', '보통 수준이에요.', '2025-06-06 12:00:00', 'B0013', 2),
(8, 'Y', '감정선이 잘 표현됐어요.', '2025-06-06 12:15:00', 'B0014', 3),
(10, 'N', '강력 추천합니다!', '2025-06-06 12:30:00', 'B0015', 4),

(2, 'Y', '내용이 어려웠어요.', '2025-06-06 12:45:00', 'B0001', 5),
(4, 'N', '기대보단 별로였어요.', '2025-06-06 13:00:00', 'B0002', 6),
(6, 'Y', '중간 부분이 좋았어요.', '2025-06-06 13:15:00', 'B0003', 7),
(8, 'N', '음악이 인상적이었어요.', '2025-06-06 13:30:00', 'B0004', 8),
(10, 'Y', '마음에 쏙 들었어요.', '2025-06-06 13:45:00', 'B0005', 9),

(2, 'N', '별로였어요.', '2025-06-06 14:00:00', 'B0006', 10),
(4, 'Y', '반전이 재미있었어요.', '2025-06-06 14:15:00', 'B0007', 11),
(6, 'N', '무난했어요.', '2025-06-06 14:30:00', 'B0008', 1),
(8, 'Y', '스토리가 탄탄했어요.', '2025-06-06 14:45:00', 'B0009', 2),
(10, 'N', '최고였습니다.', '2025-06-06 15:00:00', 'B0010', 3),

(2, 'Y', '좀 지루했어요.', '2025-06-06 15:15:00', 'B0011', 4),
(4, 'N', '다시 보고 싶지 않아요.', '2025-06-06 15:30:00', 'B0012', 5),
(6, 'Y', '배우 연기가 좋았어요.', '2025-06-06 15:45:00', 'B0013', 6),
(8, 'N', '스토리가 흥미로웠어요.', '2025-06-06 16:00:00', 'B0014', 7),
(10, 'Y', '감동적이었어요.', '2025-06-06 16:15:00', 'B0015', 8),

(2, 'N', '기대에 못 미쳤어요.', '2025-06-06 16:30:00', 'B0001', 9),
(4, 'Y', '긴장감이 있었어요.', '2025-06-06 16:45:00', 'B0002', 10),
(6, 'N', '무난한 작품이에요.', '2025-06-06 17:00:00', 'B0003', 11),
(8, 'Y', '재미있게 봤어요.', '2025-06-06 17:15:00', 'B0004', 1),
(10, 'N', '완벽했어요.', '2025-06-06 17:30:00', 'B0005', 2),

(2, 'Y', '아쉬운 점이 있었어요.', '2025-06-06 17:45:00', 'B0006', 3),
(4, 'N', '그저 그랬어요.', '2025-06-06 18:00:00', 'B0007', 4),
(6, 'Y', '스토리가 흥미진진했어요.', '2025-06-06 18:15:00', 'B0008', 5),
(8, 'N', '배우들의 연기가 좋았어요.', '2025-06-06 18:30:00', 'B0009', 6),
(10, 'Y', '최고의 작품이에요.', '2025-06-06 18:45:00', 'B0010', 7),

(2, 'N', '다소 실망했어요.', '2025-06-06 19:00:00', 'B0011', 8),
(4, 'Y', '내용이 복잡했어요.', '2025-06-06 19:15:00', 'B0012', 9),
(6, 'N', '평범했어요.', '2025-06-06 19:30:00', 'B0013', 10),
(8, 'Y', '재미있었어요.', '2025-06-06 19:45:00', 'B0014', 11),
(10, 'N', '감동받았어요.', '2025-06-06 20:00:00', 'B0015', 1),

(8, 'N', '배우들의 연기가 뛰어났어요.', '2025-06-07 09:00:00', 'B0001', 2),
(6, 'Y', '스토리가 흥미로웠어요.', '2025-06-07 09:15:00', 'B0002', 3),
(4, 'N', '무난하게 봤어요.', '2025-06-07 09:30:00', 'B0003', 4),
(10, 'Y', '정말 감동적이었어요.', '2025-06-07 09:45:00', 'B0004', 5),
(2, 'N', '기대보다 별로였어요.', '2025-06-07 10:00:00', 'B0005', 6),

(8, 'Y', '긴장감 넘치는 전개였어요.', '2025-06-07 10:15:00', 'B0006', 7),
(6, 'N', '보통 수준이에요.', '2025-06-07 10:30:00', 'B0007', 8),
(4, 'Y', '좀 지루했어요.', '2025-06-07 10:45:00', 'B0008', 9),
(10, 'N', '최고의 작품이에요!', '2025-06-07 11:00:00', 'B0009', 10),
(2, 'Y', '아쉬움이 많았어요.', '2025-06-07 11:15:00', 'B0010', 11),

(8, 'N', '음악이 정말 좋았어요.', '2025-06-07 11:30:00', 'B0011', 1),
(6, 'Y', '스토리가 조금 복잡했어요.', '2025-06-07 11:45:00', 'B0012', 2),
(4, 'N', '기대 이하였어요.', '2025-06-07 12:00:00', 'B0013', 3),
(10, 'Y', '감동적인 결말이었어요.', '2025-06-07 12:15:00', 'B0014', 4),
(2, 'N', '별로였어요.', '2025-06-07 12:30:00', 'B0015', 5),

(8, 'Y', '중반부가 특히 재미있었어요.', '2025-06-07 12:45:00', 'B0001', 6),
(6, 'N', '배우들의 연기가 최고였어요.', '2025-06-07 13:00:00', 'B0002', 7),
(4, 'Y', '스토리가 산만했어요.', '2025-06-07 13:15:00', 'B0003', 8),
(10, 'N', '마음에 쏙 들었어요.', '2025-06-07 13:30:00', 'B0004', 9),
(2, 'Y', '좀 실망했어요.', '2025-06-07 13:45:00', 'B0005', 10),

(8, 'N', '좋은 작품이었어요.', '2025-06-07 14:00:00', 'B0006', 11),
(6, 'Y', '재미있게 봤어요.', '2025-06-07 14:15:00', 'B0007', 1),
(4, 'N', '기대 이하였어요.', '2025-06-07 14:30:00', 'B0008', 2),
(10, 'Y', '다시 보고 싶어요.', '2025-06-07 14:45:00', 'B0009', 3),
(2, 'N', '별로였어요.', '2025-06-07 15:00:00', 'B0010', 4),

(8, 'Y', '복잡하지만 흥미로웠어요.', '2025-06-07 15:15:00', 'B0011', 5),
(6, 'N', '평범했어요.', '2025-06-07 15:30:00', 'B0012', 6),
(4, 'Y', '조금 지루했어요.', '2025-06-07 15:45:00', 'B0013', 7),
(10, 'N', '강력 추천합니다.', '2025-06-07 16:00:00', 'B0014', 8),
(2, 'Y', '스토리가 복잡했어요.', '2025-06-07 16:15:00', 'B0015', 9),

(8, 'N', '배우 연기가 좋았어요.', '2025-06-07 16:30:00', 'B0001', 10),
(6, 'Y', '긴장감이 넘쳤어요.', '2025-06-07 16:45:00', 'B0002', 11),
(4, 'N', '무난했어요.', '2025-06-07 17:00:00', 'B0003', 1),
(10, 'Y', '감동적이었어요.', '2025-06-07 17:15:00', 'B0004', 2),
(2, 'N', '기대 이하였어요.', '2025-06-07 17:30:00', 'B0005', 3),

(8, 'Y', '스토리가 재미있었어요.', '2025-06-07 17:45:00', 'B0006', 4),
(6, 'N', '보통이에요.', '2025-06-07 18:00:00', 'B0007', 5),
(4, 'Y', '아쉬움이 있었어요.', '2025-06-07 18:15:00', 'B0008', 6),
(10, 'N', '최고였어요.', '2025-06-07 18:30:00', 'B0009', 7),
(2, 'Y', '지루했어요.', '2025-06-07 18:45:00', 'B0010', 8),

(8, 'N', '흥미진진했어요.', '2025-06-07 19:00:00', 'B0011', 9),
(6, 'Y', '배우 연기가 좋았어요.', '2025-06-07 19:15:00', 'B0012', 10),
(4, 'N', '무난했어요.', '2025-06-07 19:30:00', 'B0013', 11),
(10, 'Y', '감동적인 작품이었어요.', '2025-06-07 19:45:00', 'B0014', 1),
(2, 'N', '기대 이하였어요.', '2025-06-07 20:00:00', 'B0015', 2),

(10, 'N', '스토리가 정말 감동적이었어요.', '2025-06-08 09:00:00', 'B0001', 3),
(8, 'Y', '중간에 반전이 있어서 흥미진진했어요.', '2025-06-08 09:15:00', 'B0002', 4),
(6, 'N', '배우들의 연기가 인상적이었어요.', '2025-06-08 09:30:00', 'B0003', 5),
(4, 'Y', '조금 지루한 부분도 있었지만 괜찮았어요.', '2025-06-08 09:45:00', 'B0004', 6),
(2, 'N', '기대에 못 미쳤어요.', '2025-06-08 10:00:00', 'B0005', 7),

(10, 'Y', '최고의 작품입니다!', '2025-06-08 10:15:00', 'B0006', 8),
(8, 'N', '음악과 영상미가 뛰어났어요.', '2025-06-08 10:30:00', 'B0007', 9),
(6, 'Y', '스토리가 조금 복잡했어요.', '2025-06-08 10:45:00', 'B0008', 10),
(4, 'N', '평범한 작품이었어요.', '2025-06-08 11:00:00', 'B0009', 11),
(2, 'Y', '아쉬움이 많았어요.', '2025-06-08 11:15:00', 'B0010', 1),

(10, 'N', '감동적인 결말이었어요.', '2025-06-08 11:30:00', 'B0011', 2),
(8, 'Y', '긴장감 넘치는 전개였어요.', '2025-06-08 11:45:00', 'B0012', 3),
(6, 'N', '무난하게 봤어요.', '2025-06-08 12:00:00', 'B0013', 4),
(4, 'Y', '스토리가 산만했어요.', '2025-06-08 12:15:00', 'B0014', 5),
(2, 'N', '다시 보고 싶지 않아요.', '2025-06-08 12:30:00', 'B0015', 6),

(10, 'Y', '재미있게 봤어요.', '2025-06-08 12:45:00', 'B0001', 7),
(8, 'N', '배우들의 연기가 최고였어요.', '2025-06-08 13:00:00', 'B0002', 8),
(6, 'Y', '스토리가 흥미진진했어요.', '2025-06-08 13:15:00', 'B0003', 9),
(4, 'N', '기대 이하였어요.', '2025-06-08 13:30:00', 'B0004', 10),
(2, 'Y', '별로였어요.', '2025-06-08 13:45:00', 'B0005', 11),

(10, 'N', '마음에 쏙 들었어요.', '2025-06-08 14:00:00', 'B0006', 1),
(8, 'Y', '중반부가 특히 재미있었어요.', '2025-06-08 14:15:00', 'B0007', 2),
(6, 'N', '보통이었어요.', '2025-06-08 14:30:00', 'B0008', 3),
(4, 'Y', '아쉬움이 있었어요.', '2025-06-08 14:45:00', 'B0009', 4),
(2, 'N', '별로였어요.', '2025-06-08 15:00:00', 'B0010', 5),

(10, 'Y', '최고였어요.', '2025-06-08 15:15:00', 'B0011', 6),
(8, 'N', '스토리가 재미있었어요.', '2025-06-08 15:30:00', 'B0012', 7),
(6, 'Y', '긴장감이 넘쳤어요.', '2025-06-08 15:45:00', 'B0013', 8),
(4, 'N', '무난했어요.', '2025-06-08 16:00:00', 'B0014', 9),
(2, 'Y', '기대 이하였어요.', '2025-06-08 16:15:00', 'B0015', 10),

(10, 'N', '감동적이었어요.', '2025-06-08 16:30:00', 'B0001', 11),
(8, 'Y', '배우 연기가 좋았어요.', '2025-06-08 16:45:00', 'B0002', 1),
(6, 'N', '스토리가 흥미로웠어요.', '2025-06-08 17:00:00', 'B0003', 2),
(4, 'Y', '조금 지루했어요.', '2025-06-08 17:15:00', 'B0004', 3),
(2, 'N', '별로였어요.', '2025-06-08 17:30:00', 'B0005', 4),

(10, 'Y', '강력 추천합니다!', '2025-06-08 17:45:00', 'B0006', 5),
(8, 'N', '음악과 영상미가 훌륭했어요.', '2025-06-08 18:00:00', 'B0007', 6),
(6, 'Y', '스토리가 복잡했어요.', '2025-06-08 18:15:00', 'B0008', 7),
(4, 'N', '평범한 작품이었어요.', '2025-06-08 18:30:00', 'B0009', 8),
(2, 'Y', '아쉬웠어요.', '2025-06-08 18:45:00', 'B0010', 9),

(10, 'N', '정말 재미있었어요.', '2025-06-08 19:00:00', 'B0011', 10),
(8, 'Y', '반전이 멋졌어요.', '2025-06-08 19:15:00', 'B0012', 11),
(6, 'N', '무난했어요.', '2025-06-08 19:30:00', 'B0013', 1),
(4, 'Y', '스토리가 좀 산만했어요.', '2025-06-08 19:45:00', 'B0014', 2),
(2, 'N', '기대에 못 미쳤어요.', '2025-06-08 20:00:00', 'B0015', 3);

-- 키워드 카테고리 테이블 삽입
DELETE FROM keyword_category;
DELETE FROM keyword;
INSERT INTO keyword_category (KC_CODE, KC_NAME) VALUES ('C01', '장르/배경');
INSERT INTO keyword_category (KC_CODE, KC_NAME) VALUES ('C02', '소재');
INSERT INTO keyword_category (KC_CODE, KC_NAME) VALUES ('C03', '남자주인공');
INSERT INTO keyword_category (KC_CODE, KC_NAME) VALUES ('C04', '여자주인공');
INSERT INTO keyword_category (KC_CODE, KC_NAME) VALUES ('C05', '기타');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0001', '현대물', 'C01');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0002', '역사물', 'C01');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0003', '동양풍', 'C01');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0004', '서양풍', 'C01');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0005', '가상세계', 'C01');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0006', '판타지', 'C01');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0007', '무협', 'C01');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0008', '대체역사', 'C01');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0009', '캠퍼스', 'C01');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0010', '재벌가', 'C01');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0011', '궁중', 'C01');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0012', '학교', 'C01');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0013', '오피스', 'C01');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0014', '연예계', 'C01');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0015', '차원이동', 'C01');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0016', '회귀물', 'C01');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0017', '성장물', 'C01');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0018', '전쟁', 'C01');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0019', '복수극', 'C01');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0020', '서바이벌', 'C01');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0021', '계약', 'C02');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0022', '복수', 'C02');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0023', '음모', 'C02');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0024', '권모술수', 'C02');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0025', '정략결혼', 'C02');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0026', '암살', 'C02');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0027', '시한부', 'C02');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0028', '도플갱어', 'C02');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0029', '빙의', 'C02');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0030', '이세계', 'C02');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0031', '악역', 'C02');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0032', '출생의 비밀', 'C02');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0033', '타임루프', 'C02');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0034', '기억상실', 'C02');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0035', '예언', 'C02');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0036', '초능력', 'C02');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0037', '성장', 'C02');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0038', '착각', 'C02');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0039', '배틀', 'C02');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0040', '정체불명', 'C02');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0041', '냉혈한', 'C03');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0042', '재벌남', 'C03');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0043', '다정남', 'C03');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0044', '츤데레', 'C03');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0045', '절륜남', 'C03');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0046', '헌신남', 'C03');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0047', '계략남', 'C03');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0048', '무능남', 'C03');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0049', '검사', 'C03');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0050', '기사', 'C03');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0051', '황자', 'C03');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0052', '황제', 'C03');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0053', '강압남', 'C03');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0054', '집착남', 'C03');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0055', '초능력자', 'C03');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0056', '전사', 'C03');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0057', '천재', 'C03');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0058', '귀족남', 'C03');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0059', '선비', 'C03');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0060', '짐승남', 'C03');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0061', '현대인', 'C04');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0062', '걸크러시', 'C04');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0063', '똑똑한', 'C04');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0064', '엉뚱한', 'C04');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0065', '사이다녀', 'C04');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0066', '복수녀', 'C04');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0067', '빙의녀', 'C04');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0068', '악녀', 'C04');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0069', '힐링녀', 'C04');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0070', '강단있는', 'C04');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0071', '상처녀', 'C04');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0072', '순정녀', 'C04');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0073', '마법사', 'C04');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0074', '궁수', 'C04');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0075', '귀족녀', 'C04');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0076', '여왕', 'C04');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0077', '재벌녀', 'C04');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0078', '암살자', 'C04');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0079', '아이돌', 'C04');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0080', '연하녀', 'C04');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0081', '로맨스', 'C05');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0082', '하렘', 'C05');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0083', '역하렘', 'C05');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0084', 'BL', 'C05');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0085', 'GL', 'C05');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0086', '다공일수', 'C05');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0087', '다수공일수', 'C05');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0088', '일공일수', 'C05');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0089', '성인', 'C05');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0090', '수위높음', 'C05');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0091', '수위낮음', 'C05');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0092', '슬로우번', 'C05');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0093', '고수위', 'C05');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0094', '순정', 'C05');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0095', '삼각관계', 'C05');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0096', '힐링', 'C05');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0097', '입덕부정', 'C05');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0098', '가벼운', 'C05');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0099', '진지한', 'C05');
INSERT INTO keyword (KW_CODE, KW_NAME, KW_KC_CODE) VALUES ('K0100', '개그물', 'C05');


-- 연결 정보 초기화
-- 연결 정보 초기화
DELETE FROM book_keyword;

INSERT INTO book_keyword (BK_KW_CODE, BK_BO_CODE) VALUES ("K0042", "B0001");
INSERT INTO book_keyword (BK_KW_CODE, BK_BO_CODE) VALUES ("K0012", "B0001");
INSERT INTO book_keyword (BK_KW_CODE, BK_BO_CODE) VALUES ("K0036", "B0002");
INSERT INTO book_keyword (BK_KW_CODE, BK_BO_CODE) VALUES ("K0072", "B0002");
INSERT INTO book_keyword (BK_KW_CODE, BK_BO_CODE) VALUES ("K0099", "B0003");
INSERT INTO book_keyword (BK_KW_CODE, BK_BO_CODE) VALUES ("K0090", "B0003");
INSERT INTO book_keyword (BK_KW_CODE, BK_BO_CODE) VALUES ("K0023", "B0004");
INSERT INTO book_keyword (BK_KW_CODE, BK_BO_CODE) VALUES ("K0031", "B0004");
INSERT INTO book_keyword (BK_KW_CODE, BK_BO_CODE) VALUES ("K0040", "B0005");
INSERT INTO book_keyword (BK_KW_CODE, BK_BO_CODE) VALUES ("K0055", "B0005");
INSERT INTO book_keyword (BK_KW_CODE, BK_BO_CODE) VALUES ("K0012", "B0005");
INSERT INTO book_keyword (BK_KW_CODE, BK_BO_CODE) VALUES ("K0052", "B0006");
INSERT INTO book_keyword (BK_KW_CODE, BK_BO_CODE) VALUES ("K0031", "B0006");
INSERT INTO book_keyword (BK_KW_CODE, BK_BO_CODE) VALUES ("K0022", "B0006");
INSERT INTO book_keyword (BK_KW_CODE, BK_BO_CODE) VALUES ("K0002", "B0007");
INSERT INTO book_keyword (BK_KW_CODE, BK_BO_CODE) VALUES ("K0079", "B0007");
INSERT INTO book_keyword (BK_KW_CODE, BK_BO_CODE) VALUES ("K0060", "B0007");
INSERT INTO book_keyword (BK_KW_CODE, BK_BO_CODE) VALUES ("K0014", "B0008");
INSERT INTO book_keyword (BK_KW_CODE, BK_BO_CODE) VALUES ("K0048", "B0008");
INSERT INTO book_keyword (BK_KW_CODE, BK_BO_CODE) VALUES ("K0081", "B0008");
INSERT INTO book_keyword (BK_KW_CODE, BK_BO_CODE) VALUES ("K0041", "B0009");
INSERT INTO book_keyword (BK_KW_CODE, BK_BO_CODE) VALUES ("K0014", "B0009");
INSERT INTO book_keyword (BK_KW_CODE, BK_BO_CODE) VALUES ("K0005", "B0010");
INSERT INTO book_keyword (BK_KW_CODE, BK_BO_CODE) VALUES ("K0100", "B0010");
INSERT INTO book_keyword (BK_KW_CODE, BK_BO_CODE) VALUES ("K0039", "B0010");
INSERT INTO book_keyword (BK_KW_CODE, BK_BO_CODE) VALUES ("K0022", "B0011");
INSERT INTO book_keyword (BK_KW_CODE, BK_BO_CODE) VALUES ("K0048", "B0011");
INSERT INTO book_keyword (BK_KW_CODE, BK_BO_CODE) VALUES ("K0032", "B0011");
INSERT INTO book_keyword (BK_KW_CODE, BK_BO_CODE) VALUES ("K0046", "B0012");
INSERT INTO book_keyword (BK_KW_CODE, BK_BO_CODE) VALUES ("K0012", "B0012");
INSERT INTO book_keyword (BK_KW_CODE, BK_BO_CODE) VALUES ("K0098", "B0013");
INSERT INTO book_keyword (BK_KW_CODE, BK_BO_CODE) VALUES ("K0041", "B0013");
INSERT INTO book_keyword (BK_KW_CODE, BK_BO_CODE) VALUES ("K0043", "B0013");
INSERT INTO book_keyword (BK_KW_CODE, BK_BO_CODE) VALUES ("K0019", "B0014");
INSERT INTO book_keyword (BK_KW_CODE, BK_BO_CODE) VALUES ("K0046", "B0014");
INSERT INTO book_keyword (BK_KW_CODE, BK_BO_CODE) VALUES ("K0013", "B0014");
INSERT INTO book_keyword (BK_KW_CODE, BK_BO_CODE) VALUES ("K0027", "B0015");
INSERT INTO book_keyword (BK_KW_CODE, BK_BO_CODE) VALUES ("K0068", "B0015");
INSERT INTO book_keyword (BK_KW_CODE, BK_BO_CODE) VALUES ("K0091", "B0015");
INSERT INTO book_keyword (BK_KW_CODE, BK_BO_CODE) VALUES ("K0098", "B0016");
INSERT INTO book_keyword (BK_KW_CODE, BK_BO_CODE) VALUES ("K0023", "B0016");
INSERT INTO book_keyword (BK_KW_CODE, BK_BO_CODE) VALUES ("K0056", "B0017");
INSERT INTO book_keyword (BK_KW_CODE, BK_BO_CODE) VALUES ("K0054", "B0017");
INSERT INTO book_keyword (BK_KW_CODE, BK_BO_CODE) VALUES ("K0092", "B0017");
INSERT INTO book_keyword (BK_KW_CODE, BK_BO_CODE) VALUES ("K0057", "B0018");
INSERT INTO book_keyword (BK_KW_CODE, BK_BO_CODE) VALUES ("K0078", "B0018");
INSERT INTO book_keyword (BK_KW_CODE, BK_BO_CODE) VALUES ("K0021", "B0018");
INSERT INTO book_keyword (BK_KW_CODE, BK_BO_CODE) VALUES ("K0042", "B0019");
INSERT INTO book_keyword (BK_KW_CODE, BK_BO_CODE) VALUES ("K0077", "B0019");
INSERT INTO book_keyword (BK_KW_CODE, BK_BO_CODE) VALUES ("K0054", "B0019");

UPDATE book b
JOIN (
    SELECT rv_bo_code AS bo_code,
           COUNT(*) AS review_count,
           SUM(rv_rating) AS total_rating
    FROM review
    GROUP BY rv_bo_code
) r ON b.bo_code = r.bo_code
SET b.bo_review_count = r.review_count,
    b.bo_total_rating = r.total_rating;
    
UPDATE BOOK 
SET BO_WAIT_FOR_FREE = 'Y'
WHERE BO_CODE IN (
  'B0001', 'B0003', 'B0005', 'B0007', 'B0009',
  'B0011', 'B0013', 'B0015', 'B0017'
);

INSERT INTO ATTENDANCE_REWARD (AR_TYPE, AR_POINT, AR_PROBABILITY, AR_MESSAGE) VALUES
('COMMON', 100, 60, '기본 출석 보상입니다.'),
('BONUS', 300, 25, '보너스 포인트 당첨!'),
('RARE', 500, 10, '레어 보상! 대박!'),
('JACKPOT', 1000, 5, '잭팟! 오늘 당신은 행운의 주인공!');
SET SQL_SAFE_UPDATES = 1;