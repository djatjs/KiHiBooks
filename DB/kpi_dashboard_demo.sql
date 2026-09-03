-- KiHiBooks 출판사 KPI 대시보드 데모 데이터
-- 대상: 기본 더미 데이터의 은하출판사(P001)
-- 특징: 실행일을 기준으로 최근 30일과 이전 30일 데이터를 생성합니다.
-- 재실행: KPIDEMO 주문과 [KPI_DEMO] 리뷰만 지우고 다시 생성합니다.

USE KIHIBOOKS;

DELIMITER $$

DROP PROCEDURE IF EXISTS seed_kpi_dashboard_demo$$

CREATE PROCEDURE seed_kpi_dashboard_demo()
BEGIN
    DECLARE v_user1 INT;
    DECLARE v_user2 INT;
    DECLARE v_user3 INT;
    DECLARE v_user4 INT;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    SELECT
        MAX(CASE WHEN UR_EMAIL = 'user1@example.com' THEN UR_NUM END),
        MAX(CASE WHEN UR_EMAIL = 'user4@example.com' THEN UR_NUM END),
        MAX(CASE WHEN UR_EMAIL = 'user5@example.com' THEN UR_NUM END),
        MAX(CASE WHEN UR_EMAIL = 'user6@example.com' THEN UR_NUM END)
    INTO v_user1, v_user2, v_user3, v_user4
    FROM `user`
    WHERE UR_EMAIL IN (
        'user1@example.com', 'user4@example.com',
        'user5@example.com', 'user6@example.com'
    );

    IF v_user1 IS NULL OR v_user2 IS NULL OR v_user3 IS NULL OR v_user4 IS NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'KPI 데모용 기본 사용자 데이터가 없습니다. step1_2_3_user_author_cate.sql을 먼저 적용하세요.';
    END IF;

    IF (
        SELECT COUNT(*)
        FROM episode
        WHERE EP_CODE IN (
            'P00111001001', 'P00111001002', 'P00111001003',
            'P00112001001', 'P00112001002', 'P00112001003',
            'P00121006001', 'P00121006002', 'P00121006003',
            'P00122006001', 'P00122006002', 'P00122006003',
            'P00123006001', 'P00123006002'
        )
    ) < 14 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'KPI 데모용 P001 회차가 없습니다. step7_episode_dummy.sql을 먼저 적용하세요.';
    END IF;

    START TRANSACTION;

    -- 이 파일이 생성한 데이터만 정리합니다.
    DELETE FROM buy_list WHERE BL_ID LIKE 'KPIDEMO%';
    DELETE FROM `order` WHERE OD_ID LIKE 'KPIDEMO%';
    DELETE FROM review WHERE RV_CONTENT LIKE '[KPI_DEMO]%';

    -- 최근 30일: 주문 8건, 유료 회차 17건, 구매 독자 4명, 판매액 1,700원
    INSERT INTO `order` (
        OD_ID, OD_TOTAL_AMOUNT, OD_USE_POINT, OD_FINAL_AMOUNT,
        OD_METHOD, OD_CREATED_AT, OD_STATUS, OD_PAID_AT, OD_UR_NUM
    ) VALUES
    ('KPIDEMO000001', 300, 0, 300, 'TOSS',  DATE_SUB(NOW(), INTERVAL 1 DAY),  'PAID', DATE_SUB(NOW(), INTERVAL 1 DAY),  v_user1),
    ('KPIDEMO000002', 200, 0, 200, 'KAKAO', DATE_SUB(NOW(), INTERVAL 3 DAY),  'PAID', DATE_SUB(NOW(), INTERVAL 3 DAY),  v_user2),
    ('KPIDEMO000003', 300, 0, 300, 'POINT', DATE_SUB(NOW(), INTERVAL 5 DAY),  'PAID', DATE_SUB(NOW(), INTERVAL 5 DAY),  v_user3),
    ('KPIDEMO000004', 100, 0, 100, 'TOSS',  DATE_SUB(NOW(), INTERVAL 8 DAY),  'PAID', DATE_SUB(NOW(), INTERVAL 8 DAY),  v_user4),
    ('KPIDEMO000005', 200, 0, 200, 'POINT', DATE_SUB(NOW(), INTERVAL 12 DAY), 'PAID', DATE_SUB(NOW(), INTERVAL 12 DAY), v_user1),
    ('KPIDEMO000006', 100, 0, 100, 'KAKAO', DATE_SUB(NOW(), INTERVAL 18 DAY), 'PAID', DATE_SUB(NOW(), INTERVAL 18 DAY), v_user2),
    ('KPIDEMO000007', 300, 0, 300, 'TOSS',  DATE_SUB(NOW(), INTERVAL 24 DAY), 'PAID', DATE_SUB(NOW(), INTERVAL 24 DAY), v_user3),
    ('KPIDEMO000008', 200, 0, 200, 'POINT', DATE_SUB(NOW(), INTERVAL 28 DAY), 'PAID', DATE_SUB(NOW(), INTERVAL 28 DAY), v_user4),

    -- 이전 30일: 주문 4건, 유료 회차 6건, 구매 독자 3명, 판매액 600원
    ('KPIDEMO000009', 200, 0, 200, 'TOSS',  DATE_SUB(NOW(), INTERVAL 34 DAY), 'PAID', DATE_SUB(NOW(), INTERVAL 34 DAY), v_user1),
    ('KPIDEMO000010', 100, 0, 100, 'POINT', DATE_SUB(NOW(), INTERVAL 40 DAY), 'PAID', DATE_SUB(NOW(), INTERVAL 40 DAY), v_user2),
    ('KPIDEMO000011', 200, 0, 200, 'KAKAO', DATE_SUB(NOW(), INTERVAL 48 DAY), 'PAID', DATE_SUB(NOW(), INTERVAL 48 DAY), v_user3),
    ('KPIDEMO000012', 100, 0, 100, 'TOSS',  DATE_SUB(NOW(), INTERVAL 55 DAY), 'PAID', DATE_SUB(NOW(), INTERVAL 55 DAY), v_user1);

    INSERT INTO buy_list (BL_ID, BL_DATE, BL_EP_CODE, BL_UR_NUM) VALUES
    ('KPIDEMO000001', DATE_SUB(NOW(), INTERVAL 1 DAY),  'P00111001001', v_user1),
    ('KPIDEMO000001', DATE_SUB(NOW(), INTERVAL 1 DAY),  'P00111001002', v_user1),
    ('KPIDEMO000001', DATE_SUB(NOW(), INTERVAL 1 DAY),  'P00111001003', v_user1),
    ('KPIDEMO000002', DATE_SUB(NOW(), INTERVAL 3 DAY),  'P00112001001', v_user2),
    ('KPIDEMO000002', DATE_SUB(NOW(), INTERVAL 3 DAY),  'P00112001002', v_user2),
    ('KPIDEMO000003', DATE_SUB(NOW(), INTERVAL 5 DAY),  'P00111001001', v_user3),
    ('KPIDEMO000003', DATE_SUB(NOW(), INTERVAL 5 DAY),  'P00121006001', v_user3),
    ('KPIDEMO000003', DATE_SUB(NOW(), INTERVAL 5 DAY),  'P00121006002', v_user3),
    ('KPIDEMO000004', DATE_SUB(NOW(), INTERVAL 8 DAY),  'P00122006001', v_user4),
    ('KPIDEMO000005', DATE_SUB(NOW(), INTERVAL 12 DAY), 'P00112001002', v_user1),
    ('KPIDEMO000005', DATE_SUB(NOW(), INTERVAL 12 DAY), 'P00112001003', v_user1),
    ('KPIDEMO000006', DATE_SUB(NOW(), INTERVAL 18 DAY), 'P00123006001', v_user2),
    ('KPIDEMO000007', DATE_SUB(NOW(), INTERVAL 24 DAY), 'P00121006001', v_user3),
    ('KPIDEMO000007', DATE_SUB(NOW(), INTERVAL 24 DAY), 'P00121006002', v_user3),
    ('KPIDEMO000007', DATE_SUB(NOW(), INTERVAL 24 DAY), 'P00121006003', v_user3),
    ('KPIDEMO000008', DATE_SUB(NOW(), INTERVAL 28 DAY), 'P00122006002', v_user4),
    ('KPIDEMO000008', DATE_SUB(NOW(), INTERVAL 28 DAY), 'P00123006002', v_user4),

    ('KPIDEMO000009', DATE_SUB(NOW(), INTERVAL 34 DAY), 'P00111001001', v_user1),
    ('KPIDEMO000009', DATE_SUB(NOW(), INTERVAL 34 DAY), 'P00112001001', v_user1),
    ('KPIDEMO000010', DATE_SUB(NOW(), INTERVAL 40 DAY), 'P00121006001', v_user2),
    ('KPIDEMO000011', DATE_SUB(NOW(), INTERVAL 48 DAY), 'P00122006001', v_user3),
    ('KPIDEMO000011', DATE_SUB(NOW(), INTERVAL 48 DAY), 'P00122006002', v_user3),
    ('KPIDEMO000012', DATE_SUB(NOW(), INTERVAL 55 DAY), 'P00123006001', v_user1);

    -- 최근 30일 리뷰 6개(평균 4.7), 이전 30일 리뷰 3개(평균 3.7)
    INSERT INTO review (
        RV_RATING, RV_SPOILER, RV_CONTENT, RV_DATE, RV_DEL, RV_BO_CODE, RV_UR_NUM
    ) VALUES
    (5, 'N', '[KPI_DEMO] 다음 화가 기다려져요.',       DATE_SUB(NOW(), INTERVAL 2 DAY),  'N', 'P00111001', v_user1),
    (5, 'N', '[KPI_DEMO] 전개가 빠르고 재미있어요.',     DATE_SUB(NOW(), INTERVAL 4 DAY),  'N', 'P00112001', v_user2),
    (4, 'N', '[KPI_DEMO] 캐릭터가 매력적입니다.',        DATE_SUB(NOW(), INTERVAL 7 DAY),  'N', 'P00121006', v_user3),
    (5, 'N', '[KPI_DEMO] 최근 본 작품 중 가장 좋았어요.', DATE_SUB(NOW(), INTERVAL 11 DAY), 'N', 'P00122006', v_user4),
    (4, 'N', '[KPI_DEMO] 다음 회차도 구매할게요.',       DATE_SUB(NOW(), INTERVAL 16 DAY), 'N', 'P00123006', v_user1),
    (5, 'N', '[KPI_DEMO] 몰입감이 좋습니다.',            DATE_SUB(NOW(), INTERVAL 25 DAY), 'N', 'P00111001', v_user2),
    (3, 'N', '[KPI_DEMO] 초반 전개가 조금 느렸어요.',     DATE_SUB(NOW(), INTERVAL 35 DAY), 'N', 'P00111001', v_user1),
    (4, 'N', '[KPI_DEMO] 설정이 흥미롭습니다.',          DATE_SUB(NOW(), INTERVAL 43 DAY), 'N', 'P00121006', v_user2),
    (4, 'N', '[KPI_DEMO] 무난하게 읽기 좋았어요.',       DATE_SUB(NOW(), INTERVAL 52 DAY), 'N', 'P00122006', v_user3);

    -- 작품 상세 화면의 누적 리뷰 캐시도 실제 리뷰 데이터와 맞춥니다.
    UPDATE book b
    SET
        b.BO_REVIEW_COUNT = (
            SELECT COUNT(*) FROM review r
            WHERE r.RV_BO_CODE = b.BO_CODE AND r.RV_DEL = 'N'
        ),
        b.BO_TOTAL_RATING = (
            SELECT COALESCE(SUM(r.RV_RATING), 0) FROM review r
            WHERE r.RV_BO_CODE = b.BO_CODE AND r.RV_DEL = 'N'
        )
    WHERE b.BO_CODE IN (
        'P00111001', 'P00112001', 'P00121006', 'P00122006', 'P00123006'
    );

    COMMIT;
END$$

CALL seed_kpi_dashboard_demo()$$
DROP PROCEDURE seed_kpi_dashboard_demo$$

DELIMITER ;

-- 적용 결과 요약
SELECT
    COUNT(DISTINCT o.OD_ID) AS demo_order_count,
    COUNT(bl.BL_NUM) AS demo_paid_episode_count,
    SUM(e.EP_PRICE) AS demo_sales_amount,
    COUNT(DISTINCT bl.BL_UR_NUM) AS demo_buyer_count
FROM `order` o
JOIN buy_list bl ON bl.BL_ID = o.OD_ID
JOIN episode e ON e.EP_CODE = bl.BL_EP_CODE
WHERE o.OD_ID LIKE 'KPIDEMO%'
  AND o.OD_STATUS = 'PAID';

SELECT
    COUNT(*) AS demo_review_count,
    ROUND(AVG(RV_RATING), 1) AS demo_average_rating
FROM review
WHERE RV_CONTENT LIKE '[KPI_DEMO]%';
