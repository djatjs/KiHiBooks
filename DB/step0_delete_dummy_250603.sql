
-- 0. 종속 테이블부터 삭제
DELETE FROM REVIEW;
DELETE FROM book_keyword;
DELETE FROM EPISODE;

-- 1. BOOK 관련 테이블
DELETE FROM BOOK;

-- 2. 참조된 외래키 테이블
DELETE FROM AUTHOR;
DELETE FROM PUBLISHER_ID;
DELETE FROM PUBLISHER;

-- 3. 키워드 관련
-- DELETE FROM keyword;
-- DELETE FROM keyword_category;

 -- 4. 출석체크 관련 
DELETE FROM ATTENDANCE;

-- 5. 마지막으로 USER
DELETE FROM USER;

