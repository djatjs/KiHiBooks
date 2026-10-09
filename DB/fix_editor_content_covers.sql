-- 더미 회차 데이터가 공용 테스트 표지를 가리키는 문제를 보정한다.
-- 도서별 표지는 C:/uploads/P00633001/covers 아래의 실제 파일명과 일치한다.
UPDATE episode
SET ep_cover_img = CASE ep_bo_code
    WHEN 'P00223002' THEN 'P00633001/covers/가상연애.jpg'
    WHEN 'P00212007' THEN 'P00633001/covers/그림자 궁녀.jpg'
    WHEN 'P00222002' THEN 'P00633001/covers/서쪽왕국의비밀.jpg'
    WHEN 'P00212002' THEN 'P00633001/covers/조선의 별.jpg'
    WHEN 'P00221007' THEN 'P00633001/covers/황혼의여우.jpg'
    WHEN 'P00333008' THEN 'P00633001/covers/강남마나헬스.jpg'
    WHEN 'P00311003' THEN 'P00633001/covers/그 겨울의 기억.jpg'
    WHEN 'P00335003' THEN 'P00633001/covers/그날의조선.jpg'
    WHEN 'P00336008' THEN 'P00633001/covers/그라운드의연인.jpg'
    WHEN 'P00312003' THEN 'P00633001/covers/달의 연인들.jpg'
    WHEN 'P00323008' THEN 'P00633001/covers/코딩된마음.jpg'
    WHEN 'P00421004' THEN 'P00633001/covers/검은달의나라.jpg'
    WHEN 'P00422004' THEN 'P00633001/covers/기사의맹세.jpg'
    WHEN 'P00436009' THEN 'P00633001/covers/라켓과하트.jpg'
    WHEN 'P00411004' THEN 'P00633001/covers/아직 사랑해.jpg'
    WHEN 'P00432004' THEN 'P00633001/covers/퓨전기사단.jpg'
    WHEN 'P00512005' THEN 'P00633001/covers/기방의 노래.jpg'
    WHEN 'P00534010' THEN 'P00633001/covers/루트이탈연애담.jpg'
    WHEN 'P00511005' THEN 'P00633001/covers/밤하늘의 약속.jpg'
    WHEN 'P00522005' THEN 'P00633001/covers/별의계보.jpg'
    WHEN 'P00521005' THEN 'P00633001/covers/비의신부.jpg'
    WHEN 'P00521010' THEN 'P00633001/covers/청운의길.jpg'
    WHEN 'P00131006' THEN 'P00633001/covers/고대의주문.jpg'
    WHEN 'P00112001' THEN 'P00633001/covers/궁의 그림자.jpg'
    WHEN 'P00111001' THEN 'P00633001/covers/너를 기다리며.jpg'
    WHEN 'P00135001' THEN 'P00633001/covers/대체황제.jpg'
    WHEN 'P00123001' THEN 'P00633001/covers/로그아웃금지.jpg'
    WHEN 'P00133001' THEN 'P00633001/covers/마법사는월세중.jpg'
    WHEN 'P00112006' THEN 'P00633001/covers/사라진 신부.jpg'
    WHEN 'P00136001' THEN 'P00633001/covers/이종격투러브.jpg'
    WHEN 'P00132001' THEN 'P00633001/covers/현대마법소녀.jpg'
    WHEN 'P00122001' THEN 'P00633001/covers/황제의명령.jpg'
    ELSE ep_cover_img
END
WHERE ep_bo_code IN (
    'P00223002', 'P00212007', 'P00222002', 'P00212002', 'P00221007',
    'P00333008', 'P00311003', 'P00335003', 'P00336008', 'P00312003', 'P00323008',
    'P00421004', 'P00422004', 'P00436009', 'P00411004', 'P00432004',
    'P00512005', 'P00534010', 'P00511005', 'P00522005', 'P00521005', 'P00521010',
    'P00131006', 'P00112001', 'P00111001', 'P00135001', 'P00123001', 'P00133001',
    'P00112006', 'P00136001', 'P00132001', 'P00122001'
);
